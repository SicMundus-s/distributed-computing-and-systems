package itmo.org.learningenglishmanagmentsystem.rabbitmq.repository;

import itmo.org.learningenglishmanagmentsystem.rabbitmq.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

}
