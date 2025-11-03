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
        try {
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
            if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
            
            BigDecimal revenueThisPeriod = vendaRepository.sumValueByPeriod(startDate, endDate);
            if (revenueThisPeriod == null) revenueThisPeriod = BigDecimal.ZERO;
            
            BigDecimal revenuePreviousPeriod = vendaRepository.sumValueByPeriod(previousStartDate, startDate);
            if (revenuePreviousPeriod == null) revenuePreviousPeriod = BigDecimal.ZERO;
            
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

            DashboardMetricsDTO result = new DashboardMetricsDTO(
                totalClients, newClientsThisPeriod,
                totalRevenue, revenueThisPeriod,
                totalSales, salesThisPeriod, activeUsers,
                averageTicket, conversionRate,
                revenueGrowth, clientsGrowth, salesGrowth
            );
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Erro ao calcular métricas: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar métricas vazias em caso de erro
            return new DashboardMetricsDTO(
                0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO, 
                0L, 0L, 0L, BigDecimal.ZERO, 0.0, 
                BigDecimal.ZERO, 0.0, 0.0
            );
        }
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

    /**
     * Relatório detalhado de vendas por período
     */
    public Map<String, Object> getVendasDetailReport(int months) {
        try {
            
            LocalDateTime endDate = LocalDateTime.now();
            List<Map<String, Object>> vendasDetail = new ArrayList<>();
            
            for (int i = 0; i < months; i++) {
                LocalDateTime monthStart = endDate.minusMonths(i + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
                
                LocalDate startDate = monthStart.toLocalDate();
                LocalDate endDateLocal = monthEnd.toLocalDate();
                
                long vendas = vendaRepository.countByDataVendaBetween(startDate, endDateLocal);
                BigDecimal valor = vendaRepository.sumValueByPeriod(monthStart, monthEnd);
                if (valor == null) valor = BigDecimal.ZERO;
                
                BigDecimal ticket = vendas > 0 ? valor.divide(BigDecimal.valueOf(vendas), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                
                // Calcular crescimento comparado com mês anterior
                LocalDateTime prevMonthStart = monthStart.minusMonths(1);
                LocalDateTime prevMonthEnd = monthStart.minusSeconds(1);
                BigDecimal valorAnterior = vendaRepository.sumValueByPeriod(prevMonthStart, prevMonthEnd);
                if (valorAnterior == null) valorAnterior = BigDecimal.ZERO;
                
                BigDecimal crescimento = calculateGrowth(valor, valorAnterior);
                
                Map<String, Object> periodo = new HashMap<>();
                periodo.put("periodo", monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("pt-BR"))));
                periodo.put("vendas", vendas);
                periodo.put("valor", valor);
                periodo.put("ticket", ticket);
                periodo.put("crescimento", crescimento);
                periodo.put("meta", calculateMetaPercentage(valor)); // Método auxiliar para calcular % da meta
                
                vendasDetail.add(periodo);
            }
            
            Collections.reverse(vendasDetail); // Ordem cronológica
            
            Map<String, Object> result = new HashMap<>();
            result.put("vendasDetail", vendasDetail);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de vendas: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar estrutura vazia em caso de erro
            Map<String, Object> result = new HashMap<>();
            result.put("vendasDetail", new ArrayList<>());
            return result;
        }
    }

    /**
     * Relatório detalhado de clientes por período
     */
    public Map<String, Object> getClientesDetailReport(int months) {
        try {
            
            LocalDateTime endDate = LocalDateTime.now();
            List<Map<String, Object>> clientesDetail = new ArrayList<>();
            
            for (int i = 0; i < months; i++) {
                LocalDateTime monthStart = endDate.minusMonths(i + 1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                LocalDateTime monthEnd = monthStart.plusMonths(1).minusSeconds(1);
                
                long novos = clienteRepository.countByDatahoraCadastroBetween(monthStart, monthEnd);
                long total = clienteRepository.countByDatahoraCadastroLessThanEqual(monthEnd);
                
                // Calcular crescimento
                LocalDateTime prevMonthEnd = monthStart.minusSeconds(1);
                long totalAnterior = clienteRepository.countByDatahoraCadastroLessThanEqual(prevMonthEnd);
                Double crescimento = calculateGrowthDouble(novos, Math.max(1, total - totalAnterior));
                
                // Calcular LTV médio (baseado nas vendas do mês)
                BigDecimal ltvTotal = vendaRepository.sumValueByPeriod(monthStart, monthEnd);
                if (ltvTotal == null) ltvTotal = BigDecimal.ZERO;
                BigDecimal ltv = novos > 0 ? ltvTotal.divide(BigDecimal.valueOf(novos), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                
                Map<String, Object> periodo = new HashMap<>();
                periodo.put("periodo", monthStart.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("pt-BR"))));
                periodo.put("novos", novos);
                periodo.put("total", total);
                periodo.put("retencao", calculateRetentionRate(monthStart, monthEnd));
                periodo.put("crescimento", crescimento);
                periodo.put("ltv", ltv);
                
                clientesDetail.add(periodo);
            }
            
            Collections.reverse(clientesDetail); // Ordem cronológica
            
            Map<String, Object> result = new HashMap<>();
            result.put("clientesDetail", clientesDetail);
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Erro ao gerar relatório de clientes: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar estrutura vazia em caso de erro
            Map<String, Object> result = new HashMap<>();
            result.put("clientesDetail", new ArrayList<>());
            return result;
        }
    }

    /**
     * Dados de vendas para exportação
     */
    public Map<String, Object> getVendasForExport(String startDateStr, String endDateStr) {
        LocalDate startDate = startDateStr != null ? 
            LocalDate.parse(startDateStr) : 
            LocalDate.now().minusMonths(1);
        LocalDate endDate = endDateStr != null ? 
            LocalDate.parse(endDateStr) : 
            LocalDate.now();
        
        List<Venda> vendas = vendaRepository.findByDataVendaBetweenOrderByDataVendaDesc(startDate, endDate);
        
        List<Map<String, Object>> vendasFormatted = vendas.stream().map(venda -> {
            Map<String, Object> vendaMap = new HashMap<>();
            vendaMap.put("id", venda.getIdVenda());
            vendaMap.put("data", venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            vendaMap.put("cliente", venda.getCliente().getRazaoSocial());
            vendaMap.put("valorBruto", venda.getValorBruto());
            vendaMap.put("valorTotal", venda.getValorTotal());
            vendaMap.put("desconto", venda.getValorDesconto());
            vendaMap.put("status", venda.getStatus());
            vendaMap.put("cupom", venda.getCupom() != null ? venda.getCupom().getCodigo() : "");
            vendaMap.put("observacao", venda.getObservacao() != null ? venda.getObservacao() : "");
            return vendaMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("vendas", vendasFormatted);
        result.put("total", vendas.size());
        result.put("valorTotal", vendas.stream()
            .map(Venda::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return result;
    }

    /**
     * Dados de clientes para exportação
     */
    public Map<String, Object> getClientesForExport() {
        try {
            
            List<Cliente> clientes = clienteRepository.findAllByOrderByDatahoraCadastroDesc();
            
            List<Map<String, Object>> clientesFormatted = clientes.stream().map(cliente -> {
                Map<String, Object> clienteMap = new HashMap<>();
                clienteMap.put("id", cliente.getIdCliente());
                clienteMap.put("nome", cliente.getRazaoSocial());
                clienteMap.put("email", cliente.getEmail());
                clienteMap.put("telefone", cliente.getTelefone());
                clienteMap.put("endereco", cliente.getEndereco());
                clienteMap.put("dataCadastro", cliente.getDatahoraCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                
                // Calcular total de compras
                BigDecimal totalCompras = vendaRepository.sumValueByCliente(cliente.getIdCliente());
                clienteMap.put("totalCompras", totalCompras != null ? totalCompras : BigDecimal.ZERO);
                
                long totalVendas = vendaRepository.countByClienteId(cliente.getIdCliente());
                clienteMap.put("totalVendas", totalVendas);
                
                return clienteMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("clientes", clientesFormatted);
            result.put("total", clientes.size());
            
            return result;
            
        } catch (Exception e) {
            System.err.println("Erro ao exportar clientes: " + e.getMessage());
            e.printStackTrace();
            
            // Retornar estrutura vazia em caso de erro
            Map<String, Object> result = new HashMap<>();
            result.put("clientes", new ArrayList<>());
            result.put("total", 0);
            return result;
        }
    }

    // Métodos auxiliares
    private BigDecimal calculateMetaPercentage(BigDecimal valor) {
        // Meta fictícia de R$ 750.000 por mês
        BigDecimal meta = new BigDecimal("750000");
        if (meta.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return valor.divide(meta, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    private String calculateRetentionRate(LocalDateTime monthStart, LocalDateTime monthEnd) {
        // Cálculo simplificado de retenção
        // Em um cenário real, seria baseado em clientes que fizeram mais de uma compra
        long clientesComVendas = vendaRepository.countDistinctClientesByPeriod(monthStart.toLocalDate(), monthEnd.toLocalDate());
        long totalClientes = clienteRepository.countByDatahoraCadastroBefore(monthEnd);
        
        if (totalClientes == 0) return "0%";
        double retencao = ((double) clientesComVendas / totalClientes) * 100;
        return String.format("%.0f%%", retencao);
    }
}
