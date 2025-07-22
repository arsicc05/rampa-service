package com.rampa.rampa.repository;

import com.rampa.rampa.model.Korisnik;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KorisnikRepository extends JpaRepository<Korisnik, Long> {

    Korisnik findByUsername(String username);
}
