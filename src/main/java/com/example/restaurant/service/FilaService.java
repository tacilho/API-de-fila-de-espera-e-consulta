package com.example.restaurant.service;

import com.example.restaurant.exception.ClienteNaoEncontradoException;
import com.example.restaurant.model.Cliente;
import com.example.restaurant.model.StatusCliente;
import com.example.restaurant.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FilaService {
    private static final Logger logger = LoggerFactory.getLogger(FilaService.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> getFila() {
        logger.info("Buscando fila de espera");
        List<Cliente> clientes = clienteRepository.findByStatusOrderByPrioridadeDescHoraEntradaAsc(StatusCliente.AGUARDANDO);
        logger.info("Total de clientes na fila: {}", clientes.size());
        return clientes;
    }

    @Transactional
    public Cliente adicionarCliente(Cliente cliente) {
        logger.info("Adicionando cliente à fila: {}", cliente.getNome());
        try {
            cliente.setStatus(StatusCliente.AGUARDANDO);
            cliente.setHoraEntrada(LocalDateTime.now());
            
            // Salva o cliente primeiro para obter o ID
            Cliente clienteSalvo = clienteRepository.save(cliente);
            
            // Busca todos os clientes aguardando, ordenados por prioridade e hora de entrada
            List<Cliente> clientes = clienteRepository.findByStatusOrderByPrioridadeDescHoraEntradaAsc(StatusCliente.AGUARDANDO);
            
            // Atualiza as posições de todos os clientes
            for (int i = 0; i < clientes.size(); i++) {
                clientes.get(i).setPosicao(i + 1);
                clienteRepository.save(clientes.get(i));
            }
            
            logger.info("Cliente adicionado com sucesso: {}", clienteSalvo.getId());
            return clienteSalvo;
        } catch (Exception e) {
            logger.error("Erro ao adicionar cliente: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void removerCliente(Long id) {
        logger.info("Removendo cliente da fila: {}", id);
        try {
            Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
            clienteRepository.delete(cliente);
            logger.info("Cliente removido com sucesso: {}", id);
        } catch (Exception e) {
            logger.error("Erro ao remover cliente: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void atualizarStatus(Long id, StatusCliente status) {
        logger.info("Atualizando status do cliente {} para {}", id, status);
        try {
            Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException("Cliente não encontrado"));
            cliente.setStatus(status);
            clienteRepository.save(cliente);
            logger.info("Status do cliente {} atualizado com sucesso para {}", id, status);
        } catch (Exception e) {
            logger.error("Erro ao atualizar status do cliente: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Cliente encontrarClienteIdeal(int capacidadeMesa, int tempoMaximoEspera) {
        logger.info("Buscando cliente ideal para mesa com capacidade {} e tempo máximo de espera {} segundos", 
            capacidadeMesa, tempoMaximoEspera);
        
        try {
            List<Cliente> clientes = clienteRepository
                .findByStatusAndPessoasLessThanEqualOrderByPrioridadeDescHoraEntradaAsc(
                    StatusCliente.AGUARDANDO, capacidadeMesa);

            if (clientes.isEmpty()) {
                logger.info("Nenhum cliente encontrado para a capacidade da mesa");
                return null;
            }

            LocalDateTime tempoLimite = LocalDateTime.now().minusSeconds(tempoMaximoEspera);
            for (Cliente cliente : clientes) {
                if (cliente.getHoraEntrada().isAfter(tempoLimite)) {
                    logger.info("Cliente ideal encontrado: {}", cliente.getNome());
                    return cliente;
                }
            }

            logger.info("Nenhum cliente encontrado dentro do tempo máximo de espera");
            return null;
        } catch (Exception e) {
            logger.error("Erro ao buscar cliente ideal: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public long contarClientesNaFila() {
        logger.info("Contando clientes na fila");
        long count = clienteRepository.findByStatus(StatusCliente.AGUARDANDO).size();
        logger.info("Total de clientes na fila: {}", count);
        return count;
    }

    @Transactional
    public void inicializarPosicoes() {
        logger.info("Inicializando posições dos clientes na fila");
        List<Cliente> clientes = clienteRepository.findByStatusOrderByPrioridadeDescHoraEntradaAsc(StatusCliente.AGUARDANDO);
        for (int i = 0; i < clientes.size(); i++) {
            clientes.get(i).setPosicao(i + 1);
            clienteRepository.save(clientes.get(i));
        }
        logger.info("Posições inicializadas para {} clientes", clientes.size());
    }

    @Transactional(readOnly = true)
    public List<Cliente> getClientesAguardando() {
        logger.info("Buscando clientes aguardando");
        List<Cliente> clientes = clienteRepository.findByStatusOrderByPrioridadeDescHoraEntradaAsc(StatusCliente.AGUARDANDO);
        logger.info("Total de clientes aguardando: {}", clientes.size());
        return clientes;
    }
} 