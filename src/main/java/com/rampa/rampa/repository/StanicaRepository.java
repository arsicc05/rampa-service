package com.rampa.rampa.repository;

import com.rampa.rampa.model.Stanica;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StanicaRepository extends JpaRepository<Stanica, Long> {
}
