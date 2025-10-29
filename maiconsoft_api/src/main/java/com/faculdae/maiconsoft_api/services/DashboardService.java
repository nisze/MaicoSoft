package com.faculdae.maiconsoft_api.services;

import com.faculdae.maiconsoft_api.dto.DashboardMetricsDTO;
import com.faculdae.maiconsoft_api.entities.Cliente;
import com.faculdae.maiconsoft_api.entities.Venda;
import com.faculdae.maiconsoft_api.repositories.ClienteRepository;
import com.faculdae.maiconsoft_api.repositories.UserRepository;
import com.faculdae.maiconsoft_api.repositories.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UserRepository userRepository;

    public DashboardMetricsDTO getMetrics(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        // Período anterior para comparação
        LocalDateTime previousStartDate = startDate.minusDays(days);
        
        // Total de clientes
        long totalClients = clienteRepository.count();
        
        // Novos clientes no período
        long newClientsThisPeriod = clienteRepository.countByDatahoraCadastroBetween(startDate, endDate);
        long newClientsPreviousPeriod = clienteRepository.countByDatahoraCadastroBetween(previousStartDate, startDate);
        
        // Receita total e do período
        BigDecimal totalRevenue = vendaRepository.sumTotalValue();
        BigDecimal revenueThisPeriod = vendaRepository.sumValueByPeriod(startDate, endDate);
        BigDecimal revenuePreviousPeriod = vendaRepository.sumValueByPeriod(previousStartDate, startDate);
        
        // Vendas total e do período
        long totalSales = vendaRepository.count();
        long salesThisPeriod = vendaRepository.countByDataVendaBetween(startDate.toLocalDate(), endDate.toLocalDate());
        long salesPreviousPeriod = vendaRepository.countByDataVendaBetween(previousStartDate.toLocalDate(), startDate.toLocalDate());
        
        // Usuários ativos
        long activeUsers = userRepository.countByAtivoTrue();
        
        // Ticket médio
        BigDecimal averageTicket = totalSales > 0 ? 
            totalRevenue.divide(BigDecimal.valueOf(totalSales), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        // Taxa de conversão (vendas / clientes)
        Double conversionRate = totalClients > 0 ? 
            (double) totalSales / totalClients * 100 : 0.0;
        
        // Cálculo de crescimento
        BigDecimal revenueGrowth = calculateGrowth(revenueThisPeriod, revenuePreviousPeriod);
        Double clientsGrowth = calculateGrowthDouble(newClientsThisPeriod, newClientsPreviousPeriod);
        Double salesGrowth = calculateGrowthDouble(salesThisPeriod, salesPreviousPeriod);

        return new DashboardMetricsDTO(
            totalClients, newClientsThisPeriod,
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO, 
            revenueThisPeriod != null ? revenueThisPeriod : BigDecimal.ZERO,
            totalSales, salesThisPeriod, activeUsers,
            averageTicket, conversionRate,
            revenueGrowth, clientsGrowth, salesGrowth
        );
    }

    public Map<String, Object> getRevenueChart(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<Venda> vendas = vendaRepository.findByDataVendaBetweenOrderByDataVenda(startDate, endDate);
        
        Map<String, BigDecimal> dailyRevenue = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Inicializar todos os dias com 0
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dailyRevenue.put(date.format(formatter), BigDecimal.ZERO);
        }
        
        // Agrupar vendas por dia
        vendas.forEach(venda -> {
            String dateKey = venda.getDataVenda().format(formatter);
            if (dailyRevenue.containsKey(dateKey)) {
                dailyRevenue.put(dateKey, dailyRevenue.get(dateKey).add(venda.getValorTotal()));
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(dailyRevenue.keySet()));
        result.put("data", new ArrayList<>(dailyRevenue.values()));
        result.put("total", dailyRevenue.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return result;
    }

    public Map<String, Object> getSalesChart(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);
        
        List<Venda> vendas = vendaRepository.findByDataVendaBetweenOrderByDataVenda(startDate, endDate);
        
        Map<String, Long> dailySales = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Inicializar todos os dias com 0
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dailySales.put(date.format(formatter), 0L);
        }
        
        // Contar vendas por dia
        vendas.forEach(venda -> {
            String dateKey = venda.getDataVenda().format(formatter);
            if (dailySales.containsKey(dateKey)) {
                dailySales.put(dateKey, dailySales.get(dateKey) + 1);
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(dailySales.keySet()));
        result.put("data", new ArrayList<>(dailySales.values()));
        result.put("total", dailySales.values().stream().mapToLong(Long::longValue).sum());
        
        return result;
    }

    public Map<String, Object> getClientsGrowth(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<Cliente> clientes = clienteRepository.findByDatahoraCadastroBetweenOrderByDatahoraCadastro(startDate, endDate);
        
        Map<String, Long> dailyClients = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        
        // Inicializar todos os dias com 0
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dailyClients.put(date.format(formatter), 0L);
        }
        
        // Contar novos clientes por dia
        clientes.forEach(cliente -> {
            String dateKey = cliente.getDatahoraCadastro().toLocalDate().format(formatter);
            if (dailyClients.containsKey(dateKey)) {
                dailyClients.put(dateKey, dailyClients.get(dateKey) + 1);
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", new ArrayList<>(dailyClients.keySet()));
        result.put("data", new ArrayList<>(dailyClients.values()));
        result.put("total", dailyClients.values().stream().mapToLong(Long::longValue).sum());
        
        return result;
    }

    public Map<String, Object> getRecentClients(int limit) {
        List<Cliente> recentClients = clienteRepository.findTopByOrderByDatahoraCadastroDesc(limit);
        
        List<Map<String, Object>> clients = recentClients.stream().map(cliente -> {
            Map<String, Object> clientMap = new HashMap<>();
            clientMap.put("id", cliente.getIdCliente());
            clientMap.put("nome", cliente.getRazaoSocial());
            clientMap.put("email", cliente.getEmail());
            clientMap.put("telefone", cliente.getTelefone());
            clientMap.put("dataCadastro", cliente.getDatahoraCadastro());
            clientMap.put("cidade", cliente.getCidade());
            return clientMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("clients", clients);
        result.put("total", recentClients.size());
        
        return result;
    }

    public Map<String, Object> getRecentSales(int limit) {
        List<Venda> recentSales = vendaRepository.findTopByOrderByDataVendaDesc(limit);
        
        List<Map<String, Object>> sales = recentSales.stream().map(venda -> {
            Map<String, Object> saleMap = new HashMap<>();
            saleMap.put("id", venda.getIdVenda());
            saleMap.put("numeroOrcamento", venda.getNumeroOrcamento());
            saleMap.put("valorTotal", venda.getValorTotal());
            saleMap.put("dataVenda", venda.getDataVenda());
            saleMap.put("status", venda.getStatus());
            saleMap.put("cliente", venda.getCliente() != null ? venda.getCliente().getRazaoSocial() : "N/A");
            saleMap.put("usuario", venda.getUsuarioCadastro() != null ? venda.getUsuarioCadastro().getNome() : "N/A");
            return saleMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("sales", sales);
        result.put("total", recentSales.size());
        
        return result;
    }

    public Map<String, Object> getTopClients(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<Object[]> topClientsData = vendaRepository.findTopClientsByRevenue(startDate, endDate, 10);
        
        List<Map<String, Object>> topClients = topClientsData.stream().map(data -> {
            Map<String, Object> clientMap = new HashMap<>();
            clientMap.put("clienteNome", data[0]);
            clientMap.put("totalVendas", data[1]);
            clientMap.put("valorTotal", data[2]);
            return clientMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("clients", topClients);
        
        return result;
    }

    public Map<String, Object> getPerformance(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<Object[]> userPerformance = vendaRepository.findUserPerformance(startDate, endDate);
        
        List<Map<String, Object>> performance = userPerformance.stream().map(data -> {
            Map<String, Object> perfMap = new HashMap<>();
            perfMap.put("usuario", data[0]);
            perfMap.put("totalVendas", data[1]);
            perfMap.put("valorTotal", data[2]);
            return perfMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("performance", performance);
        
        return result;
    }

    private BigDecimal calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ? 
                new BigDecimal("100") : BigDecimal.ZERO;
        }
        if (current == null) {
            return new BigDecimal("-100");
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    private Double calculateGrowthDouble(long current, long previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double) (current - previous) / previous) * 100;
    }
}