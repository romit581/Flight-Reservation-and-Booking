package com.example.flight.controller;

import com.example.flight.model.Booking;
import com.example.flight.model.Flight;
import com.example.flight.model.User;
import com.example.flight.repository.BookingRepository;
import com.example.flight.repository.FlightRepository;
import com.example.flight.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bookings")
public class BookingController {


    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;


    public BookingController(BookingRepository bookingRepository, UserRepository userRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody CreateBookingReq req, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        Flight flight = flightRepository.findById(req.flightId()).orElseThrow();
        if (flight.getSeatsAvailable() == null || flight.getSeatsAvailable() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "no_seats"));
        }
// decrement seats
        flight.setSeatsAvailable(flight.getSeatsAvailable() - 1);
        flightRepository.save(flight);


        Booking booking = Booking.builder().user(user).flight(flight).passengerName(req.passengerName()).seatNo(req.seatNo()).status("CONFIRMED").build();
        bookingRepository.save(booking);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/history")
    public List<Booking> history(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return bookingRepository.findByUser(user);
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Authentication auth) {
        Optional<Booking> opt = bookingRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Booking b = opt.get();
        if (!b.getUser().getUsername().equals(auth.getName())) return ResponseEntity.status(403).build();
// update status and increase seats
        b.setStatus("CANCELLED");
        bookingRepository.save(b);
        Flight flight = b.getFlight();
        flight.setSeatsAvailable(flight.getSeatsAvailable() + 1);
        flightRepository.save(flight);
        return ResponseEntity.ok(Map.of("cancelled", id));
    }

    record CreateBookingReq(Long flightId, String passengerName, String seatNo) {
    }
}