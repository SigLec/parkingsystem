package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public double getDuration(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}
		double duration = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
		return duration;
	}

	public void calculateFare(Ticket ticket) {

		/*
		 * int inHour = ticket.getInTime().getHours(); int outHour =
		 * ticket.getOutTime().getHours();
		 * 
		 * //TODO: Some tests are failing here. Need to check if this logic is correct
		 * int duration = outHour - inHour;
		 */
		try {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				if (getDuration(ticket) <= 30 * 60 * 1000) {
					ticket.setPrice(0);
				} else {
					ticket.setPrice((getDuration(ticket) / 1000 / 60 / 60) * Fare.CAR_RATE_PER_HOUR);
					break;
				}
			}
			case BIKE: {
				if (getDuration(ticket) <= 30 * 60 * 1000) {
					ticket.setPrice(0);
				} else {
					ticket.setPrice((getDuration(ticket) / 1000 / 60 / 60) * Fare.BIKE_RATE_PER_HOUR);
					break;
				}
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());

		}
	}
}