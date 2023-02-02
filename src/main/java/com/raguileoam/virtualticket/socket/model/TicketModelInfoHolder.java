package com.raguileoam.virtualticket.socket.model;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.raguileoam.virtualticket.model.Office;
import com.raguileoam.virtualticket.model.Ticket;
import com.raguileoam.virtualticket.model.TicketState;
import com.raguileoam.virtualticket.repositories.OfficeRepository;
import com.raguileoam.virtualticket.repositories.TicketRepository;

@Component
public class TicketModelInfoHolder {
	@Autowired
	TicketRepository ticketRepository;

	@Autowired
	OfficeRepository officeRepository;

	private HashMap<Long, TicketModelInfo> modelInfoList;

	public TicketModelInfoHolder() {
		this.modelInfoList = new HashMap<>();
	}

	public HashMap<Long, TicketModelInfo> getModelInfoList() {
		return this.modelInfoList;
	}

	public void changeValues() {
		List<Office> list = Optional.ofNullable(officeRepository.findAll()).orElse(new ArrayList<>());
		this.modelInfoList = new HashMap<>();
		for (Office office : list) {
			TicketModelInfo modelInfo = new TicketModelInfo(office);
			Optional<Ticket> ticket = ticketRepository.findTop1ByOfficeIdOrderByIdDesc(modelInfo.getOffice().getId());
			Optional<Ticket> ticket2 = ticketRepository
					.findTop1ByOfficeIdAndStatusOrderByIdAsc(modelInfo.getOffice().getId(), TicketState.ACTIVE);
			modelInfo.changeValues(ticket.map(t -> t.getAttentionId()).orElse("-"),
					ticket2.map(t -> t.getAttentionId()).orElse("-"));
			this.modelInfoList.put(modelInfo.getOffice().getId(), modelInfo);
		}
	}
}
