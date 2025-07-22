package com.rampa.rampa.controller;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.model.RampaStatus;
import com.rampa.rampa.model.RampaType;
import com.rampa.rampa.model.Stanica;
import com.rampa.rampa.model.StatusEnum;
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
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
public class AdminController {

    @Autowired
    private StanicaService stanicaService;

    @Autowired
    private RampaService rampaService;

    @PostMapping("/create-stanica")
    public ResponseEntity<?> createStanica(@RequestBody CreateStanicaRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Stanica stanica = new Stanica();
            stanica.setNaziv(request.getNaziv());

            StatusEnum status = StatusEnum.ACTIVE;
            if (request.getStatus() != null) {
                try {
                    status = StatusEnum.valueOf(request.getStatus().toUpperCase());
                } catch (IllegalArgumentException e) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Invalid status. Valid values are: AKTIVNA, NEAKTIVNA");
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            }
            stanica.setStatus(status);

            Stanica savedStanica = stanicaService.saveStanica(stanica);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Stanica created successfully");
            response.put("stanica", Map.of(
                "id", savedStanica.getId(),
                "naziv", savedStanica.getNaziv(),
                "status", savedStanica.getStatus()
            ));
            response.put("createdBy", username);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create stanica: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/stanica/{stanicaId}/add-rampa")
    public ResponseEntity<?> addRampaToStanica(@PathVariable Long stanicaId, @RequestBody CreateRampaRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<Stanica> stanicaOpt = stanicaService.getStanicaById(stanicaId);
            if (stanicaOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Stanica not found");
                return ResponseEntity.notFound().build();
            }

            Stanica stanica = stanicaOpt.get();

            RampaType rampaType;
            try {
                rampaType = RampaType.valueOf(request.getTip().toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid rampa type. Valid values are: " + java.util.Arrays.toString(RampaType.values()));
                return ResponseEntity.badRequest().body(errorResponse);
            }

            RampaStatus rampaStatus = RampaStatus.AKTIVNA;
            if (request.getStatus() != null) {
                try {
                    rampaStatus = RampaStatus.valueOf(request.getStatus().toUpperCase());
                } catch (IllegalArgumentException e) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Invalid rampa status. Valid values are: " + java.util.Arrays.toString(RampaStatus.values()));
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            }

            Rampa rampa = new Rampa();
            rampa.setRedniBroj(request.getRedniBroj());
            rampa.setTip(rampaType);
            rampa.setStatus(rampaStatus);

            Rampa savedRampa = rampaService.saveRampa(rampa);

            stanica.getRampe().add(savedRampa);
            stanicaService.saveStanica(stanica);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rampa added to stanica successfully");
            response.put("stanica", Map.of(
                "id", stanica.getId(),
                "naziv", stanica.getNaziv()
            ));
            response.put("rampa", Map.of(
                "id", savedRampa.getId(),
                "redniBroj", savedRampa.getRedniBroj(),
                "tip", savedRampa.getTip(),
                "status", savedRampa.getStatus()
            ));
            response.put("createdBy", username);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to add rampa: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/stanicas")
    public ResponseEntity<List<Stanica>> getAllStanicasAdmin() {
        List<Stanica> stanicas = stanicaService.getAllStanicas();
        return ResponseEntity.ok(stanicas);
    }

    @GetMapping("/stanica/{stanicaId}")
    public ResponseEntity<?> getStanicaDetails(@PathVariable Long stanicaId) {
        Optional<Stanica> stanicaOpt = stanicaService.getStanicaById(stanicaId);

        if (stanicaOpt.isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Stanica not found");
            return ResponseEntity.notFound().build();
        }

        Stanica stanica = stanicaOpt.get();
        Map<String, Object> response = new HashMap<>();
        response.put("stanica", Map.of(
            "id", stanica.getId(),
            "naziv", stanica.getNaziv(),
            "status", stanica.getStatus(),
            "rampeCount", stanica.getRampe().size()
        ));
        response.put("rampe", stanica.getRampe());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/rampa/{rampaId}")
    public ResponseEntity<?> deleteRampa(@PathVariable Long rampaId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<Rampa> rampaOpt = rampaService.getRampaById(rampaId);
            if (rampaOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Rampa not found");
                return ResponseEntity.notFound().build();
            }

            rampaService.deleteRampa(rampaId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rampa deleted successfully");
            response.put("rampaId", rampaId);
            response.put("deletedBy", username);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to delete rampa: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    public static class CreateStanicaRequest {
        private String naziv;
        private String status;

        public String getNaziv() { return naziv; }
        public void setNaziv(String naziv) { this.naziv = naziv; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class CreateRampaRequest {
        private int redniBroj;
        private String tip;
        private String status;

        public int getRedniBroj() { return redniBroj; }
        public void setRedniBroj(int redniBroj) { this.redniBroj = redniBroj; }

        public String getTip() { return tip; }
        public void setTip(String tip) { this.tip = tip; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
