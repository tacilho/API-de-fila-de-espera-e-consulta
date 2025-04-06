package com.example.restaurant.service;

import com.example.restaurant.model.HistoricoAlocacao;
import com.example.restaurant.model.Mesa;
import com.example.restaurant.repository.HistoricoAlocacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class HistoricoAlocacaoService {
    private static final Logger logger = LoggerFactory.getLogger(HistoricoAlocacaoService.class);

    @Autowired
    private HistoricoAlocacaoRepository historicoAlocacaoRepository;

    @Transactional(readOnly = true)
    public List<HistoricoAlocacao> getHistorico() {
        logger.info("Buscando histórico de alocações");
        List<HistoricoAlocacao> historico = historicoAlocacaoRepository.findAll();
        logger.info("Total de registros no histórico: {}", historico.size());
        return historico;
    }

    @Transactional
    public void registrarAlocacao(HistoricoAlocacao historicoAlocacao) {
        logger.info("Registrando alocação no histórico");
        try {
            historicoAlocacaoRepository.save(historicoAlocacao);
            logger.info("Alocação registrada com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao registrar alocação: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<HistoricoAlocacao> getHistoricoPorMesa(Mesa mesa) {
        logger.info("Buscando histórico de alocações para a mesa {}", mesa.getId());
        List<HistoricoAlocacao> historico = historicoAlocacaoRepository.findByMesa(mesa);
        logger.info("Total de registros encontrados: {}", historico.size());
        return historico;
    }

    @Transactional(readOnly = true)
    public long contarAlocacoes() {
        logger.info("Contando total de alocações no histórico");
        long count = historicoAlocacaoRepository.count();
        logger.info("Total de alocações: {}", count);
        return count;
    }
} 