package com.rampa.rampa.service;

import com.rampa.rampa.model.Admin;
import com.rampa.rampa.model.Korisnik;
import com.rampa.rampa.model.Manager;
import com.rampa.rampa.repository.KorisnikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KorisnikService {
    @Autowired
    KorisnikRepository korisnikRepository;

    public List<Korisnik> getAllUsers() {
        List<Korisnik> lista = korisnikRepository.findAll();
        return lista;
    }

    public Korisnik findByUsername(String username) {
       Korisnik korisnik = korisnikRepository.findByUsername(username);
        return korisnik;
    }

    public String getUserRole(Korisnik korisnik) {
        if (korisnik instanceof Admin) {
            return "ADMIN";
        } else if (korisnik instanceof Manager) {
            return "MANAGER";
        } else {
            return "WORKER";
        }
    }

    public Korisnik saveUser(Korisnik korisnik) {
        return korisnikRepository.save(korisnik);
    }
}
