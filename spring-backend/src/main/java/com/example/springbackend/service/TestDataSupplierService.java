package com.example.springbackend.service;

import com.example.springbackend.model.Driver;
import com.example.springbackend.model.Member;
import com.example.springbackend.model.Vehicle;
import com.example.springbackend.model.VehicleType;
import com.example.springbackend.repository.DriverRepository;
import com.example.springbackend.repository.UserRepository;
import com.example.springbackend.repository.VehicleRepository;
import com.example.springbackend.repository.VehicleTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.lang.reflect.Array;
import java.util.ArrayList;

@Service
public class TestDataSupplierService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    DriverRepository driverRepository;
    @Autowired
    VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void injectTestData() {
        addUsers();
        ArrayList<VehicleType> vehicleTypes = generateVehicleTypes();
        addDrivers(vehicleTypes);
    }


    private void addUsers() {
        Member member = new Member();
        member.setUsername("member1");
        member.setEmail("member1@noemail.com");
        member.setPassword(passwordEncoder.encode("cascaded"));
        member.setName("Membrane");
        member.setSurname("Memphis");
        member.setPhoneNumber("06146014691");
        member.setCity("Novi Sad");
        member.setBlocked(false);
        userRepository.save(member);
    }

    private void addDrivers(ArrayList<VehicleType> vehicleTypes) {
        Vehicle vehicle = new Vehicle();
        vehicle.setBabySeat(true);
        vehicle.setPetsAllowed(true);
        vehicle.setMake("Checker");
        vehicle.setModel("Marathon A11");
        vehicle.setColour("Yellow");
        vehicle.setVehicleType(vehicleTypes.get(0));
        vehicleRepository.save(vehicle);
        Driver driver = new Driver();
        driver.setUsername("driver1");
        driver.setEmail("driver1@noemail.com");
        driver.setPassword(passwordEncoder.encode("cascaded"));
        driver.setName("Travis");
        driver.setSurname("Bickle");
        driver.setPhoneNumber("+1 422 135 12");
        driver.setCity("New York City");
        driver.setBlocked(false);
        driver.setVehicle(vehicle);
        driver.setDistanceTravelled(5251.12);
        driver.setRidesCompleted(2153);
        driver.setTotalRatingSum(7814);
        driver.setNumberOfReviews(1693);
        driverRepository.save(driver);
    }
    private ArrayList<VehicleType> generateVehicleTypes() {
        ArrayList<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
        VehicleType vt1 = new VehicleType();
        vt1.setPrice(1.535);
        vt1.setName("Coupe");
        vehicleTypes.add(vt1);
        vehicleTypeRepository.save(vt1);
        return vehicleTypes;
    }

}
