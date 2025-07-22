package com.rampa.rampa.service;

import com.rampa.rampa.model.Stanica;
import com.rampa.rampa.repository.StanicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StanicaService {
    @Autowired
    private StanicaRepository stanicaRepository;

    public List<Stanica> getAllStanicas() {
        return stanicaRepository.findAll();
    }

    public Optional<Stanica> getStanicaById(Long id) {
        return stanicaRepository.findById(id);
    }

    public Stanica saveStanica(Stanica stanica) {
        return stanicaRepository.save(stanica);
    }

    public void deleteStanica(Long id) {
        stanicaRepository.deleteById(id);
    }
}
