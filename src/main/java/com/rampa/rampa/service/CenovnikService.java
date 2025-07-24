package com.rampa.rampa.service;

import com.rampa.rampa.model.Cenovnik;
import com.rampa.rampa.repository.CenovnikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CenovnikService {

    @Autowired
    private CenovnikRepository cenovnikRepository;

    @Transactional
    public Cenovnik createCenovnik(Cenovnik cenovnik) {
        cenovnikRepository.invalidateAllCenovniks();

        cenovnik.setIsValid(true);
        cenovnik.setValidFrom(new Date());

        return cenovnikRepository.save(cenovnik);
    }

    public Optional<Cenovnik> getCurrentValidCenovnik() {
        return cenovnikRepository.findByIsValidTrue();
    }

    public List<Cenovnik> getAllCenovniks() {
        return cenovnikRepository.findAll();
    }

    public Optional<Cenovnik> getCenovnikById(Long id) {
        return cenovnikRepository.findById(id);
    }
}
