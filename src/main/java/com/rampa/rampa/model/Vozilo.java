package com.rampa.rampa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Setter
@Getter
public class Vozilo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String registracija;
    private VoziloType tip;
    private Date vremeUlaska;
    private Date vremeIzlaska;
    private VoziloStatus status;

    @ManyToMany(mappedBy = "vozila")
    @JsonBackReference
    private List<Relacija> relacije = new ArrayList<>();
}
