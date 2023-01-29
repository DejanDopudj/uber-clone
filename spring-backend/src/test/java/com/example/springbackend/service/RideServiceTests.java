package com.example.springbackend.service;

import com.example.springbackend.dto.creation.BasicRideCreationDTO;
import com.example.springbackend.dto.display.RideSimpleDisplayDTO;
import com.example.springbackend.exception.AdequateDriverNotFoundException;
import com.example.springbackend.exception.InsufficientFundsException;
import com.example.springbackend.exception.PassengerAlreadyHasAnActiveRideException;
import com.example.springbackend.exception.ReservationTooSoonException;
import com.example.springbackend.model.*;
import com.example.springbackend.repository.PassengerRepository;
import com.example.springbackend.repository.PassengerRideRepository;
import com.example.springbackend.repository.RideRepository;
import com.example.springbackend.repository.VehicleTypeRepository;
import com.example.springbackend.util.RideUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class RideServiceTests {
    @Mock
    private VehicleTypeRepository vehicleTypeRepositoryMock;
    @Mock
    private PassengerRideRepository passengerRideRepositoryMock;
    @Mock
    private PassengerRepository passengerRepositoryMock;
    @Mock
    private RideRepository rideRepositoryMock;
    @Mock
    private RideUtils rideUtilsMock;

    @Mock
    private Passenger passengerMock;
    @Mock
    private PassengerRide passengerRideMock;
    @Mock
    private VehicleType vehicleType;
    @Mock
    private Authentication authenticationMock;
    @Mock
    private Driver driverMock;
    @Mock
    private Ride rideMock;

    @InjectMocks
    private RideService rideService;

    @BeforeEach
    void beforeEach() {
        reset();
    }

    @Test
    void testTests() {
        Assertions.assertTrue(true);
    }


    @Test
    void Throw_exception_when_current_passenger_ride_exists_while_ordering_a_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName(anyString()))
                        .thenReturn(Optional.of(vehicleType));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(any()))
                        .thenReturn(Optional.of(passengerRideMock));

        assertThrows(
                PassengerAlreadyHasAnActiveRideException.class,
                () -> rideService.orderBasicRide(dto, authentication),
                "Expected orderBasicRide() to throw PassengerAlreadyHasAnActiveRideException, but it didn't"
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 1000, 1299})
    void Throw_exception_when_passenger_lacks_tokens_for_a_basic_ride(int passengerTokenBalance) {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName(anyString()))
                .thenReturn(Optional.of(vehicleType));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(any()))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(passengerTokenBalance);

        assertThrows(
                InsufficientFundsException.class,
                () -> rideService.orderBasicRide(dto, authenticationMock),
                "Expected orderBasicRide() to throw InsufficientFundsException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_driver_is_not_found_for_an_immediate_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName(anyString()))
                .thenReturn(Optional.of(vehicleType));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(any()))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(any(), any())).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(0);

        when(rideUtilsMock.findDriver(rideMock)).thenReturn(null);
        doNothing().when(rideMock).setStatus(any());
        when(rideRepositoryMock.save(any())).thenReturn(null);

        assertThrows(
                AdequateDriverNotFoundException.class,
                () -> rideService.orderBasicRide(dto, authenticationMock),
                "Expected orderBasicRide() to throw AdequateDriverNotFoundException, but it didn't"
        );
        verify(rideMock).setStatus(any());
        verify(rideRepositoryMock).save(any());
    }

    @Test
    void Return_object_after_a_successful_creation_of_an_immediate_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName(anyString()))
                .thenReturn(Optional.of(vehicleType));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(any()))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(any(), any())).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(0);

        when(rideUtilsMock.findDriver(rideMock)).thenReturn(driverMock);
        doNothing().when(rideUtilsMock).linkDriverAndRide(any(), any());
        when(passengerRepositoryMock.save(any())).thenReturn(passengerMock);
        doNothing().when(passengerMock).setTokenBalance(anyInt());

        RideSimpleDisplayDTO rideSimpleDisplayDTOMock = mock(RideSimpleDisplayDTO.class);
        when(rideUtilsMock.createBasicRideSimpleDisplayDTO(any(), any())).thenReturn(rideSimpleDisplayDTOMock);

        assertEquals(rideService.orderBasicRide(dto, authenticationMock), rideSimpleDisplayDTOMock);
        verify(rideUtilsMock).linkDriverAndRide(any(), any());
        verify(passengerMock).setTokenBalance(anyInt());
        verify(passengerRepositoryMock).save(any());
        verify(rideUtilsMock).sendRefreshMessage(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1, 10, 19})
    void Throw_exception_when_delay_is_shorter_than_20_for_a_scheduled_basic_ride(int delayInMinutes) {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName(anyString()))
                .thenReturn(Optional.of(vehicleType));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(any()))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(any(), any())).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(delayInMinutes);

        doNothing().when(rideMock).setStatus(any());
        when(rideRepositoryMock.save(any())).thenReturn(rideMock);

        assertThrows(
                ReservationTooSoonException.class,
                () -> rideService.orderBasicRide(dto, authenticationMock),
                "Expected orderBasicRide() to throw ReservationTooSoonException, but it didn't"
        );
        verify(rideMock).setStatus(any());
        verify(rideRepositoryMock).save(any());
    }

    @Test
    void Return_object_after_a_successful_creation_of_a_scheduled_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName(anyString()))
                .thenReturn(Optional.of(vehicleType));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(any()))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(any(), any())).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(20);

        doNothing().when(passengerMock).setTokenBalance(anyInt());
        when(passengerRepositoryMock.save(any())).thenReturn(passengerMock);
        doNothing().when(rideUtilsMock).handleNotificationsAndProcessReservations(any(), anyList());

        RideSimpleDisplayDTO rideSimpleDisplayDTOMock = mock(RideSimpleDisplayDTO.class);
        when(rideUtilsMock.createBasicRideSimpleDisplayDTO(any(), any())).thenReturn(rideSimpleDisplayDTOMock);

        assertEquals(rideService.orderBasicRide(dto, authenticationMock), rideSimpleDisplayDTOMock);
        verify(passengerMock).setTokenBalance(anyInt());
        verify(passengerRepositoryMock).save(any());
        verify(rideUtilsMock).handleNotificationsAndProcessReservations(any(), anyList());
    }

}
