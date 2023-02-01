package com.example.springbackend.controller;

import com.example.springbackend.dto.creation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional(Transactional.TxType.REQUIRED)
public class RideControllerIntegrationTests {
    private static final String URL_PREFIX = "/api/rides";
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    // basic ride
    @Test
    @DisplayName("Should return 200 and the created basic ride [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_200_and_ride_when_ordering_a_valid_basic_ride() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.driver.name")
                                .value("Travis"))
                        .andExpect(jsonPath("$.startAddress")
                                .value("Example starting address 1, Novi Sad"))
                        .andExpect(jsonPath("$.distance")
                                .value(5.0));
    }

    @Test
    @DisplayName("Should return 400 when attempting to order a basic ride with a " +
            "non-existent vehicle type [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_404_when_requesting_a_basic_ride_with_a_non_existent_vehicle_type() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();
        dto.setVehicleType("INVALID_VEHICLE_TYPE");

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(404))
                .andReturn();
    }

    @Test
    @DisplayName("Should return 422 when attempting to order a basic ride " +
            "while having an active ride [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_422_when_ordering_a_basic_ride_while_having_an_active_ride() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(200));

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().is(422))
                        .andExpect(jsonPath("$.message")
                                .value("Passenger already has an active ride."));
        
    }

    @Test
    @DisplayName("Should return 402 when attempting to order a basic ride " +
            "without having sufficient funds [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_402_when_ordering_a_basic_ride_without_having_sufficient_funds() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();
        dto.setDistance(50.0);
        dto.setExpectedTime(4500);

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().is(402));
    }

    @Test
    @DisplayName("Should return 422 when ordering a basic ride " +
            "and a driver is not found [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_422_when_ordering_a_basic_ride_and_a_driver_is_not_found() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();
        dto.getRoute().setWaypoints(dto.getRoute().getWaypoints().stream().map(wp -> {
            wp.setLat(wp.getLat() + 1);
            wp.setLng(wp.getLng() + 1);
            return wp;
        }).toList());

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().is(422))
                        .andExpect(jsonPath("$.message")
                                .value("Adequate driver not found."));
    }

    @ParameterizedTest
    @ValueSource(ints = {20, 50, 299})
    @DisplayName("Should return 200 and the created scheduled basic ride [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_200_and_ride_when_ordering_a_valid_scheduled_basic_ride(int delayInMinutes) throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();
        dto.setDelayInMinutes(delayInMinutes);

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.startAddress")
                        .value("Example starting address 1, Novi Sad"))
                        .andExpect(jsonPath("$.distance")
                        .value(5.0));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 301, 1000})
    @DisplayName("Should return 400 when ordering a scheduled basic ride " +
            "and the delay value is outside of the min-max range [0, 300] [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_400_when_ordering_a_scheduled_basic_ride_and_the_delay_value_is_invalid(int delayInMinutes) throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();
        dto.setDelayInMinutes(delayInMinutes);

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 19})
    @DisplayName("Should return 400 when ordering a scheduled basic ride " +
            "and the delay value is less than 20 [POST] " + URL_PREFIX + "/basic")
    @Rollback
    void Return_400_when_ordering_a_scheduled_basic_ride_and_the_delay_value_is_less_than_20(int delayInMinutes) throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        BasicRideCreationDTO dto = IntegrationUtils.getValidBasicRideCreationDto();
        dto.setDelayInMinutes(delayInMinutes);

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().is(400))
                        .andExpect(jsonPath("$.message")
                                .value("Reservation must be made at least 20 minutes in advance."));
    }


    // split fare ride
    @Test
    @DisplayName("Should return 200 and the created split fare ride [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_200_and_ride_when_ordering_a_valid_split_fare_ride() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startAddress")
                        .value("Example starting address 1, Novi Sad"))
                .andExpect(jsonPath("$.distance")
                        .value(5.0));
    }

    @Test
    @DisplayName("Should return 404 when attempting to order a split fare ride with a " +
            "non-existent vehicle type [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_404_when_requesting_a_split_fare_ride_with_a_non_existent_vehicle_type() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("INVALID_VEHICLE_TYPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(404));
    }

    @Test
    @DisplayName("Should return 400 when attempting to order a split fare ride with a " +
            "non-existent co-passenger [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_400_when_requesting_a_split_fare_ride_with_a_non_existent_co_passenger() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger_invalid@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("A co-passenger's email does not exist in the system."));
    }

    @Test
    @DisplayName("Should return 400 when attempting to order a split fare ride with a " +
            "duplicate co-passenger [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_400_when_requesting_a_split_fare_ride_with_a_duplicate_co_passenger() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger2@noemail.com");

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("Not all linked passengers are distinct."));
    }

    @Test
    @DisplayName("Should return 400 when attempting to order a split fare ride with " +
            "too many passengers [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_400_when_requesting_a_split_fare_ride_with_too_many_passengers() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");
        dto.getUsersToPay().add("passenger4@noemail.com");
        dto.getUsersToPay().add("passenger5@noemail.com");

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("Number of passengers exceeds vehicle capacity."));
    }

    @Test
    @DisplayName("Should return 422 when attempting to order a split fare ride when " +
            "a linked passenger has an active ride [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_422_when_requesting_a_split_fare_ride_when_a_passenger_has_an_active_ride() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");
        dto.getUsersToPay().add("passenger4@noemail.com");

        String passenger2Token = IntegrationUtils.getToken(mockMvc, "passenger2@noemail.com");
        BasicRideCreationDTO dtoBasic = IntegrationUtils.getValidBasicRideCreationDto();

        mockMvc.perform(post(URL_PREFIX + "/basic")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dtoBasic))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passenger2Token))
                .andExpect(status().isOk());

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                        .andExpect(status().is(422))
                        .andExpect(jsonPath("$.message")
                                .value("Passenger already has an active ride."));
    }

    @Test
    @DisplayName("Should return 402 when attempting to order a split fare ride without " +
            "sufficient funds [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_402_when_requesting_a_split_fare_ride_with_too_many_passengers() throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");
        dto.setDistance(95.0);
        dto.setExpectedTime(11000);

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(402))
                .andExpect(jsonPath("$.message")
                        .value("Insufficient funds."));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, -1, 301, 1000})
    @DisplayName("Should return 400 when ordering a scheduled split fare ride " +
            "and the delay value is outside of the min-max range [0, 300] [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_400_when_ordering_a_scheduled_split_fare_ride_and_the_delay_value_is_invalid(int delayInMinutes) throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");
        dto.setDelayInMinutes(delayInMinutes);

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(400));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10, 19})
    @DisplayName("Should return 400 when ordering a scheduled split fare ride " +
            "and the delay value is less than 20 [POST] " + URL_PREFIX + "/split-fare")
    @Rollback
    void Return_400_when_ordering_a_scheduled_split_fare_ride_and_the_delay_value_is_less_than_20(int delayInMinutes) throws Exception {
        String passengerToken = IntegrationUtils.getToken(mockMvc, "passenger1@noemail.com");
        SplitFareRideCreationDTO dto = IntegrationUtils.getValidSplitFareRideCreationDto();
        dto.setVehicleType("COUPE");
        dto.getUsersToPay().add("passenger2@noemail.com");
        dto.getUsersToPay().add("passenger3@noemail.com");
        dto.setDelayInMinutes(delayInMinutes);

        mockMvc.perform(post(URL_PREFIX + "/split-fare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + passengerToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.message")
                        .value("Reservation must be made at least 20 minutes in advance."));
    }
}
