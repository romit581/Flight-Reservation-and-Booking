package com.example.flight.controller;


import com.example.flight.repository.BookingRepository;
import com.example.flight.repository.PaymentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/reports")
public class AdminReportController {


    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;


    public AdminReportController(BookingRepository bookingRepository, PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }


    @GetMapping
    public Map<String, Object> getReport() {
        long bookings = bookingRepository.count();
        long payments = paymentRepository.count();
// add more aggregations as needed
        return Map.of("totalBookings", bookings, "totalPayments", payments);
    }
}