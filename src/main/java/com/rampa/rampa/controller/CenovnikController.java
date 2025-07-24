package com.rampa.rampa.controller;

import com.rampa.rampa.model.Cenovnik;
import com.rampa.rampa.model.Price;
import com.rampa.rampa.model.VoziloType;
import com.rampa.rampa.service.CenovnikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cenovnik")
public class CenovnikController {

    @Autowired
    private CenovnikService cenovnikService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createCenovnik(@RequestBody CreateCenovnikRequest request) {
        try {
            Cenovnik cenovnik = new Cenovnik();
            cenovnik.setPrices(request.getPrices());
            cenovnik.setMaxCenaEur(request.getMaxCenaEur());
            cenovnik.setMaxCenaRsd(request.getMaxCenaRsd());

            Cenovnik savedCenovnik = cenovnikService.createCenovnik(cenovnik);
            return ResponseEntity.ok(savedCenovnik);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating cenovnik: " + e.getMessage());
        }
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentCenovnik() {
        Optional<Cenovnik> cenovnik = cenovnikService.getCurrentValidCenovnik();
        if (cenovnik.isPresent()) {
            return ResponseEntity.ok(cenovnik.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<Cenovnik>> getAllCenovniks() {
        List<Cenovnik> cenovniks = cenovnikService.getAllCenovniks();
        return ResponseEntity.ok(cenovniks);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCenovnikById(@PathVariable Long id) {
        Optional<Cenovnik> cenovnik = cenovnikService.getCenovnikById(id);
        if (cenovnik.isPresent()) {
            return ResponseEntity.ok(cenovnik.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static class CreateCenovnikRequest {
        private List<Price> prices;
        private BigDecimal maxCenaEur;
        private BigDecimal maxCenaRsd;

        public CreateCenovnikRequest() {}

        public List<Price> getPrices() {
            return prices;
        }

        public void setPrices(List<Price> prices) {
            this.prices = prices;
        }

        public BigDecimal getMaxCenaEur() {
            return maxCenaEur;
        }

        public void setMaxCenaEur(BigDecimal maxCenaEur) {
            this.maxCenaEur = maxCenaEur;
        }

        public BigDecimal getMaxCenaRsd() {
            return maxCenaRsd;
        }

        public void setMaxCenaRsd(BigDecimal maxCenaRsd) {
            this.maxCenaRsd = maxCenaRsd;
        }
    }
}
