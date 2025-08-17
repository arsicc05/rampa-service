package com.rampa.rampa.service;

import com.rampa.rampa.model.Relacija;
import com.rampa.rampa.model.Stanica;
import com.rampa.rampa.repository.RelacijaRepository;
import com.rampa.rampa.repository.StanicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RelacijaService {

    @Autowired
    private RelacijaRepository relacijaRepository;

    @Autowired
    private StanicaRepository stanicaRepository;

    public List<Relacija> getAllRelacije() {
        return relacijaRepository.findAll();
    }

    public Optional<Relacija> getRelacijaById(Long id) {
        return relacijaRepository.findById(id);
    }

    public Relacija createRelacija(String naziv, Long prvaStanicaId, Long drugaStanicaId) {
        Optional<Stanica> prvaStanica = stanicaRepository.findById(prvaStanicaId);
        Optional<Stanica> drugaStanica = stanicaRepository.findById(drugaStanicaId);

        if (prvaStanica.isEmpty() || drugaStanica.isEmpty()) {
            throw new RuntimeException("Stanica not found");
        }

        Relacija relacija = new Relacija(naziv, prvaStanica.get(), drugaStanica.get());
        return relacijaRepository.save(relacija);
    }

    public List<Relacija> getRelacijeByStanica(Stanica stanica) {
        return relacijaRepository.findByStanica(stanica);
    }

    public void deleteRelacija(Long id) {
        relacijaRepository.deleteById(id);
    }
}
