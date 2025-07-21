package com.rampa.rampa.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;

@Embeddable
public class Price {
    @Enumerated(EnumType.STRING)
    private VoziloType voziloType;
    private BigDecimal priceEur;
    private BigDecimal priceRsd;

    public Price() {}

    public Price(VoziloType voziloType, BigDecimal priceEur, BigDecimal priceRsd) {
        this.voziloType = voziloType;
        this.priceEur = priceEur;
        this.priceRsd = priceRsd;
    }
}

