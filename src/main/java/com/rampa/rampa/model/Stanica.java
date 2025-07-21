package com.rampa.rampa.model;

import jakarta.persistence.Id;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Stanica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;

    @OneToMany
    private List<Rampa> rampe;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    public Stanica() {
        this.rampe = new ArrayList<>();
    }

    public Stanica(String naziv, StatusEnum status) {
        this.naziv = naziv;
        this.status = status;
        this.rampe = new ArrayList<>();
    }

}