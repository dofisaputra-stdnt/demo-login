package com.cloudify.demologin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.cloudify.demologin.config.kafka.producer.MessageProducer;

@RestController
public class KafkaController {

    @Autowired
    private MessageProducer messageProducer;

	@PostMapping("/send-message")
	public String sendMessage(@RequestBody String message) {
		messageProducer.sendMessage("demo-login-topic", message);
		return "Message sent successfully";
	}
}
