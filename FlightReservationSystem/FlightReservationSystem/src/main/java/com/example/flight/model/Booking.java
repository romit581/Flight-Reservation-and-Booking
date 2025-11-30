package com.example.flight.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;


    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne(optional = false)
    @JoinColumn(name = "flight_id")
    private Flight flight;


    private String passengerName;
    private String seatNo;
    private String status = "CONFIRMED";
    private Instant bookingTime = Instant.now();
}