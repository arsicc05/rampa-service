package com.rampa.rampa.controller;

import com.rampa.rampa.model.*;
import com.rampa.rampa.service.RampaService;
import com.rampa.rampa.service.StanicaService;
import com.rampa.rampa.service.VoziloService;
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

    @Autowired
    private VoziloService voziloService;

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

    @PostMapping("/vehicle-entry")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> vehicleEntry(@RequestBody VehicleEntryRequest request) {
        try {
            Vozilo vozilo = voziloService.enterVehicle(
                request.getRegistracija(),
                request.getTip(),
                request.getStanicaId(),
                request.getRampaId()
            );
            return ResponseEntity.ok(new VehicleEntryResponse(
                "Vehicle entered successfully. Rampa raised and lowered.",
                vozilo
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error entering vehicle: " + e.getMessage());
        }
    }

    @PostMapping("/vehicle-exit")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<?> vehicleExit(@RequestBody VehicleExitRequest request) {
        try {
            voziloService.exitVehicle(
                request.getVoziloId(),
                request.getStanicaId(),
                request.getCurrency(),
                request.getLostReceipt()
            );

            String message = request.getLostReceipt() != null && request.getLostReceipt()
                ? "Vehicle exited successfully. Maximum price charged for lost receipt."
                : "Vehicle exited successfully. Payment processed.";

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error exiting vehicle: " + e.getMessage());
        }
    }

    @GetMapping("/relacije/{stanicaId}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<Relacija>> getRelacijeForStanica(@PathVariable Long stanicaId) {
        List<Relacija> relacije = voziloService.getRelacijeForStanica(stanicaId);
        return ResponseEntity.ok(relacije);
    }

    @GetMapping("/vehicles-in-relacija/{relacijaId}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<Vozilo>> getVehiclesInRelacija(@PathVariable Long relacijaId) {
        List<Vozilo> vozila = voziloService.getVehiclesInRelacija(relacijaId);
        return ResponseEntity.ok(vozila);
    }

    public static class SelectRampaRequest {
        private Long rampaId;
        private Long workerId;

        public SelectRampaRequest() {}

        public Long getRampaId() {
            return rampaId;
        }

        public void setRampaId(Long rampaId) {
            this.rampaId = rampaId;
        }

        public Long getWorkerId() {
            return workerId;
        }

        public void setWorkerId(Long workerId) {
            this.workerId = workerId;
        }
    }

    public static class VehicleEntryRequest {
        private String registracija;
        private VoziloType tip;
        private Long stanicaId;
        private Long rampaId;

        public VehicleEntryRequest() {}

        public String getRegistracija() {
            return registracija;
        }

        public void setRegistracija(String registracija) {
            this.registracija = registracija;
        }

        public VoziloType getTip() {
            return tip;
        }

        public void setTip(VoziloType tip) {
            this.tip = tip;
        }

        public Long getStanicaId() {
            return stanicaId;
        }

        public void setStanicaId(Long stanicaId) {
            this.stanicaId = stanicaId;
        }

        public Long getRampaId() {
            return rampaId;
        }

        public void setRampaId(Long rampaId) {
            this.rampaId = rampaId;
        }
    }

    public static class VehicleExitRequest {
        private Long voziloId;
        private Long stanicaId;
        private String currency;
        private Boolean lostReceipt;

        public VehicleExitRequest() {}

        public Long getVoziloId() {
            return voziloId;
        }

        public void setVoziloId(Long voziloId) {
            this.voziloId = voziloId;
        }

        public Long getStanicaId() {
            return stanicaId;
        }

        public void setStanicaId(Long stanicaId) {
            this.stanicaId = stanicaId;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Boolean getLostReceipt() {
            return lostReceipt;
        }

        public void setLostReceipt(Boolean lostReceipt) {
            this.lostReceipt = lostReceipt;
        }
    }

    public static class VehicleEntryResponse {
        private String message;
        private Vozilo vozilo;

        public VehicleEntryResponse(String message, Vozilo vozilo) {
            this.message = message;
            this.vozilo = vozilo;
        }

        public String getMessage() {
            return message;
        }

        public Vozilo getVozilo() {
            return vozilo;
        }
    }
}
