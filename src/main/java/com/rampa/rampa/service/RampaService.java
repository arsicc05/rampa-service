package com.rampa.rampa.service;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.model.Stanica;
import com.rampa.rampa.repository.RampaRepository;
import com.rampa.rampa.repository.StanicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RampaService {
    @Autowired
    private RampaRepository rampaRepository;

    @Autowired
    private StanicaRepository stanicaRepository;

    public List<Rampa> getAllRampas() {
        return rampaRepository.findAll();
    }

    public Optional<Rampa> getRampaById(Long id) {
        return rampaRepository.findById(id);
    }

    public Rampa saveRampa(Rampa rampa) {
        return rampaRepository.save(rampa);
    }

    @Transactional
    public void deleteRampa(Long id) {
        Optional<Rampa> rampaOpt = rampaRepository.findById(id);
        if (rampaOpt.isPresent()) {
            Rampa rampa = rampaOpt.get();

            List<Stanica> allStanicas = stanicaRepository.findAll();
            for (Stanica stanica : allStanicas) {
                if (stanica.getRampe().contains(rampa)) {
                    stanica.getRampe().remove(rampa);
                    stanicaRepository.save(stanica);
                }
            }

            rampaRepository.deleteById(id);
        }
    }
}
