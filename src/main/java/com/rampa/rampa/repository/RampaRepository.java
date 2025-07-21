package com.rampa.rampa.repository;

import com.rampa.rampa.model.Rampa;
import com.rampa.rampa.model.RampaStatus;
import com.rampa.rampa.model.RampaType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RampaRepository  extends JpaRepository<Rampa, Long> {
    Rampa findByTipAndStatus(RampaType tip, RampaStatus status);
}
