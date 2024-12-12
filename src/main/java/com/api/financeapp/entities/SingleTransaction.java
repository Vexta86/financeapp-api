package com.api.financeapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;

import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@Entity
public class SingleTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Double amount;


    @Setter
    @Column(nullable = false)
    private String description;

    @Setter
    @ManyToOne
    @JoinColumn()
    private Category category;

    @Setter
    @ManyToOne
    @JoinColumn()
    private User user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate date;
}
