package com.rampa.rampa.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
        name = "relacija_vozila",
        joinColumns = @JoinColumn(name = "relacija_id"),
        inverseJoinColumns = @JoinColumn(name = "vozilo_id")
    )
    @JsonManagedReference
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Stanica getPrvaStanica() {
        return prvaStanica;
    }

    public void setPrvaStanica(Stanica prvaStanica) {
        this.prvaStanica = prvaStanica;
    }

    public Stanica getDrugaStanica() {
        return drugaStanica;
    }

    public void setDrugaStanica(Stanica drugaStanica) {
        this.drugaStanica = drugaStanica;
    }

    public List<Vozilo> getVozila() {
        return vozila;
    }

    public void setVozila(List<Vozilo> vozila) {
        this.vozila = vozila;
    }
}
