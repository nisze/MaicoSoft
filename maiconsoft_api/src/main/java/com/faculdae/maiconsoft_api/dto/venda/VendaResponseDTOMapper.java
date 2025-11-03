package com.faculdae.maiconsoft_api.dto.venda;

import com.faculdae.maiconsoft_api.entities.Venda;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Mapper para conversão da entidade Venda para DTOs de resposta
 */
@Service
public class VendaResponseDTOMapper implements Function<Venda, VendaResponseDTO> {

    @Override
    public VendaResponseDTO apply(Venda venda) {
        return VendaResponseDTO.builder()
                .idVenda(venda.getIdVenda())
                .numeroOrcamento(venda.getNumeroOrcamento())
                .status(venda.getStatus())
                .valorBruto(venda.getValorBruto())
                .valorDesconto(venda.getValorDesconto())
                .valorTotal(venda.getValorTotal())
                .dataVenda(venda.getDataVenda())
                .datahoraCadastro(venda.getDatahoraCadastro())
                .observacao(venda.getObservacao())
                
                // Dados do comprovante
                .comprovantePath(venda.getComprovantePath())
                .comprovanteUploadDate(venda.getComprovanteUploadDate())
                .comprovanteAnexado(venda.getComprovantePath() != null && !venda.getComprovantePath().trim().isEmpty())
                
                // Dados do cliente
                .clienteId(venda.getCliente() != null ? venda.getCliente().getIdCliente() : null)
                .clienteCodigo(venda.getCliente() != null ? venda.getCliente().getCodigo() : null)
                .clienteNome(venda.getCliente() != null ? venda.getCliente().getRazaoSocial() : null)
                
                // Dados do cupom (se aplicado)
                .cupomId(venda.getCupom() != null ? venda.getCupom().getIdCupom() : null)
                .cupomCodigo(venda.getCupom() != null ? venda.getCupom().getCodigo() : null)
                .cupomNome(venda.getCupom() != null ? venda.getCupom().getNome() : null)
                .cupomDesconto(venda.getCupom() != null ? venda.getCupom().getDescontoPercentual() : null)
                
                // Dados do usuário cadastrador
                .usuarioCadastroNome(venda.getUsuarioCadastro() != null ? 
                    venda.getUsuarioCadastro().getNome() : null)
                .usuarioCadastroCodigo(venda.getUsuarioCadastro() != null ? 
                    "USR" + String.format("%03d", venda.getUsuarioCadastro().getIdUser()) : null)
                .build();
    }

    /**
     * Converte Page<Venda> para VendaResponse
     * @param vendaPage Página de vendas do repository
     * @return VendaResponse com dados de paginação
     */
    public VendaResponse toVendaResponse(Page<Venda> vendaPage) {
        return VendaResponse.builder()
                .vendas(vendaPage.getContent().stream()
                    .map(this)
                    .toList())
                .currentPage(vendaPage.getNumber())
                .totalItems(vendaPage.getTotalElements())
                .totalPages(vendaPage.getTotalPages())
                .size(vendaPage.getSize())
                .hasNext(vendaPage.hasNext())
                .hasPrevious(vendaPage.hasPrevious())
                .isFirst(vendaPage.isFirst())
                .isLast(vendaPage.isLast())
                .build();
    }
}