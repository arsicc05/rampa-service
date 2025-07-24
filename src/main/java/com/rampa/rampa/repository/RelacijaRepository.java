package com.rampa.rampa.repository;

import com.rampa.rampa.model.Relacija;
import com.rampa.rampa.model.Stanica;
import com.rampa.rampa.model.Vozilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelacijaRepository extends JpaRepository<Relacija, Long> {

    @Query("SELECT r FROM Relacija r WHERE r.prvaStanica = :stanica OR r.drugaStanica = :stanica")
    List<Relacija> findByStanica(@Param("stanica") Stanica stanica);

    @Query("SELECT r FROM Relacija r JOIN r.vozila v WHERE v = :vozilo")
    List<Relacija> findByVozilo(@Param("vozilo") Vozilo vozilo);
}
