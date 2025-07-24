package com.rampa.rampa.model;

import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Stanica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;

    @OneToMany
    private List<Rampa> rampe;

    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private BigDecimal zaradaEur = BigDecimal.ZERO;
    private BigDecimal zaradaRsd = BigDecimal.ZERO;

    public Stanica() {
        this.rampe = new ArrayList<>();
        this.zaradaEur = BigDecimal.ZERO;
        this.zaradaRsd = BigDecimal.ZERO;
    }

    public Stanica(String naziv, StatusEnum status) {
        this.naziv = naziv;
        this.status = status;
        this.rampe = new ArrayList<>();
        this.zaradaEur = BigDecimal.ZERO;
        this.zaradaRsd = BigDecimal.ZERO;
    }

}