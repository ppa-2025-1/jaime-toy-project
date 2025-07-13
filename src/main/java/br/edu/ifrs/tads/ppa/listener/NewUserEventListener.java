package br.edu.ifrs.tads.ppa.listener;

import br.edu.ifrs.tads.ppa.config.RabbitMQConfig;
import br.edu.ifrs.tads.ppa.event.NewUserEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NewUserEventListener {

    @RabbitListener(queues = RabbitMQConfig.USER_QUEUE)
    public void newUser(NewUserEvent event) {
        
        System.out.println("Novo usuário criado: " + event);
    }
}
