package com.raguileoam.virtualticket.socket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.raguileoam.virtualticket.socket.model.TicketModelInfoHolder;

@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
public class WebSocketController {
	private ObjectMapper mapper;
	private TicketModelInfoHolder modelInfoHolder;
	private SimpMessagingTemplate messageTemplate;
	private final String destinationUrl = "/info/values";

	public WebSocketController(ObjectMapper mapper, SimpMessagingTemplate messageTemplate,
			TicketModelInfoHolder modelInfoHolder) {
		this.mapper = mapper;
		this.messageTemplate = messageTemplate;
		this.modelInfoHolder = modelInfoHolder;
	}

	// @Scheduled(fixedDelay = 5000)
	@MessageMapping("/welcome")
	// @SendTo("/info/values") //Alternative to messageTemplate.convertAndSend
	public void sendWebSocketUpdate() throws JsonProcessingException {
		modelInfoHolder.changeValues();
		this.messageTemplate.convertAndSend(destinationUrl,
				mapper.writeValueAsString(modelInfoHolder.getModelInfoList().values()));
	}
}