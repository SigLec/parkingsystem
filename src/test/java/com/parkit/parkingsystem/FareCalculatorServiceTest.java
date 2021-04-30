package com.parkit.parkingsystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;

@ExtendWith(MockitoExtension.class)

public class FareCalculatorServiceTest {

	private static FareCalculatorService fareCalculatorService;
	private Ticket ticket;

	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		ticket = new Ticket();
		fareCalculatorService = new FareCalculatorService(ticketDAO);
	}

	@Test
	public void getDuration_whenInTimeIsOneOurBeforeOutTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - 60 * 60 * 1000);
		Date outTime = new Date();
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		double parkingTime = fareCalculatorService.getDuration(ticket);
		// THEN
		assertThat(parkingTime).isEqualTo((3600000));
	}

	@Test
	public void getDuration_throwsException_withFutureInTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() + (60 * 60 * 1000));
		Date outTime = new Date();
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		// THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.getDuration(ticket));
	}

	@Test
	public void getDuration_throwsException_whenOutTimeIsBeforeInTime() {
		// GIVEN
		Date inTime = new Date();
		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		// THEN
		assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.getDuration(ticket));
	}

	@Test
	public void getDuration_throwsException_whenOutTimeIsNull() {
		// GIVEN
		ticket.setInTime(new Date());
		ticket.setOutTime(null);
		// THEN
		assertThrows(NullPointerException.class, () -> fareCalculatorService.getDuration(ticket));
	}

	@Test
	public void calculateFare_throwsException_whenUnkownType() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, null, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		// THEN
		assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
	}

	@Test
	public void calculateFare_whenCar_withOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
	}

	@Test
	public void calculateFare_whenBike_withOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
	}

	@Test
	public void calculateFare_whenBike_withLessThanOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFare_whenCar_withLessThanOneHourParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give 3/4th
																		// parking fare
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@Test
	public void calculateFare_whenCar_withMoreThanADayParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));// 24 hours parking time should give 24 *
																			// parking fare per hour
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@DisplayName("Parking a car with less that 30min Parking time should return free")
	@Test
	public void calculateFare_whenCar_withLessThanThirtyMinutesParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (20 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals(0, ticket.getPrice());
	}

	@DisplayName("Parking a bike with less that 30min Parking time should return free")
	@Test
	public void calculateFare_whenBike_withLessThanThirtyMinutesParkingTime() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		// WHEN
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		ticket.setParkingSpot(parkingSpot);
		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals(0, ticket.getPrice());
	}

	@DisplayName("Test for reccurrent users with car should return fare with 5% discount")
	@Test
	public void calculateFare_whenCar_withDiscountForRecurrentUser() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (4 * 60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// WHEN
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("AZERTY");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		when(ticketDAO.getDuplicationTicket("AZERTY")).thenReturn(2);

		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals(0.95 * (4 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
	}

	@DisplayName("Test for reccurrent users with bike should return fare with 5% discount")
	@Test
	public void calculateFare_whenBike_withDiscountForRecurrentUser() {
		// GIVEN
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));
		Date outTime = new Date();
		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);
		// WHEN
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("AZERTY");
		ticket.setInTime(inTime);
		ticket.setOutTime(outTime);
		when(ticketDAO.getDuplicationTicket("AZERTY")).thenReturn(2);

		fareCalculatorService.calculateFare(ticket);
		// THEN
		assertEquals((0.95 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
	}
}
