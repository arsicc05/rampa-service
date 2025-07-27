package com.rampa.rampa.service;

import com.rampa.rampa.model.*;
import com.rampa.rampa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private VoziloRepository voziloRepository;

    @Autowired
    private StanicaRepository stanicaRepository;

    @Autowired
    private RelacijaRepository relacijaRepository;

    public DashboardStats getOverallStats() {
        List<Vozilo> allVehicles = voziloRepository.findAll();
        List<Stanica> allStanicas = stanicaRepository.findAll();

        DashboardStats stats = new DashboardStats();

        stats.setTotalVehiclesProcessed(allVehicles.size());

        long vehiclesInSystem = allVehicles.stream()
            .filter(v -> v.getStatus() == VoziloStatus.NA_RELACIJI)
            .count();
        stats.setVehiclesCurrentlyInSystem((int) vehiclesInSystem);

        long vehiclesExited = allVehicles.stream()
            .filter(v -> v.getStatus() == VoziloStatus.IZASLO)
            .count();
        stats.setVehiclesExited((int) vehiclesExited);

        BigDecimal totalEarningsEur = allStanicas.stream()
            .map(s -> s.getZaradaEur() != null ? s.getZaradaEur() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEarningsRsd = allStanicas.stream()
            .map(s -> s.getZaradaRsd() != null ? s.getZaradaRsd() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setTotalEarningsEur(totalEarningsEur);
        stats.setTotalEarningsRsd(totalEarningsRsd);

        Map<VoziloType, Long> vehicleTypeCount = allVehicles.stream()
            .filter(v -> v.getTip() != null)
            .collect(Collectors.groupingBy(Vozilo::getTip, Collectors.counting()));

        stats.setVehicleTypeDistribution(vehicleTypeCount);

        String mostCommonType = vehicleTypeCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey().toString())
            .orElse("N/A");
        stats.setMostCommonVehicleType(mostCommonType);

        return stats;
    }

    public List<StanicaStats> getStanicaStats() {
        List<Stanica> stanicas = stanicaRepository.findAll();
        List<StanicaStats> stanicaStatsList = new ArrayList<>();

        for (Stanica stanica : stanicas) {
            StanicaStats stats = new StanicaStats();
            stats.setStanicaId(stanica.getId());
            stats.setStanicaNaziv(stanica.getNaziv());
            stats.setZaradaEur(stanica.getZaradaEur() != null ? stanica.getZaradaEur() : BigDecimal.ZERO);
            stats.setZaradaRsd(stanica.getZaradaRsd() != null ? stanica.getZaradaRsd() : BigDecimal.ZERO);

            stats.setVehiclesPassed(stanica.getVehiclesPassed() != null ? stanica.getVehiclesPassed() : 0);

            stanicaStatsList.add(stats);
        }

        return stanicaStatsList;
    }

    public List<DailyStats> getDailyStats(int days) {
        List<DailyStats> dailyStatsList = new ArrayList<>();
        List<Vozilo> allVehicles = voziloRepository.findAll();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Date startOfDay = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endOfDay = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

            DailyStats dayStats = new DailyStats();
            dayStats.setDate(date);

            long vehiclesEntered = allVehicles.stream()
                .filter(v -> v.getVremeUlaska() != null)
                .filter(v -> v.getVremeUlaska().after(startOfDay) && v.getVremeUlaska().before(endOfDay))
                .count();
            dayStats.setVehiclesEntered((int) vehiclesEntered);

            long vehiclesExited = allVehicles.stream()
                .filter(v -> v.getVremeIzlaska() != null)
                .filter(v -> v.getVremeIzlaska().after(startOfDay) && v.getVremeIzlaska().before(endOfDay))
                .count();
            dayStats.setVehiclesExited((int) vehiclesExited);

            dailyStatsList.add(dayStats);
        }

        return dailyStatsList;
    }

    public RevenueStats getRevenueStats() {
        List<Stanica> stanicas = stanicaRepository.findAll();

        RevenueStats stats = new RevenueStats();

        BigDecimal totalEur = stanicas.stream()
            .map(s -> s.getZaradaEur() != null ? s.getZaradaEur() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRsd = stanicas.stream()
            .map(s -> s.getZaradaRsd() != null ? s.getZaradaRsd() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setTotalRevenueEur(totalEur);
        stats.setTotalRevenueRsd(totalRsd);

        Optional<Stanica> topEarningStanica = stanicas.stream()
            .max(Comparator.comparing(s -> {
                BigDecimal eurAmount = s.getZaradaEur() != null ? s.getZaradaEur() : BigDecimal.ZERO;
                BigDecimal rsdAmount = s.getZaradaRsd() != null ? s.getZaradaRsd() : BigDecimal.ZERO;
                return eurAmount.add(rsdAmount.divide(BigDecimal.valueOf(110), 2, BigDecimal.ROUND_HALF_UP));
            }));

        if (topEarningStanica.isPresent()) {
            Stanica topStanica = topEarningStanica.get();
            stats.setTopEarningStanicaId(topStanica.getId());
            stats.setTopEarningStanicaNaziv(topStanica.getNaziv());
            stats.setTopEarningAmount(
                (topStanica.getZaradaEur() != null ? topStanica.getZaradaEur() : BigDecimal.ZERO)
                .add((topStanica.getZaradaRsd() != null ? topStanica.getZaradaRsd() : BigDecimal.ZERO)
                    .divide(BigDecimal.valueOf(110), 2, BigDecimal.ROUND_HALF_UP))
            );
        }

        return stats;
    }

    public static class DashboardStats {
        private int totalVehiclesProcessed;
        private int vehiclesCurrentlyInSystem;
        private int vehiclesExited;
        private BigDecimal totalEarningsEur;
        private BigDecimal totalEarningsRsd;
        private Map<VoziloType, Long> vehicleTypeDistribution;
        private String mostCommonVehicleType;

        public int getTotalVehiclesProcessed() { return totalVehiclesProcessed; }
        public void setTotalVehiclesProcessed(int totalVehiclesProcessed) { this.totalVehiclesProcessed = totalVehiclesProcessed; }

        public int getVehiclesCurrentlyInSystem() { return vehiclesCurrentlyInSystem; }
        public void setVehiclesCurrentlyInSystem(int vehiclesCurrentlyInSystem) { this.vehiclesCurrentlyInSystem = vehiclesCurrentlyInSystem; }

        public int getVehiclesExited() { return vehiclesExited; }
        public void setVehiclesExited(int vehiclesExited) { this.vehiclesExited = vehiclesExited; }

        public BigDecimal getTotalEarningsEur() { return totalEarningsEur; }
        public void setTotalEarningsEur(BigDecimal totalEarningsEur) { this.totalEarningsEur = totalEarningsEur; }

        public BigDecimal getTotalEarningsRsd() { return totalEarningsRsd; }
        public void setTotalEarningsRsd(BigDecimal totalEarningsRsd) { this.totalEarningsRsd = totalEarningsRsd; }

        public Map<VoziloType, Long> getVehicleTypeDistribution() { return vehicleTypeDistribution; }
        public void setVehicleTypeDistribution(Map<VoziloType, Long> vehicleTypeDistribution) { this.vehicleTypeDistribution = vehicleTypeDistribution; }

        public String getMostCommonVehicleType() { return mostCommonVehicleType; }
        public void setMostCommonVehicleType(String mostCommonVehicleType) { this.mostCommonVehicleType = mostCommonVehicleType; }
    }

    public static class StanicaStats {
        private Long stanicaId;
        private String stanicaNaziv;
        private BigDecimal zaradaEur;
        private BigDecimal zaradaRsd;
        private int vehiclesPassed;

        public Long getStanicaId() { return stanicaId; }
        public void setStanicaId(Long stanicaId) { this.stanicaId = stanicaId; }

        public String getStanicaNaziv() { return stanicaNaziv; }
        public void setStanicaNaziv(String stanicaNaziv) { this.stanicaNaziv = stanicaNaziv; }

        public BigDecimal getZaradaEur() { return zaradaEur; }
        public void setZaradaEur(BigDecimal zaradaEur) { this.zaradaEur = zaradaEur; }

        public BigDecimal getZaradaRsd() { return zaradaRsd; }
        public void setZaradaRsd(BigDecimal zaradaRsd) { this.zaradaRsd = zaradaRsd; }

        public int getVehiclesPassed() { return vehiclesPassed; }
        public void setVehiclesPassed(int vehiclesPassed) { this.vehiclesPassed = vehiclesPassed; }
    }

    public static class DailyStats {
        private LocalDate date;
        private int vehiclesEntered;
        private int vehiclesExited;

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public int getVehiclesEntered() { return vehiclesEntered; }
        public void setVehiclesEntered(int vehiclesEntered) { this.vehiclesEntered = vehiclesEntered; }

        public int getVehiclesExited() { return vehiclesExited; }
        public void setVehiclesExited(int vehiclesExited) { this.vehiclesExited = vehiclesExited; }
    }

    public static class RevenueStats {
        private BigDecimal totalRevenueEur;
        private BigDecimal totalRevenueRsd;
        private Long topEarningStanicaId;
        private String topEarningStanicaNaziv;
        private BigDecimal topEarningAmount;

        public BigDecimal getTotalRevenueEur() { return totalRevenueEur; }
        public void setTotalRevenueEur(BigDecimal totalRevenueEur) { this.totalRevenueEur = totalRevenueEur; }

        public BigDecimal getTotalRevenueRsd() { return totalRevenueRsd; }
        public void setTotalRevenueRsd(BigDecimal totalRevenueRsd) { this.totalRevenueRsd = totalRevenueRsd; }

        public Long getTopEarningStanicaId() { return topEarningStanicaId; }
        public void setTopEarningStanicaId(Long topEarningStanicaId) { this.topEarningStanicaId = topEarningStanicaId; }

        public String getTopEarningStanicaNaziv() { return topEarningStanicaNaziv; }
        public void setTopEarningStanicaNaziv(String topEarningStanicaNaziv) { this.topEarningStanicaNaziv = topEarningStanicaNaziv; }

        public BigDecimal getTopEarningAmount() { return topEarningAmount; }
        public void setTopEarningAmount(BigDecimal topEarningAmount) { this.topEarningAmount = topEarningAmount; }
    }
}
