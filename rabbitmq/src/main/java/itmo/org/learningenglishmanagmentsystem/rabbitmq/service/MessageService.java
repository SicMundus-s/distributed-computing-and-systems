package itmo.org.learningenglishmanagmentsystem.rabbitmq.service;


import itmo.org.learningenglishmanagmentsystem.core.dto.ServiceMessageHistoryDto;
import itmo.org.learningenglishmanagmentsystem.rabbitmq.mapper.MessageMapper;
import itmo.org.learningenglishmanagmentsystem.rabbitmq.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public void saveMessage(ServiceMessageHistoryDto message) {
        messageRepository.save(messageMapper.mapToEntity(message));
    }
}
