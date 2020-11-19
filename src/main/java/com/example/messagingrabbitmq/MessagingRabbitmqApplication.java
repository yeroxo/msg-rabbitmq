package com.example.messagingrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.UUID;

@SpringBootApplication
public class MessagingRabbitmqApplication {

	static final String exchangeName = "another-exchange";
	static final String queueName = "com.main." + UUID.randomUUID();

	//здесь хранятся очереди
	@Bean
	Queue queue() {
		return new Queue(queueName, true, true, true);
	}

	//точка обмена , получает сообщения и с помощью критериев (binding) отправляет их в queue
	@Bean
	Exchange exchange() {
		final Exchange exchange = new FanoutExchange(exchangeName, true, false); //durable - существ и активна при перезагрузке сервера
		System.out.println("Declaring exchange: " + exchange.getName());
		return exchange;
	}


	//отношения между очередью сообщений и точками обмена
	@Bean
	Binding binding(Queue queue, Exchange exchange) {
		final Binding binding = new Binding(
				queue.getName(),
				Binding.DestinationType.QUEUE,
				exchange.getName(),
				"#",
				Collections.emptyMap());
		System.out.println("Declaring binding: " + binding.getExchange() + " -> " + binding.getDestination());
		return binding;
	}

	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	public static void main(String[] args)  {
		SpringApplication.run(MessagingRabbitmqApplication.class, args).close();
	}
// Важнейшие плюсы такого подхода – высокодоступность и отказоустойчивость.
}
