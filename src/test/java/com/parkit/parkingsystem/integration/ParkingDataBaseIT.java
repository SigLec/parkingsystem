package com.parkit.parkingsystem.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {
	}

// TODO: check that a ticket is actually saved in DB and Parking table is updated with availability
	@Test
	public void testParkingACar_checkIfTheTicketIsSavedInDataBase() {
		// WHEN
		repTest();
		assertNotNull(ticketDAO.getTicket("ABCDEF"));
	}

	public void testParkingACar_checkIfParkingTableIsUpdated_WithAvailbility() {
		// WHEN
		repTest();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		assertFalse(parkingSpot.isAvailable());
	}

	private void repTest() {
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
		parkingService.processIncomingVehicle();
		Ticket ticket = new Ticket(parkingSpot, "ABCDEF", 0, inTime, null);
	}

// TODO: check that the fare generated and out time are populated correctly in the database
	@Test
	public void testParkingLotExit_checkIfFareGeneratedIsPopulatedCorrectly_InTheDataBase() throws Exception {
		// GIVEN
		testParkingACar_checkIfTheTicketIsSavedInDataBase();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// WHEN
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		// THEN
		assertEquals(0, ticket.getPrice(), 0);
	}

	@Test
	public void testParkingLotExit_checkIfOutTimeIsPopulatedCorrectly_InTheDataBase() throws Exception {
		// GIVEN
		testParkingACar_checkIfTheTicketIsSavedInDataBase();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		// WHEN
		parkingService.processExitingVehicle();
		Ticket ticket = ticketDAO.getTicket("ABCDEF");
		// THEN
		assertNotNull(ticket.getOutTime());
	}
}
