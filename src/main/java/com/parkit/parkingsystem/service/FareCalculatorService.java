package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private TicketDAO ticketDAO;

	public FareCalculatorService(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}

	public double getDuration(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		double duration = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
		return duration;
	}

	public void calculateFare(Ticket ticket) {
		TicketDAO ticketDAO = new TicketDAO();
		int numberOfDuplication = ticketDAO.getDuplicationTicket(ticket.getVehicleRegNumber());
		double duration = 0;
		double hourTime = 60 * 60 * 1000;
		try {
			duration = getDuration(ticket);
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}
		if ((duration / hourTime) <= 0.5) {
			ticket.setPrice(0);
			return;
		}

		switch (ticket.getParkingSpot().getParkingType()) {
		case CAR:
			ticket.setPrice((duration / hourTime) * Fare.CAR_RATE_PER_HOUR);
			break;
		case BIKE:
			ticket.setPrice((duration / hourTime) * Fare.BIKE_RATE_PER_HOUR);
			break;
		default:
			throw new IllegalArgumentException("Unkown Parking Type");
		}
		if (numberOfDuplication > 0) {
			ticket.setPrice(ticket.getPrice() * 0.95);
			return;
		}
	}
}