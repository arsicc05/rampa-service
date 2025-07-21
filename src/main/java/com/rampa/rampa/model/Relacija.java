package com.rampa.rampa.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Relacija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;

    @ManyToOne
    private Stanica prvaStanica;

    @ManyToOne
    private Stanica drugaStanica;

    @OneToMany
    private List<Vozilo> vozila;

    public Relacija(String naziv, Stanica prvaStanica, Stanica drugaStanica) {
        this.naziv = naziv;
        this.prvaStanica = prvaStanica;
        this.drugaStanica = drugaStanica;
        this.vozila = new ArrayList<>();
    }

    public Relacija(String naziv, Stanica prvaStanica, Stanica drugaStanica, List<Vozilo> vozila) {
        this.naziv = naziv;
        this.prvaStanica = prvaStanica;
        this.drugaStanica = drugaStanica;
        this.vozila = vozila;
    }

    public Relacija() {
        this.vozila = new ArrayList<>();
    }
}
