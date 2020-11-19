package com.example.messagingrabbitmq;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Runner implements CommandLineRunner {

	private final RabbitTemplate rabbitTemplate;
	private final Receiver receiver;

	public static final String ANSI_RESET = "\u001B[0m";

	private static final String[] colors = new String[] {"\u001B[31m","\u001B[32m",
														 "\u001B[33m", "\u001B[34m",
														 "\u001B[35m", "\u001B[36m"};


	public Runner(Receiver receiver, RabbitTemplate rabbitTemplate) {
		this.receiver = receiver;
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner sc = new Scanner(System.in);
		String color = colors[new Random().nextInt(colors.length - 1)];
		System.out.println(colors[1] + "\n\n\nEnter the message or wait for it" + ANSI_RESET);
		while(true) {
			String message = sc.nextLine();
			System.out.println(colors[0] + ">sending message..." + ANSI_RESET);
			rabbitTemplate.convertAndSend(
					MessagingRabbitmqApplication.exchangeName,
					"com.main.testMessage",
					color + message + ANSI_RESET);
			System.out.println(colors[5] + ">message sent" + ANSI_RESET);
			receiver.getLatch().await(1000, TimeUnit.MILLISECONDS);
		}
	}
}
