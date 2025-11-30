package com.example.flight.controller;


import com.example.flight.model.Flight;
import com.example.flight.repository.FlightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/flights")
public class AdminController {


    private final FlightRepository flightRepository;


    public AdminController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody Flight flight) {
        Flight saved = flightRepository.save(flight);
        return ResponseEntity.ok(saved);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Flight updated) {
        Optional<Flight> opt = flightRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Flight f = opt.get();
        f.setAirline(updated.getAirline());
        f.setSource(updated.getSource());
        f.setDestination(updated.getDestination());
        f.setDepartureTime(updated.getDepartureTime());
        f.setArrivalTime(updated.getArrivalTime());
        f.setFare(updated.getFare());
        f.setSeatsAvailable(updated.getSeatsAvailable());
        flightRepository.save(f);
        return ResponseEntity.ok(f);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        flightRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id));
    }
}