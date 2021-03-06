package cn.edu.bistu.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Validator;


@Configuration
public class ValidationConfiguration {

    @Autowired
    ApplicationContext context;

    @Bean
    @Scope("prototype")
    //@ConditionalOnBean(Validator.class)
    public ValidationWrapper validationWrapper() {
        Validator defaultValidator = (Validator) context.getBean("defaultValidator");
        return new ValidationWrapper(defaultValidator);
    }

}
