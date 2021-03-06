package cn.edu.bistu.workOrder.service.impl;

import cn.edu.bistu.user.dao.UserDao;
import cn.edu.bistu.approval.WorkOrderFinisherFactory;
import cn.edu.bistu.approval.dao.ApproverLogicDao;
import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MD5Utils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.constants.WorkOrderStatus;
import cn.edu.bistu.flow.dao.FlowDaoImpl;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.DaoResult;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.model.entity.FlowNodeApprover;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.model.vo.FlowNodeVo;
import cn.edu.bistu.model.vo.FlowVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.dao.WorkOrderDao;
import cn.edu.bistu.workOrder.dao.WorkOrderDaoImpl;
import cn.edu.bistu.workOrder.mapper.WorkOrderMapper;
import cn.edu.bistu.workOrder.service.ActualApproverFinalizer;
import cn.edu.bistu.workOrder.service.FlowNodeApproverDecider;
import cn.edu.bistu.workOrder.service.FlowNodeApproverDeciderFactory;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import cn.edu.bistu.wx.service.WxMiniApi;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class WorkOrderServiceImpl extends ServiceImpl<WorkOrderMapper, WorkOrder> implements WorkOrderService {
    @Autowired
    WxMiniApi wxMiniApi;

    @Qualifier("workOrderDaoImpl")
    @Autowired
    WorkOrderDao workOrderDao;

    @Autowired
    FlowDaoImpl flowDao;

    @Value("${attachmentDownloadApi}")
    String attachmentDownloadApi;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    UserDao userDao;

    @Autowired
    ApprovalService approvalService;

    @Autowired
    WorkOrderFinisherFactory workOrderFinisherFactory;

    @Autowired
    ApproverLogicDao approverLogicDao;

    @Autowired
    ActualApproverFinalizer actualApproverFinalizer;

    @Autowired
    FlowNodeApproverDeciderFactory flowNodeApproverDeciderFactory;

    FlowNodeApproverDecider flowNodeApproverDecider;


    @Override
    public ServiceResult listWorkOrder(WorkOrderVo workOrderVo, Page<WorkOrderVo> page) {
        DaoResult<Page<WorkOrderVo>> daoResultPage = workOrderDao.getWorkOrderPageByConditions(page, workOrderVo, "user");
        return new ServiceResultImpl<>(daoResultPage.getResult());
    }

    @Override
    public void revoke(Long workOrderId, Long initiator) {

        WorkOrderVo workOrderVo = workOrderDao.getOneWorkOrderById(workOrderId).getResult();

        //?????????????????????????????????????????????????????????????????????????????????
        if (!workOrderVo.getInitiatorId().equals(initiator)) {
            throw new ResultCodeException("user: " + initiator + " has no right",
                    ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //???????????????????????????????????????
        if (workOrderVo.getIsFinished().equals(1)) {
            throw new ResultCodeException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_FINISHED);
        }

        //?????????????????????????????????????????????
        if (workOrderVo.getIsExamined().equals(1)) {
            throw new ResultCodeException("workOrderId:" + workOrderId,
                    ResultCodeEnum.WORKORDER_BEEN_EXAMINED);
        }

        approvalService.workOrderFinish(workOrderFinisherFactory.getFinisher(
                        "notApprovalTypeV2"),
                workOrderVo,
                null,
                WorkOrderStatus.BEEN_WITHDRAWN,
                null);
    }

    @Override
    public ServiceResult<WorkOrderVo> detail(WorkOrder workOrder) {

        WorkOrder inspectWorkOrder = ((WorkOrderDaoImpl) workOrderDao).getWorkOrderMapper().selectOne(new QueryWrapper<WorkOrder>().select("id", "initiator_id").eq("id", workOrder.getId()));

        //???????????????
        if (inspectWorkOrder == null) {
            throw new ResultCodeException("workOrder id: " + workOrder.getId(), ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //???????????????????????????????????????????????????????????????
        if (!inspectWorkOrder.getInitiatorId().equals(workOrder.getInitiatorId())) {
            if (inspectWorkOrder.getActualApproverId() != null && !inspectWorkOrder.getActualApproverId().equals(workOrder.getInitiatorId())) {
                throw new ResultCodeException("workOrder id: " + workOrder.getId(), ResultCodeEnum.HAVE_NO_RIGHT);
            }
        }

        DaoResult<WorkOrderVo> daoResultPage = workOrderDao.getOneWorkOrderById(workOrder.getId());
        WorkOrderVo resultWorkOrderWithOutFlowInfo = daoResultPage.getResult();
        resultWorkOrderWithOutFlowInfo.setAttachment(null);

        //??????????????????
        FlowVo fullPreparedFlowOfResultWorkOrder = flowDao.getFullPreparedFlowByFlowId(resultWorkOrderWithOutFlowInfo.getFlowId()).getResult();

        for (FlowNodeVo oneFlowNodeOfResultWorkOrder : fullPreparedFlowOfResultWorkOrder.getFlowNodeList()) {
            Long approverId = oneFlowNodeOfResultWorkOrder.getApproverId();
            flowNodeApproverDecider = flowNodeApproverDeciderFactory.getApproverDecider(approverId);
            FlowNodeApprover flowNodeApproverOfResultWorkOrder = flowNodeApproverDecider.findAndSetFlowNodeApprover(oneFlowNodeOfResultWorkOrder);
            oneFlowNodeOfResultWorkOrder.setFlowNodeApprover(flowNodeApproverOfResultWorkOrder);
        }

        resultWorkOrderWithOutFlowInfo.setFlow(fullPreparedFlowOfResultWorkOrder);

        return new ServiceResultImpl<>(resultWorkOrderWithOutFlowInfo);
    }

    @Override
    @Transactional
    public ServiceResult submitWorkOrder(WorkOrderVo workOrderVo) {

        Long flowId = workOrderVo.getFlowId();
        QueryWrapper<FlowNode> flowNodeQueryWrapper = new QueryWrapper<>();

        flowNodeQueryWrapper.eq("flow_id", flowId).orderByAsc("node_order");
        List<FlowNode> flowNodeList = flowDao.getFlowNodeMapper().selectList(flowNodeQueryWrapper);

        FlowNode firstFlowNode = flowNodeList.get(0);

        //??????????????????????????????
        workOrderVo.setFlowNodeId(firstFlowNode.getId());//????????????????????????

        //????????????????????????????????????
        actualApproverFinalizer.decideActualApprover(workOrderVo, false);

        workOrderVo.setStatus(0);                           //????????????
        workOrderVo.setIsExamined(0);                       //??????????????????
        workOrderVo.setIsFinished(0);                       //????????????
        //????????????
        save(workOrderVo);

        return new ServiceResultImpl(workOrderVo);


        //????????????????????????????????????
        //UserVo userVo = userMapper.getOneById(workOrder.getId());
        //String openId = userVo.getOpenId();
        //wxMiniApi.sendSubscribeMsg(openId);
    }

    @Override
    public ServiceResult getAllWorkOrders(Page<WorkOrderVo> page, WorkOrderVo workOrderVo) {
        DaoResult<Page<WorkOrderVo>> workOrderPageByConditions = workOrderDao.getWorkOrderPageByConditions(page, workOrderVo, "admin");
        Page<WorkOrderVo> result = workOrderPageByConditions.getResult();
        return new ServiceResultImpl<>(result);
    }

    @Override
    public void deleteAttachmentByWorkOrderId(Long workOrderId) {
        ((WorkOrderDaoImpl) workOrderDao).deleteWorkOrderAttachment(workOrderId);
    }

}
