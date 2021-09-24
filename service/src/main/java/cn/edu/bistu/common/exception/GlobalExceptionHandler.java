package cn.edu.bistu.common.exception;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.result.Result;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.lang.reflect.Field;
import java.util.Set;

/**
 * 全局异常处理类
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 统一处理返回值
     *
     * @return
     */
    @ExceptionHandler({ResultCodeException.class})
    @ResponseBody
    public Result frontDataMissingException(ResultCodeException ex) {
        if (!(ex.getExceptionInfo() == null)) {
            log.debug(ex.getCode().toString() + ":");
        }

        log.debug(ex.getExceptionInfo().toString());
        return Result.build(ex.getExceptionInfo(), ex.getCode());
    }

    /**
     * 统一处理其他后端异常
     *
     * @return
     */
    @ExceptionHandler({RuntimeException.class})
    @ResponseBody
    public Result runtimeException(RuntimeException ex) {
        log.error("exception:", ex);
        return Result.build(ex.getClass().getTypeName(), ResultCodeEnum.BACKEND_ERROR);
    }


    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseBody
    public Result missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("exception:", ex);
        return Result.build(ex.getMessage(), ResultCodeEnum.FRONT_DATA_MISSING);
    }

    //@ExceptionHandler({MethodArgumentNotValidException.class})
    //@ResponseBody
    //public Result methodArgumentNotValidException(MethodArgumentNotValidException ex) {
    //    BindingResult bindingResult = ex.getBindingResult();
    //    ParameterCheckResult parameterCheckResult = bindingResultPackager(bindingResult);
    //    return Result.build(parameterCheckResult, ResultCodeEnum.FRONT_DATA_ERROR);
    //}

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public Result bindException(Exception ex) throws NoSuchFieldException, IllegalAccessException {
        BindingResult bindingResult = FieldGetter.getField(ex, "bindingResult", BindingResult.class);
        ParameterCheckResult parameterCheckResult = bindingResultPackager(bindingResult);
        return Result.build(parameterCheckResult, ResultCodeEnum.FRONT_DATA_ERROR);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseBody
    public Result httpMessageNotReadableException(Exception ex) throws NoSuchFieldException, IllegalAccessException {
        return Result.build(ex.getMessage(), ResultCodeEnum.FRONT_DATA_ERROR);
    }



    private ParameterCheckResult bindingResultPackager(BindingResult bindingResult) {
        ParameterCheckResult parameterCheckResult = new ParameterCheckResult();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            FieldError fieldError = (FieldError) objectError;
            parameterCheckResult.putResult(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return parameterCheckResult;
    }

    /**
     * 统一处理参数校验异常
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public Result constraintViolationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ParameterCheckResult parameterCheckResult = new ParameterCheckResult();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            parameterCheckResult.putResult(getLastPathNode(constraintViolation.getPropertyPath()),
                    constraintViolation.getMessage());
        }

        return Result.build(parameterCheckResult, ResultCodeEnum.FRONT_DATA_ERROR);
    }


    private static String getLastPathNode(Path path) {
        String wholePath = path.toString();
        int i = wholePath.lastIndexOf(".");
        if (i != -1) {
            String substring = wholePath.substring(i + 1, wholePath.length());
            return substring;
        }
        return wholePath;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    class ParameterCheckResult {
        JSONObject paramCheckMap = new JSONObject();

        public void putResult(String field, String message) {
            paramCheckMap.put(field, message);
        }
    }


}


class FieldGetter{
    public static <T> T getField(Object source, String fieldName, Class<T> fieldClazz) throws NoSuchFieldException, IllegalAccessException {
        Field field = BeanUtils.getDeclaredField(source.getClass(), fieldName);

        if(field.getType().equals(fieldClazz)) {
            field.setAccessible(true);
            return (T)field.get(source);
        }

        throw new NoSuchFieldException("field " + fieldName + " not found");
    }
}
