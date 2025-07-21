package com.rampa.rampa.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Korisnik {
    // Add admin-specific fields or methods here if needed
}

