package com.rampa.rampa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Rampa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int redniBroj;
    private RampaType tip;
    private RampaStatus status;

    @Enumerated(EnumType.STRING)
    private RampaPosition position = RampaPosition.SPUSTENA;

    public Rampa() {}

    public Rampa(int redniBroj, RampaType tip, RampaStatus status) {
        this.redniBroj = redniBroj;
        this.tip = tip;
        this.status = status;
        this.position = RampaPosition.SPUSTENA;
    }
}
