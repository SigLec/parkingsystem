package com.parkit.parkingsystem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)

/*
 * Unit test for ParkingServvice class
 */

public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;
	@Mock
	private static FareCalculatorService fareCalculatorService;

	@BeforeEach
	private void setUpPerTest() {
		try {
			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Date inTime = new Date(System.currentTimeMillis() - (60 * 60 * 1000));
			Ticket ticket = new Ticket(parkingSpot, "ABCDEF", 0, inTime, null);

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void processIncomingVehicleTest_whenCar_shouldCallReadSelectionMethodOneTime() throws Exception {
		// GIVEN
		repMock();
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readSelection();
	}

	@Test
	public void processIncomingVehicleTest_whenCar_shouldCallGetNextAvailableSlotMethodOneTime() throws Exception {
		// GIVEN
		repMock();
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(any(ParkingType.class));
	}

	@Test
	public void processIncomingVehicleTest_whenCar_shouldCallReadVehicleRegistrationNumberMethodOneTime()
			throws Exception {
		// GIVEN
		repMock();
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(inputReaderUtil, Mockito.times(1)).readVehicleRegistrationNumber();
	}

	@Test
	public void processIncomingVehicleTest_whenCar_shouldCallGetDuplicationTicketMethodOneTime() throws Exception {
		// GIVEN
		repMock();
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(ticketDAO, Mockito.times(1)).getDuplicationTicket(anyString());
	}

	@Test
	public void processIncomingVehicleTest_whenCar_shouldCallUpdateParkingMethodOneTime() throws Exception {
		// GIVEN
		repMock();
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processIncomingVehicleTest_whenCar_shouldCallSaveTicketMethodOneTime() throws Exception {
		// GIVEN
		repMock();
		// WHEN
		parkingService.processIncomingVehicle();
		// THEN
		verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
	}

	private void repMock() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		when(ticketDAO.getDuplicationTicket(anyString())).thenReturn(0);
		when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

	}

	@Test
	public void processExitingVehicleTest_whenCar_ShouldCallUpdateParkingMethodOneTime() throws Exception {
		// GIVEN
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		Ticket ticket = new Ticket(parkingSpot, "ABCDEF", 0, inTime, outTime);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
		when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
		// WHEN
		parkingService.processExitingVehicle();
		// THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	@Test
	public void processExitingVehicleTestwhenCar_withAnInvalidVehicleRegistrationNumber_shoudNotCallGetTicketMethod()
			throws Exception {
		// GIVEN
		when(inputReaderUtil.readVehicleRegistrationNumber())
				.thenThrow(new IllegalArgumentException("Invalid input provided"));
		// WHEN
		parkingService.processExitingVehicle();
		// THEN
		verify(ticketDAO, Mockito.times(0)).getTicket(anyString());
	}
}
