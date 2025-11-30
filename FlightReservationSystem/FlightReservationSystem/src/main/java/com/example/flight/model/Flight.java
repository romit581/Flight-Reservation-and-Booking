package com.example.flight.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightId;


    private String airline;
    private String source;
    private String destination;
    private Instant departureTime;
    private Instant arrivalTime;
    private Double fare;
    private Integer seatsAvailable;
}