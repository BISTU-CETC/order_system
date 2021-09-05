package cn.edu.bistu.approval.rest;

import cn.edu.bistu.approval.service.ApprovalService;
import cn.edu.bistu.common.MapService;
import cn.edu.bistu.common.config.ValidationWrapper;
import cn.edu.bistu.common.exception.ParameterMissingException;
import cn.edu.bistu.common.exception.ParameterRedundentException;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.flow.service.FlowNodeService;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.ApprovalRecord;
import cn.edu.bistu.model.entity.FlowNode;
import cn.edu.bistu.workOrder.service.WorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
public class ApprovalController {

    @Autowired
    ApprovalService approvalService;

    @Autowired
    FlowNodeService flowNodeService;

    @Autowired
    WorkOrderService workOrderService;

    @Autowired
    ValidationWrapper globalValidator;

    @PostMapping("/approval/pass")
    public Result pass(@RequestBody ApprovalRecord approvalRecord,
                       HttpServletRequest req) {
        //获取审批者id
        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long approverId = mapService.getVal("id", Long.class);

        //生成审批记录
        approvalRecord.setApproverId(approverId);
        Long workOrderId = approvalRecord.getWorkOrderId();

        approvalService.pass(approvalRecord);

        return Result.ok();
    }

    @PostMapping("/approval/reject")
    public Result reject(@RequestBody ApprovalRecord approvalRecord,
                         HttpServletRequest req) {

        try {
            globalValidator.setRequiredPropsName(new String[]{"workOrderId"});
            globalValidator.setOptionalPropsName(new String[]{"comment"});
            globalValidator.checkParamIntegrity(approvalRecord);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            globalValidator.setPropsNameNull();
        }

        MapService mapService = (MapService) req.getAttribute("userInfo");
        Long approverId = mapService.getVal("id", Long.class);

        approvalRecord.setApproverId(approverId);

        approvalService.reject(approvalRecord);

        return Result.ok();
    }





}
