package cn.edu.bistu.utils.rest;

import cn.edu.bistu.auth.WeChatUtil;
import cn.edu.bistu.dept.service.DeptService;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.utils.service.CommonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class UtilsController {

    @Autowired
    DeptService deptService;

    @Autowired
    CommonInfoService commonInfoService;

    @GetMapping("/utils/commonInfo")
    public Result commonInfo() {
        ServiceResult commonInfo = commonInfoService.getCommonInfo();
        return Result.ok( commonInfo.getServiceResult());
    }

    @GetMapping("/utils/wxLoginJsFile")
    public Result wxLoginJsFile(String url) {
        System.out.println(url);
        String str = WeChatUtil.httpRequest(url, "GET", null);
        return Result.ok(str);
    }



}
