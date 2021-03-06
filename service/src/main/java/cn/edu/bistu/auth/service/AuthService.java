package cn.edu.bistu.auth.service;

import cn.edu.bistu.model.common.result.ServiceResult;
import cn.edu.bistu.model.vo.UserVo;
import com.alibaba.fastjson.JSONObject;

public interface AuthService{
    /**
     * 用户认证，为登录接口提供服务。
     * 检查用户是否注册，若未注册，为用户自动注册；若已注册，认证用户身份
     * @param code 小程序微信临时登录凭证
     * @return 返回认证结果，若认证通过，返回用户信息和登录token（包含用户id）
     */
    ServiceResult authentication(String code);

    /**
     * 用户授权，为授权拦截器提供服务。
     * 检查用户的权限是否足以访问当前api
     * @param id 用户id
     * @param requestURL api的url
     * @param requestMethod api的请求方式
     * @return 如果权限足够，返回true，如果权限不足，返回false
     */
    boolean authorization(Long id, String requestURL, String requestMethod);

    /**
     * 提供给完善用户信息service使用
     * @param userVo 包含user的信息，信息如下:
     *               教师信息：
     *                  id,                     ：学生id
     *                  name,                   ：姓名
     *                  collegeName,            ：学院名称
     *                  secondaryDeptName,      ：所处二级部门名称
     *                  jobId                   ：工号（字符串）
     *                  roleId                  ：user的角色id，由于role与user是多对多的关系，所以需要将roleId和userId插入到关系user_role表中
     *
     *               学生信息：
     *                      id              ：学生id
     *                      name,           ：姓名
     *                      collegeName,    ：学院名称
     *                      majorName,      ：专业名称
     *                      className,      ：班级名称
     *                      grade,          ：年级号（整数）
     *                      studentId       ：学号（字符串）
     *                      roleId          ：user的角色id，由于role与user是多对多的关系，所以需要将roleId和userId插入到关系user_role表中
     */
    public ServiceResult<UserVo> userInfoCompletion(UserVo userVo);


    /**
     * 后台管理系统用户认证，为登录接口提供服务。
     * 使用code换取unionid，直接根据unionid查表检查是否存在用户，若存在且完善信息且角色为管理员，返回用户信息和登录token（包含用户id）；
     * 如果存在但是没有完善信息，返回错误代码；
     * 若不存在，直接返回未注册错误码，不自动注册用户；
     * @param code 第三方网站微信临时登录凭证
     * @return 返回认证结果，若认证通过，返回用户信息和登录token（包含用户id）
     */
    public ServiceResult adminSystemAuthentication(String code);

}