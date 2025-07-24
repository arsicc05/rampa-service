package com.rampa.rampa.controller;

import com.rampa.rampa.service.DashboardService;
import com.rampa.rampa.service.DashboardService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<DashboardStats> getOverviewStats() {
        DashboardStats stats = dashboardService.getOverallStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stanicas")
    public ResponseEntity<List<StanicaStats>> getStanicaStats() {
        List<StanicaStats> stats = dashboardService.getStanicaStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<DailyStats>> getDailyStats(@RequestParam(defaultValue = "7") int days) {
        if (days < 1 || days > 365) {
            return ResponseEntity.badRequest().build();
        }
        List<DailyStats> stats = dashboardService.getDailyStats(days);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueStats> getRevenueStats() {
        RevenueStats stats = dashboardService.getRevenueStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        DashboardSummary summary = new DashboardSummary();
        summary.setOverview(dashboardService.getOverallStats());
        summary.setRevenueStats(dashboardService.getRevenueStats());
        summary.setStanicaStats(dashboardService.getStanicaStats());
        summary.setDailyStats(dashboardService.getDailyStats(7));

        return ResponseEntity.ok(summary);
    }

    public static class DashboardSummary {
        private DashboardStats overview;
        private RevenueStats revenueStats;
        private List<StanicaStats> stanicaStats;
        private List<DailyStats> dailyStats;

        public DashboardStats getOverview() {
            return overview;
        }

        public void setOverview(DashboardStats overview) {
            this.overview = overview;
        }

        public RevenueStats getRevenueStats() {
            return revenueStats;
        }

        public void setRevenueStats(RevenueStats revenueStats) {
            this.revenueStats = revenueStats;
        }

        public List<StanicaStats> getStanicaStats() {
            return stanicaStats;
        }

        public void setStanicaStats(List<StanicaStats> stanicaStats) {
            this.stanicaStats = stanicaStats;
        }

        public List<DailyStats> getDailyStats() {
            return dailyStats;
        }

        public void setDailyStats(List<DailyStats> dailyStats) {
            this.dailyStats = dailyStats;
        }
    }
}
