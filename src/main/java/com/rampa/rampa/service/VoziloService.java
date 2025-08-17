package com.rampa.rampa.service;

import com.rampa.rampa.model.*;
import com.rampa.rampa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VoziloService {

    @Autowired
    private VoziloRepository voziloRepository;

    @Autowired
    private RelacijaRepository relacijaRepository;

    @Autowired
    private StanicaRepository stanicaRepository;

    @Autowired
    private RampaRepository rampaRepository;

    @Autowired
    private CenovnikService cenovnikService;

    @Transactional
    public Vozilo enterVehicle(String registracija, VoziloType tip, Long stanicaId, Long rampaId) {
        Vozilo vozilo = new Vozilo();
        vozilo.setRegistracija(registracija);
        vozilo.setTip(tip);
        vozilo.setVremeUlaska(new Date());
        vozilo.setStatus(VoziloStatus.NA_RELACIJI);

        Vozilo savedVozilo = voziloRepository.saveAndFlush(vozilo);

        Optional<Rampa> rampaOpt = rampaRepository.findById(rampaId);
        if (rampaOpt.isPresent()) {
            Rampa rampa = rampaOpt.get();
            rampa.setPosition(RampaPosition.PODIGNUTA);
            rampaRepository.save(rampa);

            rampa.setPosition(RampaPosition.SPUSTENA);
            rampaRepository.save(rampa);
        }

        Optional<Stanica> stanicaOpt = stanicaRepository.findById(stanicaId);
        if (stanicaOpt.isPresent()) {
            Stanica stanica = stanicaOpt.get();
            List<Relacija> relacije = relacijaRepository.findByStanica(stanica);

            for (Relacija relacija : relacije) {
                addVehicleToRelacija(savedVozilo, relacija);
            }
        }

        return savedVozilo;
    }

    @Transactional
    public void addVehicleToRelacija(Vozilo vozilo, Relacija relacija) {
        if (!relacija.getVozila().contains(vozilo)) {
            relacija.getVozila().add(vozilo);
            relacijaRepository.save(relacija);
        }
    }

    @Transactional
    public void exitVehicle(Long voziloId, Long stanicaId, String currency, Boolean lostReceipt) {
        Optional<Vozilo> voziloOpt = voziloRepository.findById(voziloId);
        Optional<Stanica> stanicaOpt = stanicaRepository.findById(stanicaId);

        if (voziloOpt.isPresent() && stanicaOpt.isPresent()) {
            Vozilo vozilo = voziloOpt.get();
            Stanica stanica = stanicaOpt.get();


            vozilo.setVremeIzlaska(new Date());
            vozilo.setStatus(VoziloStatus.IZASLO);
            voziloRepository.save(vozilo);


            Integer currentCount = stanica.getVehiclesPassed() != null ? stanica.getVehiclesPassed() : 0;
            stanica.setVehiclesPassed(currentCount + 1);

            Optional<Cenovnik> cenovnikOpt = cenovnikService.getCurrentValidCenovnik();
            if (cenovnikOpt.isPresent()) {
                Cenovnik cenovnik = cenovnikOpt.get();
                BigDecimal amount;


                if (lostReceipt != null && lostReceipt) {
                    amount = "EUR".equalsIgnoreCase(currency) ? cenovnik.getMaxCenaEur() : cenovnik.getMaxCenaRsd();
                } else {
                    amount = calculatePrice(vozilo.getTip(), cenovnik, currency);
                }

                if ("EUR".equalsIgnoreCase(currency)) {
                    BigDecimal currentZaradaEur = stanica.getZaradaEur() != null ? stanica.getZaradaEur() : BigDecimal.ZERO;
                    stanica.setZaradaEur(currentZaradaEur.add(amount));
                } else {
                    BigDecimal currentZaradaRsd = stanica.getZaradaRsd() != null ? stanica.getZaradaRsd() : BigDecimal.ZERO;
                    stanica.setZaradaRsd(currentZaradaRsd.add(amount));
                }
            }

            stanicaRepository.save(stanica);

            List<Relacija> relacije = relacijaRepository.findByVozilo(vozilo);
            for (Relacija relacija : relacije) {
                relacija.getVozila().remove(vozilo);
                relacijaRepository.save(relacija);
            }
        }
    }

    private BigDecimal calculatePrice(VoziloType tip, Cenovnik cenovnik, String currency) {
        for (Price price : cenovnik.getPrices()) {
            if (price.getVoziloType() == tip) {
                return "EUR".equalsIgnoreCase(currency) ? price.getPriceEur() : price.getPriceRsd();
            }
        }
        return BigDecimal.ZERO;
    }

    public List<Vozilo> getVehiclesInRelacija(Long relacijaId) {
        Optional<Relacija> relacijaOpt = relacijaRepository.findById(relacijaId);
        return relacijaOpt.map(Relacija::getVozila).orElse(List.of());
    }

    public List<Relacija> getRelacijeForStanica(Long stanicaId) {
        Optional<Stanica> stanicaOpt = stanicaRepository.findById(stanicaId);
        return stanicaOpt.map(relacijaRepository::findByStanica).orElse(List.of());
    }
}
