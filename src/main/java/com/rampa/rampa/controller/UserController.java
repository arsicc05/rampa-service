package com.rampa.rampa.controller;

import com.rampa.rampa.model.Admin;
import com.rampa.rampa.model.Korisnik;
import com.rampa.rampa.model.Manager;
import com.rampa.rampa.model.Worker;
import com.rampa.rampa.service.KorisnikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    KorisnikService korisnikService;

    @GetMapping("/users")
    public List<Korisnik> getAllUsers() {
        return korisnikService.getAllUsers();
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("message", "This is a protected route!");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            if (korisnikService.findByUsername(request.getUsername()) != null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Username already exists");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Korisnik newUser;
            switch (request.getRole().toUpperCase()) {
                case "ADMIN":
                    newUser = new Admin();
                    break;
                case "MANAGER":
                    newUser = new Manager();
                    break;
                case "WORKER":
                    newUser = new Worker();
                    break;
                default:
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Invalid role. Must be ADMIN, MANAGER, or WORKER");
                    return ResponseEntity.badRequest().body(errorResponse);
            }

            newUser.setUsername(request.getUsername());
            newUser.setLozinka(request.getPassword());
            newUser.setIme(request.getIme());
            newUser.setPrezime(request.getPrezime());
            newUser.setEmail(request.getEmail());

            Korisnik savedUser = korisnikService.saveUser(newUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("userId", savedUser.getId());
            response.put("username", savedUser.getUsername());
            response.put("role", request.getRole());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    public static class CreateUserRequest {
        private String username;
        private String password;
        private String ime;
        private String prezime;
        private String email;
        private String role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getIme() { return ime; }
        public void setIme(String ime) { this.ime = ime; }

        public String getPrezime() { return prezime; }
        public void setPrezime(String prezime) { this.prezime = prezime; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
