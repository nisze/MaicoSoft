package com.faculdae.maiconsoft_api.dto.cliente;

import com.faculdae.maiconsoft_api.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * Mapper para conversão da entidade Cliente para DTOs de resposta
 */
@Service
public class ClienteResponseDTOMapper implements Function<Cliente, ClienteResponseDTO> {

    @Override
    public ClienteResponseDTO apply(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .idCliente(cliente.getIdCliente())
                .codigo(cliente.getCodigo())
                .loja(cliente.getLoja())
                .razaoSocial(cliente.getRazaoSocial())
                .tipo(cliente.getTipo())
                .nomeFantasia(cliente.getNomeFantasia())
                .finalidade(cliente.getFinalidade())
                .cpfCnpj(cliente.getCpfCnpj())
                .cep(cliente.getCep())
                .pais(cliente.getPais())
                .estado(cliente.getEstado())
                .codMunicipio(cliente.getCodMunicipio())
                .cidade(cliente.getCidade())
                .endereco(cliente.getEndereco())
                .bairro(cliente.getBairro())
                .ddd(cliente.getDdd())
                .telefone(cliente.getTelefone())
                .abertura(cliente.getAbertura())
                .contato(cliente.getContato())
                .email(cliente.getEmail())
                .homepage(cliente.getHomepage())
                .datahoraCadastro(cliente.getDatahoraCadastro())
                .descricao(cliente.getDescricao())
                .usuarioCadastroNome(cliente.getUsuarioCadastro() != null ? 
                    cliente.getUsuarioCadastro().getNome() : null)
                .usuarioCadastroCodigo(cliente.getUsuarioCadastro() != null ? 
                    "USR" + String.format("%03d", cliente.getUsuarioCadastro().getIdUser()) : null)
                .build();
    }

    /**
     * Converte Page<Cliente> para ClienteResponse
     * @param clientePage Página de clientes do repository
     * @return ClienteResponse com dados de paginação
     */
    public ClienteResponse toClienteResponse(Page<Cliente> clientePage) {
        return ClienteResponse.builder()
                .clientes(clientePage.getContent().stream()
                    .map(this)
                    .toList())
                .currentPage(clientePage.getNumber())
                .totalItems(clientePage.getTotalElements())
                .totalPages(clientePage.getTotalPages())
                .size(clientePage.getSize())
                .hasNext(clientePage.hasNext())
                .hasPrevious(clientePage.hasPrevious())
                .isFirst(clientePage.isFirst())
                .isLast(clientePage.isLast())
                .build();
    }
}