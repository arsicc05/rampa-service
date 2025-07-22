package com.rampa.rampa.controller;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.model.Stanica;
import com.rampa.rampa.service.RampaService;
import com.rampa.rampa.service.StanicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/worker")
@PreAuthorize("hasRole('WORKER')")
public class WorkerController {

    @Autowired
    private StanicaService stanicaService;

    @Autowired
    private RampaService rampaService;

    @GetMapping("/stanicas")
    public ResponseEntity<List<Stanica>> getAllStanicas() {
        List<Stanica> stanicas = stanicaService.getAllStanicas();
        return ResponseEntity.ok(stanicas);
    }

    @GetMapping("/stanica/{stanicaId}/rampas")
    public ResponseEntity<?> getRampasByStanica(@PathVariable Long stanicaId) {
        Optional<Stanica> stanicaOpt = stanicaService.getStanicaById(stanicaId);

        if (stanicaOpt.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Stanica not found");
            return ResponseEntity.notFound().build();
        }

        Stanica stanica = stanicaOpt.get();
        List<Rampa> rampas = stanica.getRampe();

        Map<String, Object> response = new HashMap<>();
        response.put("stanica", Map.of(
            "id", stanica.getId(),
            "naziv", stanica.getNaziv(),
            "status", stanica.getStatus()
        ));
        response.put("rampas", rampas);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/select-rampa")
    public ResponseEntity<?> selectRampa(@RequestBody SelectRampaRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String workerUsername = authentication.getName();

        Optional<Rampa> rampaOpt = rampaService.getRampaById(request.getRampaId());

        if (rampaOpt.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Rampa not found");
            return ResponseEntity.notFound().build();
        }

        Rampa rampa = rampaOpt.get();

        //TODO: add logic to assign the worker to the rampa maybe
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Rampa selected successfully");
        response.put("worker", workerUsername);
        response.put("rampa", Map.of(
            "id", rampa.getId(),
            "redniBroj", rampa.getRedniBroj(),
            "tip", rampa.getTip(),
            "status", rampa.getStatus()
        ));
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }


    public static class SelectRampaRequest {
        private Long rampaId;
        private Long stanicaId;

        public Long getRampaId() { return rampaId; }
        public void setRampaId(Long rampaId) { this.rampaId = rampaId; }

        public Long getStanicaId() { return stanicaId; }
        public void setStanicaId(Long stanicaId) { this.stanicaId = stanicaId; }
    }
}
