package com.parkit.parkingsystem.model;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;

/*
 * Unit test for Ticket class
 */

class TicketTest {

	private static Ticket ticket;

	@BeforeEach
	void setUp() throws Exception {
		ticket = new Ticket();
	}

	@Test
	void testSetId() {
		// WHEN
		ticket.setId(6);
		// THEN
		assertEquals(6, ticket.getId());
	}

	@Test
	void testSetParkingSpot() {
		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		ticket.setParkingSpot(parkingSpot);
		// THEN
		assertEquals(parkingSpot, ticket.getParkingSpot());
	}

	@Test
	void testGetVehicleRegNumber() {
		// GIVEN
		ticket.setVehicleRegNumber("AZERTY");
		// THEN
		assertEquals("AZERTY", ticket.getVehicleRegNumber());
	}

	@Test
	void testSetInTime() {
		// GIVEN
		Date inTime = new Date(System.currentTimeMillis());
		ticket.setInTime(inTime);
		// THEN
		assertEquals(inTime, ticket.getInTime());
	}

	@Test
	void testSetOutTime() {
		// GIVEN
		Date outTime = new Date(System.currentTimeMillis());
		ticket.setOutTime(outTime);
		// THEN
		assertEquals(outTime, ticket.getOutTime());
	}

}
