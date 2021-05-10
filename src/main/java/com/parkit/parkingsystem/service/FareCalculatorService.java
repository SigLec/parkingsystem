package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

/*
 * Calculation of parking time and fare to pay
 */

public class FareCalculatorService {

	private TicketDAO ticketDAO;

	public FareCalculatorService(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}
	/*
	 * Calculate the time between the vehicle enters in the parking and leaves.
	 * 
	 * @param ticket
	 * 
	 * @return duration
	 */

	public double getDuration(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		double duration = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
		return duration;
	}
	/*
	 * Calculate the price to pay for parking time including : free parking fare if
	 * duration is less than 30 minutes; 5% discount for recurrent users;
	 * 
	 * @param ticket
	 */

	public void calculateFare(Ticket ticket) {
		// Check if vehicle registration number has duplication in the database.
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
		/*
		 * If numberOfDuplication > 0, the user is known a recurrent and benefits 5%
		 * discount.
		 */
		if (numberOfDuplication > 0) {
			ticket.setPrice(ticket.getPrice() * 0.95);
			return;
		}
	}
}