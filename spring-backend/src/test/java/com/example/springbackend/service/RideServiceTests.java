package com.example.springbackend.service;

import com.example.springbackend.dto.creation.*;
import com.example.springbackend.dto.display.RideSimpleDisplayDTO;
import com.example.springbackend.exception.*;
import com.example.springbackend.model.*;
import com.example.springbackend.model.helpClasses.Coordinates;
import com.example.springbackend.repository.*;
import com.example.springbackend.util.RideUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootTest(classes = TestConfig.class)
@ExtendWith(MockitoExtension.class)
public class RideServiceTests {
    @Mock
    private VehicleTypeRepository vehicleTypeRepositoryMock;
    @Mock
    private VehicleRepository vehicleRepositoryMock;
    @Mock
    private PassengerRideRepository passengerRideRepositoryMock;
    @Mock
    private PassengerRepository passengerRepositoryMock;
    @Mock
    private RideRepository rideRepositoryMock;
    @Mock
    private DriverRepository driverRepositoryMock;
    @Mock
    private RideUtils rideUtilsMock;

    @Mock
    private Passenger passengerMock;
    @Mock
    private PassengerRide passengerRideMock;
    @Mock
    private Vehicle vehicleMock;
    @Mock
    private VehicleType vehicleTypeMock;
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

    // basic ride
    @Test
    void Throw_exception_when_vehicle_type_does_not_exist_when_ordering_a_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn(null);
        assertThrows(
                NoSuchElementException.class,
                () -> rideService.orderBasicRide(dto, authentication),
                "Expected orderBasicRide() to throw NoSuchElementException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_current_passenger_ride_exists_while_ordering_a_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE"))
                        .thenReturn(Optional.of(vehicleTypeMock));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(passengerMock))
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
        when(vehicleTypeRepositoryMock.findByName("COUPE"))
                .thenReturn(Optional.of(vehicleTypeMock));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(passengerMock))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
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
        when(vehicleTypeRepositoryMock.findByName("COUPE"))
                .thenReturn(Optional.of(vehicleTypeMock));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(passengerMock))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(passengerMock, rideMock)).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(0);

        when(rideUtilsMock.findDriver(rideMock)).thenReturn(null);
        doNothing().when(rideMock).setStatus(RideStatus.CANCELLED);
        when(rideRepositoryMock.save(rideMock)).thenReturn(rideMock);

        assertThrows(
                AdequateDriverNotFoundException.class,
                () -> rideService.orderBasicRide(dto, authenticationMock),
                "Expected orderBasicRide() to throw AdequateDriverNotFoundException, but it didn't"
        );
        verify(rideMock).setStatus(RideStatus.CANCELLED);
        verify(rideRepositoryMock).save(rideMock);
    }

    @Test
    void Return_object_after_a_successful_creation_of_an_immediate_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE"))
                .thenReturn(Optional.of(vehicleTypeMock));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(passengerMock))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(passengerMock, rideMock)).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(0);

        when(rideUtilsMock.findDriver(rideMock)).thenReturn(driverMock);
        doNothing().when(rideUtilsMock).linkDriverAndRide(driverMock, rideMock);
        when(passengerRepositoryMock.save(any())).thenReturn(passengerMock);
        doNothing().when(passengerMock).setTokenBalance(anyInt());

        RideSimpleDisplayDTO rideSimpleDisplayDTOMock = mock(RideSimpleDisplayDTO.class);
        when(rideUtilsMock.createBasicRideSimpleDisplayDTO(passengerRideMock, driverMock)).thenReturn(rideSimpleDisplayDTOMock);

        assertEquals(rideService.orderBasicRide(dto, authenticationMock), rideSimpleDisplayDTOMock);
        verify(rideUtilsMock).linkDriverAndRide(driverMock, rideMock);
        verify(passengerMock).setTokenBalance(anyInt());
        verify(passengerRepositoryMock).save(passengerMock);
        verify(rideUtilsMock).sendRefreshMessage(any());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1, 10, 19})
    void Throw_exception_when_delay_is_shorter_than_20_for_a_scheduled_basic_ride(int delayInMinutes) {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE"))
                .thenReturn(Optional.of(vehicleTypeMock));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(passengerMock))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(passengerMock, rideMock)).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(delayInMinutes);

        doNothing().when(rideMock).setStatus(RideStatus.CANCELLED);
        when(rideRepositoryMock.save(rideMock)).thenReturn(rideMock);

        assertThrows(
                ReservationTooSoonException.class,
                () -> rideService.orderBasicRide(dto, authenticationMock),
                "Expected orderBasicRide() to throw ReservationTooSoonException, but it didn't"
        );
        verify(rideMock).setStatus(RideStatus.CANCELLED);
        verify(rideRepositoryMock).save(rideMock);
    }

    @Test
    void Return_object_after_a_successful_creation_of_a_scheduled_basic_ride() {
        BasicRideCreationDTO dto = mock(BasicRideCreationDTO.class);

        when(authenticationMock.getPrincipal()).thenReturn(passengerMock);

        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE"))
                .thenReturn(Optional.of(vehicleTypeMock));
        when(passengerRideRepositoryMock.getCurrentPassengerRide(passengerMock))
                .thenReturn(Optional.empty());

        when(rideUtilsMock.calculateRidePrice(any(), any())).thenReturn(1300);
        when(passengerMock.getTokenBalance()).thenReturn(1300);

        when(rideUtilsMock.createBasicRide(any(), anyInt(), any())).thenReturn(rideMock);
        when(rideUtilsMock.createPassengerRide(passengerMock, rideMock)).thenReturn(passengerRideMock);
        when(dto.getDelayInMinutes()).thenReturn(20);

        doNothing().when(passengerMock).setTokenBalance(anyInt());
        when(passengerRepositoryMock.save(passengerMock)).thenReturn(passengerMock);
        doNothing().when(rideUtilsMock).handleNotificationsAndProcessReservations(any(), anyList());

        RideSimpleDisplayDTO rideSimpleDisplayDTOMock = mock(RideSimpleDisplayDTO.class);
        when(rideUtilsMock.createBasicRideSimpleDisplayDTO(any(), any())).thenReturn(rideSimpleDisplayDTOMock);

        assertEquals(rideService.orderBasicRide(dto, authenticationMock), rideSimpleDisplayDTOMock);
        verify(passengerMock).setTokenBalance(anyInt());
        verify(passengerRepositoryMock).save(passengerMock);
        verify(rideUtilsMock).handleNotificationsAndProcessReservations(any(), anyList());
    }


    // split fare ride
    @Test
    void Throw_exception_when_vehicle_type_does_not_exist_when_ordering_a_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn(null);
        assertThrows(
                NoSuchElementException.class,
                () -> rideService.orderSplitFareRide(dto, authentication),
                "Expected orderSplitFareRide() to throw NoSuchElementException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_a_linked_passenger_does_not_exist_when_ordering_a_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE")).thenReturn(Optional.of(vehicleTypeMock));

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        when(dto.getUsersToPay()).thenReturn(new ArrayList<>(3));

        doThrow(UserDoesNotExistException.class)
                .when(rideUtilsMock).checkIfSplitFareRideIsValid(any(), any(), anyInt());

        assertThrows(
                UserDoesNotExistException.class,
                () -> rideService.orderSplitFareRide(dto, authentication),
                "Expected orderSplitFareRide() to throw UserDoesNotExistException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_there_are_duplicate_passengers_when_ordering_a_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE")).thenReturn(Optional.of(vehicleTypeMock));

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        when(dto.getUsersToPay()).thenReturn(new ArrayList<>(3));

        doThrow(LinkedPassengersNotAllDistinctException.class)
                .when(rideUtilsMock).checkIfSplitFareRideIsValid(any(), any(), anyInt());

        assertThrows(
                LinkedPassengersNotAllDistinctException.class,
                () -> rideService.orderSplitFareRide(dto, authentication),
                "Expected orderSplitFareRide() to throw LinkedPassengersNotAllDistinctException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_a_passenger_already_has_an_active_ride_when_ordering_a_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE")).thenReturn(Optional.of(vehicleTypeMock));

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        when(dto.getUsersToPay()).thenReturn(new ArrayList<>(3));

        doThrow(PassengerAlreadyHasAnActiveRideException.class)
                .when(rideUtilsMock).checkIfSplitFareRideIsValid(any(), any(), anyInt());

        assertThrows(
                PassengerAlreadyHasAnActiveRideException.class,
                () -> rideService.orderSplitFareRide(dto, authentication),
                "Expected orderSplitFareRide() to throw PassengerAlreadyHasAnActiveRideException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_the_order_sender_passenger_lacks_tokens_when_ordering_a_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE")).thenReturn(Optional.of(vehicleTypeMock));

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        when(dto.getUsersToPay()).thenReturn(new ArrayList<>(3));

        doThrow(InsufficientFundsException.class)
                .when(rideUtilsMock).checkIfSplitFareRideIsValid(any(), any(), anyInt());

        assertThrows(
                InsufficientFundsException.class,
                () -> rideService.orderSplitFareRide(dto, authentication),
                "Expected orderSplitFareRide() to throw InsufficientFundsException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_delay_is_shorter_than_20_for_a_scheduled_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE")).thenReturn(Optional.of(vehicleTypeMock));

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        when(dto.getUsersToPay()).thenReturn(new ArrayList<>(3));

        doThrow(ReservationTooSoonException.class)
                .when(rideUtilsMock).checkIfSplitFareRideIsValid(any(), any(), anyInt());

        assertThrows(
                ReservationTooSoonException.class,
                () -> rideService.orderSplitFareRide(dto, authentication),
                "Expected orderSplitFareRide() to throw ReservationTooSoonException, but it didn't"
        );
    }


    @Test
    void Return_true_after_a_successful_creation_of_an_immediate_split_fare_ride() {
        SplitFareRideCreationDTO dto = mock(SplitFareRideCreationDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(dto.getVehicleType()).thenReturn("COUPE");
        when(vehicleTypeRepositoryMock.findByName("COUPE")).thenReturn(Optional.of(vehicleTypeMock));

        when(rideUtilsMock.calculateRidePrice(dto, vehicleTypeMock)).thenReturn(1300);
        List<String> usersToPay = new ArrayList<>(3);
        usersToPay.add("username1@noemail.com");
        usersToPay.add("username2@noemail.com");
        usersToPay.add("username3@noemail.com");

        when(dto.getUsersToPay()).thenReturn(usersToPay);
        doNothing().when(rideUtilsMock).checkIfSplitFareRideIsValid(any(), any(), anyInt());

        when(rideUtilsMock.createSplitFareRide(any(), anyInt())).thenReturn(rideMock);
        doNothing().when(passengerMock).setTokenBalance(anyInt());
        when(passengerRepositoryMock.save(passengerMock)).thenReturn(passengerMock);
        doNothing().when(rideUtilsMock).createPassengerRideForUsers(any(), any(), anyInt(), any());

        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        doNothing().when(rideUtilsMock).sendRefreshMessage(anyString());

        RideService rideServiceSpy = Mockito.spy(rideService);
        doNothing().when(rideServiceSpy).scheduleExecution(any(), anyLong(), any());

        assertTrue(rideServiceSpy.orderSplitFareRide(dto, authentication));
        verify(passengerMock).setTokenBalance(anyInt());
        verify(passengerRepositoryMock).save(passengerMock);
        verify(rideUtilsMock).createPassengerRideForUsers(any(), any(), anyInt(), any());
        verify(rideServiceSpy).scheduleExecution(any(), anyLong(), any());
    }

    // passenger reject ride
    @Test
    void Throw_exception_when_attempting_to_reject_a_ride_that_does_not_exist() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> rideService.rejectRide(dto, authentication),
                "Expected rejectRide() to throw NoSuchElementException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_attempting_to_reject_a_passenger_ride_that_does_not_exist() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            passengerRidesMock.add(Mockito.mock(PassengerRide.class));
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock, "username1@noemail.com")).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> rideService.rejectRide(dto, authentication),
                "Expected rejectRide() to throw NoSuchElementException, but it didn't"
        );
    }

    @Test
    void Return_false_when_attempting_to_reject_ride_after_all_passengers_have_confirmed() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            passengerRidesMock.add(Mockito.mock(PassengerRide.class));
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        PassengerRide currentPassengerRideMock = Mockito.mock(PassengerRide.class);
        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock, "username1@noemail.com"))
                .thenReturn(Optional.of(currentPassengerRideMock));

        when(rideMock.getPassengersConfirmed()).thenReturn(true);

        assertFalse(rideService.rejectRide(dto, authentication));
    }

    @Test
    void Return_false_when_attempting_to_reject_ride_after_the_passenger_has_already_confirmed() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            passengerRidesMock.add(Mockito.mock(PassengerRide.class));
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        PassengerRide currentPassengerRideMock = Mockito.mock(PassengerRide.class);
        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock, "username1@noemail.com"))
                .thenReturn(Optional.of(currentPassengerRideMock));

        when(rideMock.getPassengersConfirmed()).thenReturn(false);
        when(currentPassengerRideMock.isAgreed()).thenReturn(true);

        assertFalse(rideService.rejectRide(dto, authentication));
    }

    @Test
    void Return_true_when_rejection_is_valid() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            passengerRidesMock.add(Mockito.mock(PassengerRide.class));
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        PassengerRide currentPassengerRideMock = Mockito.mock(PassengerRide.class);
        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock, "username1@noemail.com"))
                .thenReturn(Optional.of(currentPassengerRideMock));

        when(rideMock.getPassengersConfirmed()).thenReturn(false);
        when(currentPassengerRideMock.isAgreed()).thenReturn(false);

        List<Passenger> passengerRidesPassengersMock = new ArrayList<>();
        passengerRidesPassengersMock.add(passengerMock);
        for (int i = 2; i < 5; i++) {
            Passenger p = Mockito.mock(Passenger.class);
            when(p.getUsername()).thenReturn("username" + i + "@noemail.com");
            passengerRidesPassengersMock.add(p);
        }
        for (int i = 0; i < 4; i++) {
            when(passengerRidesMock.get(i).getPassenger()).thenReturn(passengerRidesPassengersMock.get(i));
        }

        doNothing().when(rideUtilsMock).sendMessageToPassenger(anyString(), anyString(), any());
        doNothing().when(rideUtilsMock).refundPassengers(any());
        doNothing().when(rideMock).setStatus(RideStatus.CANCELLED);
        when(rideRepositoryMock.save(rideMock)).thenReturn(rideMock);

        assertTrue(rideService.rejectRide(dto, authentication));
        verify(rideUtilsMock, times(3)).sendMessageToPassenger(anyString(), anyString(), any());
        verify(rideUtilsMock).refundPassengers(any());
        verify(rideMock).setStatus(RideStatus.CANCELLED);
        verify(rideRepositoryMock).save(rideMock);
    }

    // ride confirmation
    @Test
    void Throw_exception_when_attempting_to_confirm_a_ride_that_does_not_exist() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> rideService.confirmRide(dto, authentication),
                "Expected confirmRide() to throw NoSuchElementException, but it didn't"
        );
    }

    @Test
    void Throw_exception_when_attempting_to_confirm_a_passenger_ride_that_does_not_exist() {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            passengerRidesMock.add(Mockito.mock(PassengerRide.class));
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        List<Passenger> passengerRidesPassengersMock = new ArrayList<>();
        passengerRidesPassengersMock.add(passengerMock);
        for (int i = 0; i < 3; i++) {
            Passenger p = Mockito.mock(Passenger.class);
            when(p.getUsername()).thenReturn("username" + (i + 2) + "@noemail.com");
            passengerRidesPassengersMock.add(p);
        }
        for (int i = 0; i < 4; i++) {
            when(passengerRidesMock.get(i).getPassenger()).thenReturn(passengerRidesPassengersMock.get(i));
        }

        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock, "username1@noemail.com")).thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> rideService.confirmRide(dto, authentication),
                "Expected confirmRide() to throw NoSuchElementException, but it didn't"
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100, 999})
    void Throw_exception_when_attempting_to_confirm_a_ride_with_insufficient_token_balance(int passengerTokenBalance) {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            passengerRidesMock.add(Mockito.mock(PassengerRide.class));
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        List<Passenger> passengerRidesPassengersMock = new ArrayList<>();
        passengerRidesPassengersMock.add(passengerMock);
        for (int i = 0; i < 3; i++) {
            Passenger p = Mockito.mock(Passenger.class);
            when(p.getUsername()).thenReturn("username" + (i + 2) + "@noemail.com");
            passengerRidesPassengersMock.add(p);
        }
        for (int i = 0; i < 4; i++) {
            when(passengerRidesMock.get(i).getPassenger()).thenReturn(passengerRidesPassengersMock.get(i));
        }

        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");
        when(passengerRideRepositoryMock
                .findByRideAndPassengerUsername(any(), anyString())).thenReturn(Optional.of(passengerRideMock));

        when(passengerMock.getTokenBalance()).thenReturn(passengerTokenBalance);
        when(passengerRideMock.getFare()).thenReturn(1000);

        doNothing().when(rideUtilsMock).sendMessageToPassenger(anyString(), anyString(), any());
        doNothing().when(rideMock).setStatus(RideStatus.CANCELLED);
        when(rideRepositoryMock.save(rideMock)).thenReturn(rideMock);
        doNothing().when(rideUtilsMock).refundPassengers(anyList());

        assertThrows(
                InsufficientFundsException.class,
                () -> rideService.confirmRide(dto, authentication),
                "Expected confirmRide() to throw InsufficientFundsException, but it didn't"
        );
        verify(rideUtilsMock, times(4)).sendMessageToPassenger(anyString(), anyString(), any());
        verify(rideMock).setStatus(RideStatus.CANCELLED);
        verify(rideRepositoryMock).save(rideMock);
        verify(rideUtilsMock).refundPassengers(anyList());
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 1500, 5000})
    void Return_true_when_passenger_is_not_the_last_one_to_confirm_the_ride(int passengerTokenBalance) {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        passengerRidesMock.add(passengerRideMock);
        for (int i = 0; i < 3; i++) {
            PassengerRide pr = Mockito.mock(PassengerRide.class);
            passengerRidesMock.add(pr);
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        List<Passenger> passengerRidesPassengersMock = new ArrayList<>();
        passengerRidesPassengersMock.add(passengerMock);
        for (int i = 0; i < 3; i++) {
            Passenger p = Mockito.mock(Passenger.class);
            when(p.getUsername()).thenReturn("username" + (i + 2) + "@noemail.com");
            passengerRidesPassengersMock.add(p);
        }
        for (int i = 0; i < 4; i++) {
            when(passengerRidesMock.get(i).getPassenger()).thenReturn(passengerRidesPassengersMock.get(i));
            if (i < 3) {
                when(passengerRidesMock.get(i).isAgreed()).thenReturn(true);
            }
            else {
                when(passengerRidesMock.get(i).isAgreed()).thenReturn(false);
            }
            when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock,
                    passengerRidesMock.get(i).getPassenger().getUsername()))
                    .thenReturn(Optional.of(passengerRidesMock.get(i)));
        }

        when(passengerMock.getTokenBalance()).thenReturn(passengerTokenBalance);
        when(passengerRideMock.getFare()).thenReturn(1000);

        doNothing().when(passengerMock).setTokenBalance(anyInt());
        when(passengerRideRepositoryMock.save(passengerRideMock)).thenReturn(passengerRideMock);
        when(passengerRepositoryMock.save(passengerMock)).thenReturn(passengerMock);

        assertTrue(rideService.confirmRide(dto, authentication));
        verify(passengerRideRepositoryMock).save(passengerRideMock);
        verify(passengerRepositoryMock).save(passengerMock);
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 1500, 5000})
    void Return_true_when_passenger_is_the_last_one_to_confirm_an_immediate_ride(int passengerTokenBalance) {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        passengerRidesMock.add(passengerRideMock);
        for (int i = 0; i < 3; i++) {
            PassengerRide pr = Mockito.mock(PassengerRide.class);
            passengerRidesMock.add(pr);
        }
        when(passengerRideRepositoryMock.findByRide(rideMock)).thenReturn(passengerRidesMock);

        List<Passenger> passengerRidesPassengersMock = new ArrayList<>();
        passengerRidesPassengersMock.add(passengerMock);
        for (int i = 0; i < 3; i++) {
            Passenger p = Mockito.mock(Passenger.class);
            when(p.getUsername()).thenReturn("username" + (i + 2) + "@noemail.com");
            passengerRidesPassengersMock.add(p);
        }
        for (int i = 0; i < 4; i++) {
            when(passengerRidesMock.get(i).getPassenger()).thenReturn(passengerRidesPassengersMock.get(i));
            when(passengerRidesMock.get(i).isAgreed()).thenReturn(true);
            when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock,
                    passengerRidesMock.get(i).getPassenger().getUsername()))
                    .thenReturn(Optional.of(passengerRidesMock.get(i)));
        }

        when(passengerMock.getTokenBalance()).thenReturn(passengerTokenBalance);
        when(passengerRideMock.getFare()).thenReturn(1000);

        doNothing().when(passengerMock).setTokenBalance(anyInt());
        when(passengerRideRepositoryMock.save(passengerRideMock)).thenReturn(passengerRideMock);
        when(passengerRepositoryMock.save(passengerMock)).thenReturn(passengerMock);

        doNothing().when(rideMock).setPassengersConfirmed(true);
        doNothing().when(rideMock).setStatus(RideStatus.RESERVED);
        when(rideRepositoryMock.save(rideMock)).thenReturn(null);

        when(rideMock.getDelayInMinutes()).thenReturn(0);
        doNothing().when(rideUtilsMock).processReservation(rideMock, passengerRidesMock);

        assertTrue(rideService.confirmRide(dto, authentication));
        verify(passengerRideRepositoryMock).save(passengerRideMock);
        verify(passengerRepositoryMock).save(passengerMock);
        verify(rideMock).setPassengersConfirmed(true);
        verify(rideMock).setStatus(RideStatus.RESERVED);
        verify(rideRepositoryMock).save(rideMock);
        verify(rideUtilsMock).processReservation(rideMock, passengerRidesMock);
    }

    @ParameterizedTest
    @ValueSource(ints = {1000, 1500, 5000})
    void Return_true_when_passenger_is_the_last_one_to_confirm_a_scheduled_ride(int passengerTokenBalance) {
        RideIdDTO dto = mock(RideIdDTO.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(passengerMock);
        when(passengerMock.getUsername()).thenReturn("username1@noemail.com");

        when(dto.getRideId()).thenReturn(0);
        when(rideRepositoryMock.findById(0)).thenReturn(Optional.of(rideMock));

        List<PassengerRide> passengerRidesMock = new ArrayList<>();
        passengerRidesMock.add(passengerRideMock);
        for (int i = 0; i < 3; i++) {
            PassengerRide pr = Mockito.mock(PassengerRide.class);
            passengerRidesMock.add(pr);
        }
        when(passengerRideRepositoryMock.findByRide(any())).thenReturn(passengerRidesMock);

        List<Passenger> passengerRidesPassengersMock = new ArrayList<>();
        passengerRidesPassengersMock.add(passengerMock);
        for (int i = 0; i < 3; i++) {
            Passenger p = Mockito.mock(Passenger.class);
            when(p.getUsername()).thenReturn("username" + (i + 2) + "@noemail.com");
            passengerRidesPassengersMock.add(p);
        }
        for (int i = 0; i < 4; i++) {
            when(passengerRidesMock.get(i).getPassenger()).thenReturn(passengerRidesPassengersMock.get(i));
            when(passengerRidesMock.get(i).isAgreed()).thenReturn(true);
            when(passengerRideRepositoryMock.findByRideAndPassengerUsername(rideMock,
                    passengerRidesMock.get(i).getPassenger().getUsername()))
                    .thenReturn(Optional.of(passengerRidesMock.get(i)));
        }

        when(passengerMock.getTokenBalance()).thenReturn(passengerTokenBalance);
        when(passengerRideMock.getFare()).thenReturn(1000);

        doNothing().when(passengerMock).setTokenBalance(anyInt());
        when(passengerRideRepositoryMock.save(passengerRideMock)).thenReturn(passengerRideMock);
        when(passengerRepositoryMock.save(passengerMock)).thenReturn(passengerMock);

        doNothing().when(rideMock).setPassengersConfirmed(true);
        doNothing().when(rideMock).setStatus(RideStatus.RESERVED);
        when(rideRepositoryMock.save(rideMock)).thenReturn(null);

        when(rideMock.getDelayInMinutes()).thenReturn(20);
        doNothing().when(rideUtilsMock).sendRefreshMessageToMultipleUsers(anyList());
        doNothing().when(rideUtilsMock).handleNotificationsAndProcessReservations(rideMock, passengerRidesMock);

        assertTrue(rideService.confirmRide(dto, authentication));
        verify(passengerRideRepositoryMock).save(passengerRideMock);
        verify(passengerRepositoryMock).save(passengerMock);
        verify(rideMock).setPassengersConfirmed(true);
        verify(rideMock).setStatus(RideStatus.RESERVED);
        verify(rideRepositoryMock).save(rideMock);
        verify(rideUtilsMock).sendRefreshMessageToMultipleUsers(anyList());
        verify(rideUtilsMock).handleNotificationsAndProcessReservations(rideMock, passengerRidesMock);
    }
}
