package com.example.flight.controller;


import com.example.flight.model.Flight;
import com.example.flight.repository.FlightRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {


    private final FlightRepository flightRepository;


    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }


    @GetMapping
    public List<Flight> all() {
        return flightRepository.findAll();
    }


    @GetMapping("/search")
    public List<Flight> search(@RequestParam String source, @RequestParam String destination, @RequestParam(required = false) Instant date) {
        if (date != null) return flightRepository.search(source, destination, date);
        return flightRepository.findBySourceAndDestination(source, destination);
    }
}