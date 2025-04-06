package com.example.restaurant.service;

import com.example.restaurant.exception.MesaNaoEncontradaException;
import com.example.restaurant.exception.OperacaoInvalidaException;
import com.example.restaurant.model.Cliente;
import com.example.restaurant.model.Mesa;
import com.example.restaurant.model.StatusMesa;
import com.example.restaurant.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MesaService {
    private static final Logger logger = LoggerFactory.getLogger(MesaService.class);

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private FilaService filaService;

    @Transactional(readOnly = true)
    public List<Mesa> getMesas() {
        logger.info("Buscando todas as mesas");
        List<Mesa> mesas = mesaRepository.findAll();
        logger.info("Total de mesas encontradas: {}", mesas.size());
        return mesas;
    }

    @Transactional
    public void atualizarStatusMesa(Long id, StatusMesa status) {
        logger.info("Atualizando status da mesa {} para {}", id, status);
        try {
            Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));
            mesa.setStatus(status);
            mesaRepository.save(mesa);
            logger.info("Status da mesa {} atualizado com sucesso para {}", id, status);
        } catch (Exception e) {
            logger.error("Erro ao atualizar status da mesa {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void alocarMesa(Long id) {
        logger.info("Alocando mesa {}", id);
        try {
            Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));
            
            if (mesa.getStatus() == StatusMesa.OCUPADA) {
                throw new OperacaoInvalidaException("Mesa já está ocupada");
            }
            
            mesa.setStatus(StatusMesa.OCUPADA);
            mesaRepository.save(mesa);
            logger.info("Mesa {} alocada com sucesso", id);
        } catch (Exception e) {
            logger.error("Erro ao alocar mesa {}: {}", id, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void agruparMesas(Long mesaPrincipalId, Long mesaSecundariaId) {
        logger.info("Agrupando mesas {} e {}", mesaPrincipalId, mesaSecundariaId);
        
        try {
            Mesa mesaPrincipal = mesaRepository.findById(mesaPrincipalId)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa principal não encontrada"));
            
            Mesa mesaSecundaria = mesaRepository.findById(mesaSecundariaId)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa secundária não encontrada"));

            if (mesaPrincipal.getMesaAgrupada() != null || mesaSecundaria.getMesaAgrupada() != null) {
                throw new OperacaoInvalidaException("Uma ou ambas as mesas já estão agrupadas");
            }

            if (mesaPrincipal.getStatus() == StatusMesa.OCUPADA || mesaSecundaria.getStatus() == StatusMesa.OCUPADA) {
                throw new OperacaoInvalidaException("Uma ou ambas as mesas estão ocupadas");
            }

            mesaSecundaria.setMesaAgrupada(mesaPrincipal);
            mesaPrincipal.setCapacidadeAgrupada(mesaPrincipal.getCapacidade() + mesaSecundaria.getCapacidade());

            mesaRepository.save(mesaPrincipal);
            mesaRepository.save(mesaSecundaria);
            
            logger.info("Mesas agrupadas com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao agrupar mesas: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void desagruparMesas(Long mesaPrincipalId) {
        logger.info("Desagrupando mesa {}", mesaPrincipalId);
        
        try {
            Mesa mesaPrincipal = mesaRepository.findById(mesaPrincipalId)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa principal não encontrada"));

            List<Mesa> mesasAgrupadas = mesaRepository.findByMesaAgrupada(mesaPrincipal);

            for (Mesa mesa : mesasAgrupadas) {
                mesa.setMesaAgrupada(null);
                mesaRepository.save(mesa);
            }

            mesaPrincipal.setCapacidadeAgrupada(mesaPrincipal.getCapacidade());
            mesaRepository.save(mesaPrincipal);
            
            logger.info("Mesas desagrupadas com sucesso");
        } catch (Exception e) {
            logger.error("Erro ao desagrupar mesas: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void liberarMesa(Long mesaId) {
        logger.info("Liberando mesa {}", mesaId);
        try {
            Mesa mesa = mesaRepository.findById(mesaId)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa não encontrada"));
                
            if (mesa.getStatus() == StatusMesa.DISPONIVEL) {
                throw new OperacaoInvalidaException("Mesa já está disponível");
            }
            
            mesa.setStatus(StatusMesa.DISPONIVEL);
            mesaRepository.save(mesa);
            logger.info("Mesa {} liberada com sucesso", mesaId);

            Cliente cliente = filaService.encontrarClienteIdeal(mesa.getCapacidade(), 300);
            if (cliente != null) {
                logger.info("Cliente ideal encontrado para a mesa {}: {}", mesaId, cliente.getNome());
                logger.info("Notificação enviada para {} sobre mesa disponível para {} pessoas", 
                    cliente.getNome(), cliente.getPessoas());
            } else {
                logger.info("Nenhum cliente ideal encontrado para a mesa {}", mesaId);
            }
        } catch (Exception e) {
            logger.error("Erro ao liberar mesa {}: {}", mesaId, e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public long contarMesasDisponiveis() {
        logger.info("Contando mesas disponíveis");
        long count = mesaRepository.findByStatus(StatusMesa.DISPONIVEL).size();
        logger.info("Total de mesas disponíveis: {}", count);
        return count;
    }

    @Transactional(readOnly = true)
    public List<Mesa> getMesasDisponiveisParaAgrupamento() {
        logger.info("Buscando mesas disponíveis para agrupamento");
        List<Mesa> mesas = mesaRepository.findByStatusAndMesaAgrupadaIsNull(StatusMesa.DISPONIVEL);
        logger.info("Total de mesas disponíveis para agrupamento: {}", mesas.size());
        return mesas;
    }

    @Transactional
    public Mesa criarMesa(Mesa mesa) {
        logger.info("Criando nova mesa: {}", mesa);
        try {
            if (mesa.getNumero() == null || mesa.getCapacidade() == null) {
                throw new IllegalArgumentException("Número e capacidade são obrigatórios");
            }
            if (mesa.getStatus() == null) {
                mesa.setStatus(StatusMesa.DISPONIVEL);
            }
            Mesa novaMesa = mesaRepository.save(mesa);
            logger.info("Mesa criada com sucesso: {}", novaMesa);
            return novaMesa;
        } catch (Exception e) {
            logger.error("Erro ao criar mesa: {}", e.getMessage());
            throw e;
        }
    }
} 