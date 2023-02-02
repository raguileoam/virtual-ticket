package com.raguileoam.virtualticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.raguileoam.virtualticket.controller.TicketController;
import com.raguileoam.virtualticket.model.Office;
import com.raguileoam.virtualticket.model.Ticket;
import com.raguileoam.virtualticket.repositories.OfficeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class VirtualTicketApplicationTests {

	@Autowired
	private TicketController ticketController;

	@Autowired
	private OfficeRepository officeRepository;

	private WebSocketStompClient webSocketStompClient;

	@LocalServerPort
	private Integer port;

	private final String subscriptionUrl = "/info/values";
	
	private final String socketEndpoint = "data-info";

	@BeforeEach
	void setup() {
		List<Transport> webSocketTransportList = List.of(new WebSocketTransport(new StandardWebSocketClient()));
		this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(webSocketTransportList));
		this.webSocketStompClient.setMessageConverter(new StringMessageConverter());
	}

	@Test
	void socketStompIsConnected() throws Exception {
		CompletableFuture<String> completableFuture = new CompletableFuture<>();
		this.webSocketStompClient
				.connectAsync(getWsPath(), new CustomStompFrameHandler(completableFuture))
				.get(1, TimeUnit.SECONDS);
		Ticket ticket = new Ticket();
		Office office = officeRepository.findById(1L).get();
		ticket.setOffice(office);
		ticket = ticketController.saveTicket(ticket);
		assertEquals(1L, office.getId());
		assertEquals(1L, ticket.getOffice().getId());
		String ticketInfoResponse = completableFuture.get(10, TimeUnit.SECONDS);
		assertNotNull(ticketInfoResponse);
	}

	private String getWsPath() {
		return String.format("ws://localhost:%d/%s", this.port, this.socketEndpoint);
	}

	private class CustomStompFrameHandler extends StompSessionHandlerAdapter {
		private Logger logger = LogManager.getLogger(CustomStompFrameHandler.class);
		private final CompletableFuture<String> completableFuture;

		public CustomStompFrameHandler(CompletableFuture<String> completableFuture) {
			this.completableFuture = completableFuture;
		}

		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			session.subscribe(subscriptionUrl, new StompFrameHandler() {
				@Override
				public Type getPayloadType(StompHeaders stompHeaders) {
					return String.class;
				}

				@Override
				public void handleFrame(StompHeaders headers, Object payload) {
					completableFuture.complete((String) payload);
				}
			});

			session.send("/app/welcome", null);
		}

		@Override
		public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
				Throwable exception) {
			logger.error("Got an exception", exception);
		}
	}
}
