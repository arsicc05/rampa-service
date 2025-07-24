package com.rampa.rampa.controller;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.model.RampaStatus;
import com.rampa.rampa.service.RampaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/rampa")
public class RampaController {

    @Autowired
    private RampaService rampaService;

    @PutMapping("/{rampaId}/status")
    @PreAuthorize("hasRole('WORKER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateRampaStatus(@PathVariable Long rampaId, @RequestBody UpdateRampaStatusRequest request) {
        try {
            Optional<Rampa> rampaOpt = rampaService.getRampaById(rampaId);

            if (rampaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Rampa rampa = rampaOpt.get();
            rampa.setStatus(request.getStatus());

            Rampa updatedRampa = rampaService.saveRampa(rampa);

            return ResponseEntity.ok(new UpdateRampaStatusResponse(
                "Rampa status updated successfully",
                updatedRampa
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating rampa status: " + e.getMessage());
        }
    }

    @PutMapping("/{rampaId}/set-neispravna")
    @PreAuthorize("hasRole('WORKER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> setRampaNeispravna(@PathVariable Long rampaId) {
        try {
            Optional<Rampa> rampaOpt = rampaService.getRampaById(rampaId);

            if (rampaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Rampa rampa = rampaOpt.get();
            rampa.setStatus(RampaStatus.NEISPRAVNA);

            Rampa updatedRampa = rampaService.saveRampa(rampa);

            return ResponseEntity.ok("Rampa successfully set to NEISPRAVNA status");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error setting rampa to neispravna: " + e.getMessage());
        }
    }

    @PutMapping("/{rampaId}/set-aktivna")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> setRampaAktivna(@PathVariable Long rampaId) {
        try {
            Optional<Rampa> rampaOpt = rampaService.getRampaById(rampaId);

            if (rampaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Rampa rampa = rampaOpt.get();
            rampa.setStatus(RampaStatus.AKTIVNA);

            Rampa updatedRampa = rampaService.saveRampa(rampa);

            return ResponseEntity.ok("Rampa successfully set to AKTIVNA status");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error setting rampa to aktivna: " + e.getMessage());
        }
    }

    public static class UpdateRampaStatusRequest {
        private RampaStatus status;

        public UpdateRampaStatusRequest() {}

        public RampaStatus getStatus() {
            return status;
        }

        public void setStatus(RampaStatus status) {
            this.status = status;
        }
    }

    public static class UpdateRampaStatusResponse {
        private String message;
        private Rampa rampa;

        public UpdateRampaStatusResponse(String message, Rampa rampa) {
            this.message = message;
            this.rampa = rampa;
        }

        public String getMessage() {
            return message;
        }

        public Rampa getRampa() {
            return rampa;
        }
    }
}
