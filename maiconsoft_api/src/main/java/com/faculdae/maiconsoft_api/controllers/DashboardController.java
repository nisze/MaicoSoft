package com.faculdae.maiconsoft_api.controllers;

import com.faculdae.maiconsoft_api.dto.DashboardMetricsDTO;
import com.faculdae.maiconsoft_api.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getMetrics(
            @RequestParam(defaultValue = "30") int days) {
        try {
            DashboardMetricsDTO metrics = dashboardService.getMetrics(days);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/revenue-chart")
    public ResponseEntity<Map<String, Object>> getRevenueChart(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> chartData = dashboardService.getRevenueChart(days);
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/sales-chart")
    public ResponseEntity<Map<String, Object>> getSalesChart(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> chartData = dashboardService.getSalesChart(days);
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/clients-growth")
    public ResponseEntity<Map<String, Object>> getClientsGrowth(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> chartData = dashboardService.getClientsGrowth(days);
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recent-clients")
    public ResponseEntity<Map<String, Object>> getRecentClients(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> data = dashboardService.getRecentClients(limit);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/recent-sales")
    public ResponseEntity<Map<String, Object>> getRecentSales(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Map<String, Object> data = dashboardService.getRecentSales(limit);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/top-clients")
    public ResponseEntity<Map<String, Object>> getTopClients(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> data = dashboardService.getTopClients(days);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformance(
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> data = dashboardService.getPerformance(days);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}