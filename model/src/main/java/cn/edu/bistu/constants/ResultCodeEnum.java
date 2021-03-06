package cn.edu.bistu.constants;
import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 * @author qy
 */
@Getter
public enum ResultCodeEnum {

    TOKEN_EXPIRED(100, "token已失效"),
    USER_INFO_NOT_COMPLETE(101, "用户信息未完善"),
    FRONT_DATA_MISSING(102, "前端数据缺失"),
    USER_UNREGISTERED(103, "此id用户未注册"),
    OAUTH_CODE_INVALID(104, "无效的 oauth_code"),
    OAUTH_CODE_BEEN_USED(105, "oauth_code 已被使用"),
    USER_INFO_COMPLETED(106, "用户信息无需完善"),
    ATTACHMENT_NOT_EXISTS(107, "附件不存在"),
    FRONT_DATA_REDUNDANT(108, "前端数据冗余"),
    AUTHENTICATION_FAIL(110, "用户认证失败"),
    AUTHORIZATION_FAIL(111, "用户授权失败"),
    /**
     * 初次用于鉴别工单撤回者与工单的发起者是否是同一个人
     */
    HAVE_NO_RIGHT(112, "无权进行此操作"),
    WORKORDER_BEEN_EXAMINED(113, "此工单已经被审批过，无法撤销"),
    WORKORDER_NOT_EXISTS(114, "工单不存在"),
    WORKORDER_BEEN_FINISHED(115, "工单已经结束"),
    USER_NOT_REGISTERED(116, "用户没有注册"),
    INTERFACE_ACCESS_ERRORS(117, "接口访问错误"),
    FRONT_DATA_ERROR(118, "前端参数错误"),
    USER_LOCK(119, "用户被锁定，无法登录"),
    NOT_MULTIPART_REQUEST(120, "当前请求不是多段数据请求"),
    WORKORDER_NOT_INVALIDATED(121, "工单没有被作废"),
    USER_IS_ADMIN(122, "用户已经是管理员了"),
    HAVE_NOT_TUTOR_YET(123, "该用户还没有导师"),
    USER_SPECIFIED_ID_NULL(124, "该工单本应携带用户指定的id"),


    SUCCESS(200,"成功"),
    FAIL(201, "失败"),


    SIGN_ERROR(300, "签名认证错误"),
    TOKEN_MISSING(301, "token丢失"),
    SIGN_MISSING(302, "签名丢失"),
    TOKEN_FORMAT_ERROR(303, "token格式错误"),
    TOKEN_BODY_ERROR(304, "token体解压错误"),
    TOKEN_ERROR(305, "token错误"),
    MESSAGE_NOT_EXIST(306,"消息不存在"),
    USER_SENDMSG_NOT_CONSISTENT(307,"发送消息和上传附件的用户不一致"),
    USER_MESSAGE_NOT_CONSISTENT(308,"用户和消息不匹配"),
    SENDER_IS_RECEIVER(309,"发送者和接受者一样"),
    BACKEND_ERROR(500, "后端错误");


    private Integer code;

    private String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
