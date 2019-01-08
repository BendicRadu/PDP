import distributed.Agent;
import distributed.Master;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@ComponentScan("distributed")
@SpringBootApplication
public class DistributedMain {


    static final String TOPIC_EXCHANGE_NAME = "spring-boot-exchange";

    static final String QUEUE_NAME_1 = "q1";
    static final String QUEUE_NAME_2 = "q2";
    static final String QUEUE_NAME_3 = "q3";
    static final String QUEUE_NAME_4 = "q4";

    @Bean
    Queue queue1() {
        return new Queue(QUEUE_NAME_1, false);
    }

    @Bean
    Queue queue2() {
        return new Queue(QUEUE_NAME_2, false);
    }


    @Bean
    Queue queue3() {
        return new Queue(QUEUE_NAME_3, false);
    }


    @Bean
    Queue queue4() {
        return new Queue(QUEUE_NAME_4, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(TOPIC_EXCHANGE_NAME);
    }

    @Bean
    Binding binding1() {
        return BindingBuilder.bind(queue1()).to(exchange()).with("1.1.1");
    }

    @Bean
    Binding binding2() {
        return BindingBuilder.bind(queue2()).to(exchange()).with("2.2.2");
    }

    @Bean
    Binding binding3() {
        return BindingBuilder.bind(queue3()).to(exchange()).with("3.3.3");
    }

    @Bean
    Binding binding4() {
        return BindingBuilder.bind(queue4()).to(exchange()).with("4.4.4");
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME_1, QUEUE_NAME_2, QUEUE_NAME_3, QUEUE_NAME_4);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Agent agent) {
        return new MessageListenerAdapter(agent, "receiveMessage");
    }


    public static void main(String[] argv) throws IOException {

        ConfigurableApplicationContext ctx = SpringApplication.run(DistributedMain.class, argv);

        Master master = (Master) ctx.getBean("master");
        master.run();

    }


}
