package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.aop;

import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mq.RabbitMQSender;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Aspect
@Component
public class RabbitMQSenderAspect {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Pointcut("execution(* itmo.org.learningenglishmanagmentsystem.simpleenglishdata.controller.*.*(..))")
    public void controllerMethods() {}

    @AfterReturning("controllerMethods()")
    public void afterControllerMethod(JoinPoint joinPoint) {
        String methodUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        rabbitMQSender.send(methodUrl);
    }
}
