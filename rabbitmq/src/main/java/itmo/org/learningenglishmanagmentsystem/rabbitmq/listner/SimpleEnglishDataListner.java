package itmo.org.learningenglishmanagmentsystem.rabbitmq.listner;

import itmo.org.learningenglishmanagmentsystem.core.dto.ServiceMessageHistoryDto;
import itmo.org.learningenglishmanagmentsystem.rabbitmq.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
@RequiredArgsConstructor
public class SimpleEnglishDataListner {

    private final MessageService messageService;

    @RabbitListener(queues = "${spring.rabbitmq.queueEnglishData}")
    public void receiveMessage(ServiceMessageHistoryDto message) {
        messageService.saveMessage(message);
    }

}
