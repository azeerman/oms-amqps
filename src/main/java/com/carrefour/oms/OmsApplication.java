package com.carrefour.oms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(OmsProperties.class)
public class OmsApplication {
	
	
	private static final Logger log = LoggerFactory.getLogger(OmsApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(OmsApplication.class, args);
	}
	
    @Bean
    public ApplicationRunner runner(AmqpTemplate template) {
        return args -> template.convertAndSend("myqueue", "foo");
    }

    @Bean
    public Queue myQueue() {
        return new Queue("myqueue");
    }

    @RabbitListener(queues = "myqueue",containerFactory = "omsRabbitListenerContainerFactory")
    public void listen(String in) {
        log.info(in);
    }
    
    @RabbitListener(queues = "myqueue",containerFactory = "rabbitListenerContainerFactory")
    public void listenDefault(String in) {
    	 log.info(in);
    }

}
