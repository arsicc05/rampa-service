package com.rampa.rampa.controller;

import com.rampa.rampa.model.Korisnik;
import com.rampa.rampa.service.KorisnikService;
import com.rampa.rampa.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    @Autowired
    private KorisnikService korisnikService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        Korisnik korisnik = korisnikService.findByUsername(username);
        if (korisnik != null && korisnik.getLozinka().equals(password)) {
            String role = korisnikService.getUserRole(korisnik);
            String token = jwtUtil.generateToken(username, role);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("role", role);
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(response);
        }
    }
}
