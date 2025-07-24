package com.rampa.rampa.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
public class Cenovnik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isValid;
    private Date validFrom;
    @ElementCollection
    private List<Price> prices;
    private BigDecimal maxCenaEur;
    private BigDecimal maxCenaRsd;

    public Cenovnik() {}

    public Cenovnik(Boolean isValid, Date validFrom, List<Price> prices, BigDecimal maxCenaEur, BigDecimal maxCenaRsd) {
        this.isValid = isValid;
        this.validFrom = validFrom;
        this.prices = prices;
        this.maxCenaEur = maxCenaEur;
        this.maxCenaRsd = maxCenaRsd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

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
