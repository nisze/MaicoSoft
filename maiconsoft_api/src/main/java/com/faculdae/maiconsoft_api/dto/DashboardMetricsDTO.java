package com.faculdae.maiconsoft_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DashboardMetricsDTO {
    
    private long totalClients;
    private long newClientsThisPeriod;
    private BigDecimal totalRevenue;
    private BigDecimal revenueThisPeriod;
    private long totalSales;
    private long salesThisPeriod;
    private long activeUsers;
    private BigDecimal averageTicket;
    private Double conversionRate;
    private BigDecimal revenueGrowth;
    private Double clientsGrowth;
    private Double salesGrowth;
    private LocalDateTime lastUpdate;

    // Constructors
    public DashboardMetricsDTO() {}

    public DashboardMetricsDTO(long totalClients, long newClientsThisPeriod, 
                              BigDecimal totalRevenue, BigDecimal revenueThisPeriod,
                              long totalSales, long salesThisPeriod, long activeUsers,
                              BigDecimal averageTicket, Double conversionRate,
                              BigDecimal revenueGrowth, Double clientsGrowth, Double salesGrowth) {
        this.totalClients = totalClients;
        this.newClientsThisPeriod = newClientsThisPeriod;
        this.totalRevenue = totalRevenue;
        this.revenueThisPeriod = revenueThisPeriod;
        this.totalSales = totalSales;
        this.salesThisPeriod = salesThisPeriod;
        this.activeUsers = activeUsers;
        this.averageTicket = averageTicket;
        this.conversionRate = conversionRate;
        this.revenueGrowth = revenueGrowth;
        this.clientsGrowth = clientsGrowth;
        this.salesGrowth = salesGrowth;
        this.lastUpdate = LocalDateTime.now();
    }

    // Getters and Setters
    public long getTotalClients() {
        return totalClients;
    }

    public void setTotalClients(long totalClients) {
        this.totalClients = totalClients;
    }

    public long getNewClientsThisPeriod() {
        return newClientsThisPeriod;
    }

    public void setNewClientsThisPeriod(long newClientsThisPeriod) {
        this.newClientsThisPeriod = newClientsThisPeriod;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getRevenueThisPeriod() {
        return revenueThisPeriod;
    }

    public void setRevenueThisPeriod(BigDecimal revenueThisPeriod) {
        this.revenueThisPeriod = revenueThisPeriod;
    }

    public long getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(long totalSales) {
        this.totalSales = totalSales;
    }

    public long getSalesThisPeriod() {
        return salesThisPeriod;
    }

    public void setSalesThisPeriod(long salesThisPeriod) {
        this.salesThisPeriod = salesThisPeriod;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }

    public BigDecimal getAverageTicket() {
        return averageTicket;
    }

    public void setAverageTicket(BigDecimal averageTicket) {
        this.averageTicket = averageTicket;
    }

    public Double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Double conversionRate) {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getRevenueGrowth() {
        return revenueGrowth;
    }

    public void setRevenueGrowth(BigDecimal revenueGrowth) {
        this.revenueGrowth = revenueGrowth;
    }

    public Double getClientsGrowth() {
        return clientsGrowth;
    }

    public void setClientsGrowth(Double clientsGrowth) {
        this.clientsGrowth = clientsGrowth;
    }

    public Double getSalesGrowth() {
        return salesGrowth;
    }

    public void setSalesGrowth(Double salesGrowth) {
        this.salesGrowth = salesGrowth;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}