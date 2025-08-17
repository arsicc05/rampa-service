package com.rampa.rampa.controller;

import com.rampa.rampa.model.Relacija;
import com.rampa.rampa.service.RelacijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/relacije")
public class RelacijeController {

    @Autowired
    private RelacijaService relacijaService;

    @GetMapping
    public ResponseEntity<List<Relacija>> getAllRelacije() {
        List<Relacija> relacije = relacijaService.getAllRelacije();
        return ResponseEntity.ok(relacije);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Relacija> getRelacijaById(@PathVariable Long id) {
        Optional<Relacija> relacija = relacijaService.getRelacijaById(id);
        if (relacija.isPresent()) {
            return ResponseEntity.ok(relacija.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Relacija> createRelacija(@RequestBody CreateRelacijaRequest request) {
        try {
            Relacija relacija = relacijaService.createRelacija(
                request.getNaziv(),
                request.getPrvaStanicaId(),
                request.getDrugaStanicaId()
            );
            return ResponseEntity.ok(relacija);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Void> deleteRelacija(@PathVariable Long id) {
        try {
            relacijaService.deleteRelacija(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class CreateRelacijaRequest {
        private String naziv;
        private Long prvaStanicaId;
        private Long drugaStanicaId;

        public CreateRelacijaRequest() {}

        public String getNaziv() {
            return naziv;
        }

        public void setNaziv(String naziv) {
            this.naziv = naziv;
        }

        public Long getPrvaStanicaId() {
            return prvaStanicaId;
        }

        public void setPrvaStanicaId(Long prvaStanicaId) {
            this.prvaStanicaId = prvaStanicaId;
        }

        public Long getDrugaStanicaId() {
            return drugaStanicaId;
        }

        public void setDrugaStanicaId(Long drugaStanicaId) {
            this.drugaStanicaId = drugaStanicaId;
        }
    }
}
