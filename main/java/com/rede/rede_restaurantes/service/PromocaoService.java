
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Promocao;
import com.rede.rede_restaurantes.repository.PromocaoRepository;
import com.rede.rede_restaurantes.repository.PedidoPromocaoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;
    private final PedidoPromocaoRepository pedidoPromocaoRepository;

    public PromocaoService(
            PromocaoRepository promocaoRepository,
            PedidoPromocaoRepository pedidoPromocaoRepository) {

        this.promocaoRepository = promocaoRepository;
        this.pedidoPromocaoRepository = pedidoPromocaoRepository;
    }

    public List<Promocao> listarTodas() {
        return promocaoRepository.findAll();
    }

    public List<Promocao> listarAtivas() {
        return promocaoRepository.findByAtivoTrue();
    }

    public Promocao buscarPorId(Long id) {
        return promocaoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Promoção não encontrada"));
    }

    public List<Promocao> buscarPorNome(String nome) {
        return promocaoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Promocao salvar(Promocao promocao) {

        if (promocao.getId() != null) {

            buscarPorId(promocao.getId());

            boolean vinculada = pedidoPromocaoRepository
                    .existsByPromocaoId(promocao.getId());

            if (vinculada) {
                throw new IllegalArgumentException(
                        "Não é possível alterar uma promoção vinculada a um pedido");
            }
        }

        if (promocao.getTipoDesconto() == null) {
            throw new IllegalArgumentException(
                    "O tipo de desconto é obrigatório");
        }

        String tipo = promocao.getTipoDesconto()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!tipo.equals("PERCENTUAL") && !tipo.equals("FIXO")) {
            throw new IllegalArgumentException(
                    "Tipo de desconto inválido. Utilize PERCENTUAL ou FIXO");
        }

        promocao.setTipoDesconto(tipo);

        if (promocao.getValorDesconto() == null
                || promocao.getValorDesconto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor do desconto deve ser maior que zero");
        }

        if (tipo.equals("PERCENTUAL")
                && promocao.getValorDesconto()
                .compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(
                    "O desconto percentual não pode ultrapassar 100%");
        }

        if (promocao.getDataInicio() == null
                || promocao.getDataFim() == null) {
            throw new IllegalArgumentException(
                    "As datas de início e fim são obrigatórias");
        }

        if (!promocao.getDataFim().isAfter(promocao.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data de fim deve ser posterior à data de início");
        }

        if (promocao.getAtivo() == null) {
            throw new IllegalArgumentException(
                    "O campo ativo é obrigatório");
        }

        return promocaoRepository.save(promocao);
    }

    @Transactional
    public void excluir(Long id) {

        Promocao promocao = buscarPorId(id);

        boolean vinculada = pedidoPromocaoRepository
                .existsByPromocaoId(id);

        if (vinculada) {
            throw new IllegalArgumentException(
                    "Não é possível excluir uma promoção vinculada a um pedido");
        }

        promocaoRepository.delete(promocao);
    }
}
