package com.parkit.parkingsystem.model;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.parkit.parkingsystem.constants.ParkingType;

/*
 * Unit test for ParkingSpot class
 */

class ParkingSpotTest {

	private static ParkingSpot parkingSpot;

	@BeforeEach
	void setUp() throws Exception {
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	}

	@Test
	void testGetId() {
		assertEquals(1, parkingSpot.getId());
	}

	@Test
	void testGetParkingType() {
		assertEquals(ParkingType.CAR, parkingSpot.getParkingType());
	}

	@Test
	void testIsAvailable() {
		assertEquals(false, parkingSpot.isAvailable());
	}

	@Test
	void testEqualsObject() {
		assertTrue(parkingSpot.equals(parkingSpot));
	}

	@Test
	void testHashCode() {
		assertEquals(1, parkingSpot.hashCode());
	}
}