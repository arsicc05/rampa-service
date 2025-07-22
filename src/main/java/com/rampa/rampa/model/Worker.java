package com.rampa.rampa.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("WORKER")
public class Worker extends Korisnik {

}
