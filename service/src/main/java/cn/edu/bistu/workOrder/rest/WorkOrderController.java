package cn.edu.bistu.workOrder.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MD5Utils;
import cn.edu.bistu.common.exception.ResultCodeException;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.result.ServiceResultImpl;
import cn.edu.bistu.model.common.validation.Insert;
import cn.edu.bistu.model.entity.WorkOrderHistory;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.vo.WorkOrderHistoryVo;
import cn.edu.bistu.model.vo.WorkOrderVo;
import cn.edu.bistu.workOrder.exception.AttachmentNotExistsException;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.utils.MimeTypeUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.entity.WorkOrder;
import cn.edu.bistu.workOrder.service.WorkOrderHistoryService;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
@RestController
@Validated
@CrossOrigin
public class WorkOrderController extends BaseController {

    @Autowired
    ApprovalService approvalRecordService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    WorkOrderHistoryService workOrderHistorService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    ValidationWrapper globalValidator;

    /**
     * ????????????????????????????????????????????????????????????
     * ?????????size(10)???current(1)???title(NULL)
     */
    @GetMapping("/workOrders")
    public Result list(PageVo pageVo,
                       WorkOrderVo workOrderVo,
                       HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());

        if (workOrderVo.getTitle() == null) {
            workOrderVo.setTitle("");
        }

        workOrderVo.setInitiatorId(getVisitorId(req));

        Page<WorkOrderVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());

        //????????????
        ServiceResult serviceResult = workOrderService.listWorkOrder(workOrderVo, page);
        return Result.ok(serviceResult.getServiceResult());
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     * ?????????size(10)???current(1)???title("")
     */
    @GetMapping("/workOrder/histories")
    public Result history(PageVo pageVo,
                          WorkOrderHistoryVo workOrderHistoryVo,
                          HttpServletRequest req) {

        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());

        if (workOrderHistoryVo.getWorkOrderVo() == null) {
            workOrderHistoryVo.setWorkOrderVo(new WorkOrderVo());
        }

        if (StringUtils.isEmpty(workOrderHistoryVo.getWorkOrderVo().getTitle())) {
            workOrderHistoryVo.getWorkOrderVo().setTitle("");
        }

        //??????????????????
        workOrderHistoryVo.getWorkOrderVo().setInitiatorId(getVisitorId(req));

        //??????????????????
        Page<WorkOrderHistoryVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());

        ServiceResult result = workOrderHistorService.listWorkOrderHistory(workOrderHistoryVo, page);

        return Result.ok(result.getServiceResult());
    }

    /**
     * ?????????????????????????????????
     * ??????????????????
     *
     * @return
     */
    @GetMapping("/workOrder/attachment/{workOrderId}/{attachmentDownloadId}")
    public void downloadAttachment(
            @PathVariable("workOrderId") @NotNull Long workOrderId,
            @PathVariable("attachmentDownloadId") @NotNull String attachmentDownloadId,
            HttpServletRequest req, HttpServletResponse resp) throws IOException {

        //????????????
        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (workOrder == null) {
            throw new ResultCodeException("workOrderId: " + workOrderId, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        ////?????????????????????????????????????????????????????????????????????????????????????????????
        //Long visitorId = getVisitorId(req);
        //if (!visitorId.equals(workOrder.getInitiatorId()) && !isAdmin(req)) {
        //    throw new ResultCodeException("visitor id: " + visitorId + "has not right", ResultCodeEnum.HAVE_NO_RIGHT);
        //}

        byte[] attachmentBytes = workOrder.getAttachment();

        //log.debug("" + attachmentBytes.length);

        if (attachmentBytes == null) {
            throw new AttachmentNotExistsException(null, ResultCodeEnum.ATTACHMENT_NOT_EXISTS);
        }

        String attachmentDownloadIdFromDataBase = workOrderService.getOne(new QueryWrapper<WorkOrder>().select("attachment_download_id")
                .eq("id", workOrder.getId())).getAttachmentDownloadId();

        if (attachmentDownloadId.equals(attachmentDownloadIdFromDataBase)) {
            //???????????????MIME??????
            String mimeType = MimeTypeUtils.getType(workOrder.getAttachmentName());
            //???????????????MIME??????
            resp.setContentType(mimeType);
            log.debug("mimeType:" + mimeType);

            //?????????????????????????????????????????????
            resp.setHeader("Content-Disposition", "downloadAttachment; fileName=" + URLEncoder.encode(workOrder.getAttachmentName(), "UTF-8"));
            log.debug("attachmentName:" + workOrder.getAttachmentName());

            resp.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

            //???????????????????????????http????????????
            ServletOutputStream out = resp.getOutputStream();
            out.write(attachmentBytes, 0, attachmentBytes.length);
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????
     * ???????????????????????????
     *
     * @return ?????????????????????????????????????????????102
     */
    @PutMapping("/workOrder/attachment/{workOrderId}")
    public Result uploadAttachment(
            @RequestPart("attachment") MultipartFile attachment
            , @PathVariable("workOrderId") @NotNull Long workOrderId
            , HttpServletRequest req
    ) throws IOException {


        WorkOrder workOrder = workOrderService.getById(workOrderId);

        //???????????????
        if (workOrder == null) {
            log.debug("workOrderId???" + ResultCodeEnum.WORKORDER_NOT_EXISTS.toString());
            return Result.build(null, ResultCodeEnum.WORKORDER_NOT_EXISTS);
        }

        //?????????????????????????????????????????????????????????????????????????????????????????????
        Long visitorId = getVisitorId(req);
        if (!visitorId.equals(workOrder.getInitiatorId()) && !isAdmin(req)) {
            throw new ResultCodeException("visitor id: " + visitorId + "has not right", ResultCodeEnum.HAVE_NO_RIGHT);
        }

        //????????????
        if (attachment.getSize() != 0 && !attachment.getOriginalFilename().equals("")) {
            byte[] bytes = attachment.getBytes();
            workOrder = new WorkOrder();
            workOrder.setId(workOrderId);
            workOrder.setAttachment(bytes);
            workOrder.setAttachmentName(attachment.getOriginalFilename());
            workOrder.setAttachmentSize(String.format("%.2f", attachment.getSize() / 1024.0));
            //??????????????????id
            String rowDownloadId = System.currentTimeMillis() + workOrder.getId() + workOrder.getAttachmentName();
            String md5DownloadId = MD5Utils.MD5(rowDownloadId);
            workOrder.setAttachmentDownloadId(md5DownloadId);
            workOrderService.updateById(workOrder);
            return Result.ok();
        } else {
            return Result.build(null, ResultCodeEnum.FRONT_DATA_MISSING);
        }

    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    @PostMapping("/workOrder")
    public Result submitWorkOrder(
            @Validated(Insert.class) @RequestBody WorkOrderVo workOrderVo,
            HttpServletRequest req) {
        //????????????????????????id
        Long visitorId = getVisitorId(req);
        workOrderVo.setInitiatorId(visitorId);
        ServiceResult result = workOrderService.submitWorkOrder(workOrderVo);
        return Result.ok(result.getServiceResult());
    }


    /**
     * ????????????
     * <p>
     *
     * @param req
     *
     * @return ?????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @PutMapping("/workOrder/revoke")
    public Result revoke(@NotNull Long workOrderId,
                         HttpServletRequest req) {
        Long approverId = getVisitorId(req);

        workOrderService.revoke(workOrderId, approverId);

        return Result.ok();
    }

    /**
     * ??????????????????
     * <p>
     * //* @param json ???????????????id???
     * workOrderId      ??????id
     *
     * @return
     */
    @GetMapping("/workOrder/detail")
    public Result detail(@NotNull Long workOrderId, HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        Long visitorId = getVisitorId(req);
        WorkOrder workOrder = new WorkOrder();
        workOrder.setInitiatorId(visitorId);
        workOrder.setId(workOrderId);

        ServiceResult<WorkOrderVo> detail = workOrderService.detail(workOrder);

        return Result.ok(detail.getServiceResult());
    }

    /**
     * ????????????????????????
     */
    @GetMapping("/workOrder/history/detail")
    public Result historyDetail(@NotNull Long workOrderHistoryId, HttpServletRequest req) throws NoSuchFieldException, IllegalAccessException {

        WorkOrderHistory workOrderHistory = new WorkOrderHistory();
        workOrderHistory.setId(workOrderHistoryId);

        //????????????
        ServiceResult<WorkOrderHistoryVo> serviceResult = workOrderHistorService.detail(workOrderHistory, getVisitorId(req));
        return Result.ok(serviceResult.getServiceResult());
    }

    @DeleteMapping("/workOrder/attachment/{id}")
    public Result deleteAttachment(HttpServletRequest req
            , @NotNull @PathVariable("id") Long id) {

        WorkOrder workOrder = workOrderService.getById(id);

        //?????????????????????????????????????????????????????????????????????????????????????????????
        Long visitorId = getVisitorId(req);
        if (!visitorId.equals(workOrder.getInitiatorId()) && !isAdmin(req)) {
            throw new ResultCodeException("visitor id: " + visitorId + "has not right", ResultCodeEnum.HAVE_NO_RIGHT);
        }

        workOrderService.deleteAttachmentByWorkOrderId(id);
        return Result.ok();
    }
}
