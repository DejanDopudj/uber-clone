package com.example.springbackend.repository;

import com.example.springbackend.model.*;
import com.example.springbackend.model.helpClasses.Coordinates;
import com.example.springbackend.service.TestDataSupplierService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
@ActiveProfiles("test")
public class RideRepositoryTests {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    RideRepository rideRepository;
    @Autowired
    PassengerRideRepository passengerRideRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    VehicleTypeRepository vehicleTypeRepository;

    @MockBean
    TestDataSupplierService testDataSupplierService;


    //getClosestFreeDriver
    @Test
    void Return_free_driver_within_five_kilometers() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(41.245782, 19.851122));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(driverPage.getTotalElements(), 1);
        assertEquals(driverPage.getContent().get(0), driver1);
    }

    @Test
    void Return_no_driver_if_none_are_within_five_kilometers() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(43.252782, 19.855517));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(41.245782, 19.851122));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(0, driverPage.getTotalElements());
    }

    @Test
    void Return_the_closer_driver() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(2, driverPage.getTotalElements());
        assertEquals(driverPage.getContent().get(0), driver2);
    }


    @Test
    void Return_no_driver_if_none_are_active() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(false);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(false);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(0, driverPage.getTotalElements());
    }

    @Test
    void Return_the_active_driver() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(false);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(1, driverPage.getTotalElements());
        assertEquals(driverPage.getContent().get(0), driver2);
    }

    @Test
    void Return_no_driver_if_all_have_an_active_ride() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(true);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Ride ride1 = new Ride();
        entityManager.persist(ride1);
        Driver driver1 = new Driver();
        driver1.setCurrentRide(ride1);
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(true);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Ride ride2 = new Ride();
        entityManager.persist(ride2);
        Driver driver2 = new Driver();
        driver2.setCurrentRide(ride2);
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(0, driverPage.getTotalElements());
    }

    @Test
    void Return_no_driver_if_none_meet_the_baby_seat_criteria() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(true);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, true,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(0, driverPage.getTotalElements());
    }

    @Test
    void Return_the_driver_that_meets_the_baby_seat_criteria() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(true);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, true,
                        false, "COUPE", PageRequest.of(0, 1));

        assertEquals(1, driverPage.getTotalElements());
        assertEquals(driverPage.getContent().get(0), driver2);
    }

    @Test
    void Return_no_driver_if_none_meet_the_pets_allowed_criteria() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(true);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        true, "COUPE", PageRequest.of(0, 1));

        assertEquals(0, driverPage.getTotalElements());
    }

    @Test
    void Return_the_driver_that_meets_the_pets_allowed_criteria() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(true);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        true, "COUPE", PageRequest.of(0, 1));

        assertEquals(1, driverPage.getTotalElements());
        assertEquals(driverPage.getContent().get(0), driver2);
    }

    @Test
    void Return_no_driver_if_none_meet_the_vehicle_type_criteria() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("MINIVAN").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "STATION", PageRequest.of(0, 1));

        assertEquals(0, driverPage.getTotalElements());
    }

    @Test
    void Return_the_driver_that_meets_the_vehicle_type_criteria() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setRideActive(false);
        vehicle1.setVehicleType(vehicleTypeRepository.findByName("COUPE").get());
        vehicle1.setCurrentCoordinates(new Coordinates(45.245782, 19.851122));
        vehicle1.setBabySeat(false);
        vehicle1.setPetsAllowed(false);
        entityManager.persist(vehicle1);
        Driver driver1 = new Driver();
        driver1.setActive(true);
        driver1.setVehicle(vehicle1);
        driver1.setUsername("driver1@noemail.com");
        entityManager.persist(driver1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setRideActive(false);
        vehicle2.setVehicleType(vehicleTypeRepository.findByName("MINIVAN").get());
        vehicle2.setCurrentCoordinates(new Coordinates(45.252782, 19.855517));
        vehicle2.setBabySeat(false);
        vehicle2.setPetsAllowed(false);
        entityManager.persist(vehicle2);
        Driver driver2 = new Driver();
        driver2.setActive(true);
        driver2.setVehicle(vehicle2);
        driver2.setUsername("driver2@noemail.com");
        entityManager.persist(driver2);

        Page<Driver> driverPage = driverRepository
                .getClosestFreeDriver(45.245749, 19.851122, false,
                        false, "MINIVAN", PageRequest.of(0, 1));

        assertEquals(1, driverPage.getTotalElements());
        assertEquals(driverPage.getContent().get(0), driver2);
    }

}
