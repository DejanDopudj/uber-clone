package com.example.springbackend.repository;

import com.example.springbackend.model.Admin;
import com.example.springbackend.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Integer> {


    @Query(value = "SELECT  cast(ride.startTime as date), SUM(ride.distance)\n" +
            "            FROM \n" +
            "    Ride ride where cast(ride.startTime as date) >= ?1 and" +
            " cast(ride.startTime as date) <= ?2 and ride.driver.username = ?3" +
            "      group by cast(ride.startTime as date) order by" +
            " cast(ride.startTime as date)")
    List<Object[]> getDriverDistanceReport(Date startDate, Date endDate, String username);
    @Query(value = "SELECT  cast(ride.startTime as date), count (ride)\n" +
            "            FROM \n" +
            "    Ride ride where cast(ride.startTime as date) >= ?1 and" +
            " cast(ride.startTime as date) <= ?2 and ride.driver.username = ?3" +
            "      group by cast(ride.startTime as date) order by" +
            " cast(ride.startTime as date)")
    List<Object[]> getDriverRidesReport(Date startDate, Date endDate, String username);
    @Query(value = "SELECT  cast(ride.startTime as date), SUM(ride.price)\n" +
            "            FROM \n" +
            "    Ride ride where cast(ride.startTime as date) >= ?1 and" +
            " cast(ride.startTime as date) <= ?2 and ride.driver.username = ?3" +
            "      group by cast(ride.startTime as date) order by" +
            " cast(ride.startTime as date)")
    List<Object[]> getDriverMoneyReport(Date startDate, Date endDate, String username);
}
