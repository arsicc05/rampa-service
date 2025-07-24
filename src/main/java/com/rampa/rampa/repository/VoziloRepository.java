package com.rampa.rampa.repository;

import com.rampa.rampa.model.Vozilo;
import com.rampa.rampa.model.VoziloStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoziloRepository extends JpaRepository<Vozilo, Long> {
    List<Vozilo> findByStatus(VoziloStatus status);
    List<Vozilo> findByRegistracija(String registracija);
}
