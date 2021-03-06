package cn.edu.bistu.user.rest;

import cn.edu.bistu.user.Service.UserService;
import cn.edu.bistu.common.rest.BaseController;
import cn.edu.bistu.common.utils.Pagination;
import cn.edu.bistu.model.common.result.Result;
import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.common.validation.ConditionQuery;
import cn.edu.bistu.model.entity.auth.User;
import cn.edu.bistu.model.vo.PageVo;
import cn.edu.bistu.model.vo.UserVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@RestController
@Slf4j
@CrossOrigin
public class UserController extends BaseController{

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public Result getAllUsers(PageVo pageVo,
                              @Validated({ConditionQuery.class}) UserVo userVo) {
        pageVo = Pagination.setDefault(pageVo.getCurrent(), pageVo.getSize());
        Page<UserVo> page = new Page<>(pageVo.getCurrent(), pageVo.getSize());
        //获取结果
        ServiceResult<UserVo> serviceResult = userService.getAllUsers(page, userVo);
        return Result.ok(serviceResult.getServiceResult());
    }

    @PutMapping("/lock/{id}/{status}")
    public Result lock(
            @PathVariable("id") @NotNull Long id,
            @PathVariable("status") @Pattern(regexp = "[0|1]") Integer status,
            HttpServletResponse resp) {

        User user = new User();
        user.setIsLock(status);
        user.setId(id);
        userService.lock(user);

        return Result.ok();
    }

    @PutMapping("/users")
    public Result update(
           @RequestBody @Validated UserVo userVo) {
        ServiceResult<UserVo> serviceResult = userService.updateUser(userVo);
        return Result.ok(serviceResult.getServiceResult());
    }

    @PostMapping("/admin/user/promote/{userId}")
    public Result promote(
           @NotNull @PathVariable(name="userId") Long userId) {
        userService.promote(userId);
        return Result.ok();
    }

    @DeleteMapping("/admin/user/demote/{userId}")
    public Result demote(
            @NotNull @PathVariable(name="userId") Long userId) {
        userService.demote(userId);
        return Result.ok();
    }


    @GetMapping("/user/{studentJobId}")
    public Result searchByStudentJobId(
            @NotNull @PathVariable(name="studentJobId") String studentJobId) {
        return Result.ok(userService.searchOneUserByStudentJobId(studentJobId).getServiceResult());
    }


}
