package com.example.flight.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;


@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;


    @OneToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;


    private Double amount;
    private String paymentStatus;
    private Instant paymentTime = Instant.now();
}