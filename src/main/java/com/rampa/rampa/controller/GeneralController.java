package com.rampa.rampa.controller;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.model.RampaStatus;
import com.rampa.rampa.model.RampaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GeneralController {
    @GetMapping("/test")
    public int testEndpoint() {
        return 2;
    }
}
