package com.example.springbackend.service;

import com.example.springbackend.dto.creation.BasicRideCreationDTO;
import com.example.springbackend.dto.creation.CoordinatesCreationDTO;
import com.example.springbackend.dto.display.DriverSimpleDisplayDTO;
import com.example.springbackend.dto.display.RideSimpleDisplayDTO;
import com.example.springbackend.exception.AdequateDriverNotFoundException;
import com.example.springbackend.exception.InsufficientFundsException;
import com.example.springbackend.model.*;
import com.example.springbackend.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RideService {

    @Autowired
    RideRepository rideRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    PassengerRepository passengerRepository;
    @Autowired
    PassengerRideRepository passengerRideRepository;
    @Autowired
    VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    ModelMapper modelMapper;

    public RideSimpleDisplayDTO orderBasicRide(BasicRideCreationDTO dto, Authentication auth) {
        Passenger passenger = (Passenger) auth.getPrincipal();
        VehicleType vehicleType = vehicleTypeRepository.findByName(dto.getVehicleType()).orElseThrow();

        int price = calculateRidePrice(dto, vehicleType);
        if (passenger.getTokenBalance() < price) {
            throw new InsufficientFundsException();
        }

        Driver driver = findDriver(dto);
        if (driver == null) {
            throw new AdequateDriverNotFoundException();
        }

        // successful
        passenger.setTokenBalance(passenger.getTokenBalance() - price);
        passengerRepository.save(passenger);
        Ride ride = createRide(dto, price, driver);
        createPassengerRide(passenger, ride);

        // send notification to driver

        RideSimpleDisplayDTO rideDisplayDTO = modelMapper.map(ride, RideSimpleDisplayDTO.class);
        rideDisplayDTO.setDriver(modelMapper.map(driver, DriverSimpleDisplayDTO.class));

        return rideDisplayDTO;
    }

    private void createPassengerRide(Passenger passenger, Ride ride) {
        PassengerRide passengerRide = new PassengerRide();
        passengerRide.setPassenger(passenger);
        passengerRide.setRide(ride);
        passengerRide.setFare(ride.getPrice());
        passengerRideRepository.save(passengerRide);
    }

    private Ride createRide(BasicRideCreationDTO dto, int price, Driver driver) {
        Ride ride = new Ride();
        Route actualRoute = modelMapper.map(dto.getActualRoute(), Route.class);
        routeRepository.save(actualRoute);
        Route expectedRoute = null;
        if (dto.getExpectedRoute() != null) {
            expectedRoute = modelMapper.map(dto.getExpectedRoute(), Route.class);
            routeRepository.save(expectedRoute);
        }
        ride.setDistance(dto.getDistance());
        ride.setActualRoute(actualRoute);
        ride.setExpectedRoute(expectedRoute);
        ride.setCancelled("");
        ride.setRejected(false);
        ride.setStartTime(null);
        ride.setEndTime(null);
        ride.setCreatedAt(LocalDateTime.now());
        ride.setDriverInconsistency(false);
        ride.setPrice(price);
        rideRepository.save(ride);

        if (driver.getCurrentRide() == null) {
            driver.setCurrentRide(ride);
        } else {
            driver.setNextRide(ride);
        }
        driverRepository.save(driver);

        return ride;
    }

    private Driver findDriver(BasicRideCreationDTO dto) {
        CoordinatesCreationDTO startCoordinates = dto.getActualRoute().getWaypoints().get(0);

        // TODO: INCLUDE BABY SEAT AND PET FRIENDLY PARAMS IN DRIVER SEARCH

        List<Driver> potentialClosestDriver = driverRepository.getClosestFreeDriver(startCoordinates.getLat(),
                startCoordinates.getLng(), PageRequest.of(0, 1)).stream().toList();
        if (!potentialClosestDriver.isEmpty()) return potentialClosestDriver.get(0);

        List<Driver> closeBusyDriversWithNoNextRide = driverRepository
                .getCloseBusyDriversWithNoNextRide(startCoordinates.getLat(), startCoordinates.getLng());

        if (closeBusyDriversWithNoNextRide.isEmpty()) return null;
        else if (closeBusyDriversWithNoNextRide.size() == 1) return closeBusyDriversWithNoNextRide.get(0);

        Driver bestChoice = closeBusyDriversWithNoNextRide.get(0);
        long minSecondsUntilRideEnd = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                bestChoice.getVehicle().getCoordinatesChangedAt()) - bestChoice.getVehicle().getExpectedTripTime();

        for (Driver d : closeBusyDriversWithNoNextRide) {
            long secondsUntilRideEnd = ChronoUnit.SECONDS.between(LocalDateTime.now(),
                    d.getVehicle().getCoordinatesChangedAt()) - d.getVehicle().getExpectedTripTime();
            if (secondsUntilRideEnd < minSecondsUntilRideEnd) {
                bestChoice = d;
                minSecondsUntilRideEnd = secondsUntilRideEnd;
            }
        }

        return bestChoice;
    }

    private int calculateRidePrice(BasicRideCreationDTO dto, VehicleType vehicleType) {
        return (int) Math.round(vehicleType.getPrice() + dto.getDistance() * 120);
    }
}
