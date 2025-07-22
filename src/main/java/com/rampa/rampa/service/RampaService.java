package com.rampa.rampa.service;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.repository.RampaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RampaService {
    @Autowired
    private RampaRepository rampaRepository;

    public List<Rampa> getAllRampas() {
        return rampaRepository.findAll();
    }

    public Optional<Rampa> getRampaById(Long id) {
        return rampaRepository.findById(id);
    }

    public Rampa saveRampa(Rampa rampa) {
        return rampaRepository.save(rampa);
    }

    public void deleteRampa(Long id) {
        rampaRepository.deleteById(id);
    }
}
