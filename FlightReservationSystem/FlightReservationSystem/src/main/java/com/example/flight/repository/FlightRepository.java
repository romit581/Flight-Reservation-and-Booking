package com.example.flight.repository;


import com.example.flight.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;


public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.source = :source AND f.destination = :destination AND DATE(f.departureTime) = :date")
    List<Flight> search(@Param("source") String source, @Param("destination") String destination, @Param("date") Instant date);


    List<Flight> findBySourceAndDestination(String source, String destination);
}