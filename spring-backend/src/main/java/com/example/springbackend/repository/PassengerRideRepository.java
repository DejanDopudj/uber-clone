package com.example.springbackend.repository;

import com.example.springbackend.model.Passenger;
import com.example.springbackend.model.PassengerRide;
import com.example.springbackend.model.Ride;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PassengerRideRepository extends JpaRepository<PassengerRide, Integer> {
    @Query("SELECT pr.ride FROM PassengerRide pr WHERE " +
            "pr.passenger = :passenger AND pr.ride.rejected = false " +
            "AND pr.ride.endTime is null")
    Optional<Ride> getCurrentRide(@Param("passenger") Passenger passenger);

    Optional<PassengerRide> findByRideAndPassengerUsername(Ride ride, String username);
    Optional<PassengerRide> findByRideIdAndPassengerUsername(Integer rideId, String username);
    List<PassengerRide> findByRideId(Integer rideId);

    Page<PassengerRide> findByPassengerUsername(String username, Pageable paging);

    @Query("SELECT  function('date_trunc', 'day',pr.ride.startTime), SUM (pr.ride.distance) FROM PassengerRide pr inner JOIN pr.passenger passenger where pr.ride.startTime > ?1 and pr.ride.endTime < ?2 and pr.passenger.username = ?3"+
            "group by function('date_trunc', 'day',pr.ride.startTime) order by function('date_trunc','day',pr.ride.startTime)")
    List<Object[]> getPassengerDistanceReport(LocalDateTime startDate, LocalDateTime endDate, String username);

    @Query("SELECT  function('date_trunc', 'day',pr.ride.startTime), COUNT (pr.ride) FROM PassengerRide pr inner JOIN pr.passenger passenger where pr.ride.startTime > ?1 and pr.ride.endTime < ?2 and pr.passenger.username = ?3"+
            "group by function('date_trunc', 'day',pr.ride.startTime) order by function('date_trunc','day',pr.ride.startTime)")
    List<Object[]> getPassengerRidesReport(LocalDateTime startDate, LocalDateTime endDate, String username);

    @Query("SELECT  function('date_trunc', 'day',pr.ride.startTime), SUM (pr.fare) FROM PassengerRide pr inner JOIN pr.passenger passenger where pr.ride.startTime > ?1 and pr.ride.endTime < ?2 and pr.passenger.username = ?3"+
            "group by function('date_trunc', 'day',pr.ride.startTime) order by function('date_trunc','day',pr.ride.startTime)")
    List<Object[]> getPassengersMoneyReport(LocalDateTime startDate, LocalDateTime endDate, String username);

    @Query("SELECT  function('date_trunc', 'day',pr.ride.startTime), SUM (pr.ride.distance) FROM PassengerRide pr inner JOIN pr.passenger passenger where pr.ride.startTime > ?1 and pr.ride.endTime < ?2 and pr.ride.driver.username = ?3"+
            "group by function('date_trunc', 'day',pr.ride.startTime) order by function('date_trunc','day',pr.ride.startTime)")
    List<Object[]> getDriversDistanceReport(LocalDateTime startDate, LocalDateTime endDate, String username);

    @Query("SELECT  function('date_trunc', 'day',pr.ride.startTime), COUNT (pr.ride) FROM PassengerRide pr inner JOIN pr.passenger passenger where pr.ride.startTime > ?1 and pr.ride.endTime < ?2 and pr.ride.driver.username = ?3"+
            "group by function('date_trunc', 'day',pr.ride.startTime) order by function('date_trunc','day',pr.ride.startTime)")
    List<Object[]> getDriversRidesReport(LocalDateTime startDate, LocalDateTime endDate, String username);

    @Query("SELECT  function('date_trunc', 'day',pr.ride.startTime), SUM (pr.fare) FROM PassengerRide pr inner JOIN pr.passenger passenger where pr.ride.startTime > ?1 and pr.ride.endTime < ?2 and pr.ride.driver.username = ?3"+
            "group by function('date_trunc', 'day',pr.ride.startTime) order by function('date_trunc','day',pr.ride.startTime)")
    List<Object[]> getDriversMoneyReport(LocalDateTime startDate, LocalDateTime endDate, String username);



    @Query("SELECT pr.passenger.username FROM PassengerRide pr WHERE " +
            "pr.ride.id = :rideId")
    List<String> getPassengersForRide(@Param("rideId") Integer rideId);
}

