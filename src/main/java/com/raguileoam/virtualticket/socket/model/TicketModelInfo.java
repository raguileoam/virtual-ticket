package com.raguileoam.virtualticket.socket.model;

import lombok.Getter;

import java.io.Serializable;

import com.raguileoam.virtualticket.domain.model.Office;

@Getter
public class TicketModelInfo implements Serializable {
	private static final long serialVersionUID = -7984032536291170146L;

	private String lastTicketInProcess;

	private String lastTicketInAttention;

	private Office office;

	public TicketModelInfo(Office office) {
		this.office = office;
		this.lastTicketInProcess = "-";
		this.lastTicketInAttention = "-";
	}

	public void changeValues(String lastTicketInProcess, String lastTicketInAttention) {
		this.lastTicketInProcess = lastTicketInProcess;
		this.lastTicketInAttention = lastTicketInAttention;
	}

}