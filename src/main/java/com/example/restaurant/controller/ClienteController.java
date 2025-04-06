package com.example.restaurant.controller;

import com.example.restaurant.model.Cliente;
import com.example.restaurant.model.StatusCliente;
import com.example.restaurant.repository.ClienteRepository;
import com.example.restaurant.service.FilaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private FilaService filaService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        logger.info("=== LISTANDO TODOS OS CLIENTES ===");
        List<Cliente> clientes = clienteRepository.findAll();
        logger.info("Total de clientes encontrados: {}", clientes.size());
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        logger.info("=== INICIANDO CADASTRO DE CLIENTE ===");
        logger.info("Dados recebidos: {}", cliente);
        
        try {
            // Validações básicas
            if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nome é obrigatório"));
            }
            
            if (cliente.getTelefone() == null || cliente.getTelefone().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Telefone é obrigatório"));
            }
            
            if (cliente.getPessoas() == null || cliente.getPessoas() <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Número de pessoas deve ser maior que zero"));
            }
            
            // Garante que a prioridade seja um número válido
            logger.info("Valor da prioridade antes da validação: {}", cliente.getPrioridade());
            
            if (cliente.getPrioridade() == null) {
                cliente.setPrioridade(0);
            } else if (cliente.getPrioridade() < 0) {
                cliente.setPrioridade(0);
            } else if (cliente.getPrioridade() > 10) {
                cliente.setPrioridade(10);
            }
            
            logger.info("Valor da prioridade após a validação: {}", cliente.getPrioridade());
            
            // Adiciona o cliente na fila
            Cliente clienteSalvo = filaService.adicionarCliente(cliente);
            logger.info("Cliente salvo com sucesso - ID: {}", clienteSalvo.getId());
            
            return ResponseEntity.ok(clienteSalvo);
        } catch (IllegalArgumentException e) {
            logger.error("Erro de validação: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao cadastrar cliente: {}", e.getMessage());
            logger.error("Stack trace completo:", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erro ao cadastrar cliente: " + e.getMessage()));
        }
    }

    @PostMapping("/inicializar-posicoes")
    public ResponseEntity<Void> inicializarPosicoes() {
        logger.info("=== INICIANDO ENDPOINT DE INICIALIZAÇÃO DE POSIÇÕES ===");
        
        try {
            filaService.inicializarPosicoes();
            logger.info("Posições inicializadas com sucesso");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("ERRO ao inicializar posições", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status/{telefone}")
    public ResponseEntity<?> verificarStatus(@PathVariable String telefone) {
        logger.info("=== INICIANDO VERIFICAÇÃO DE STATUS ===");
        logger.info("Telefone recebido: {}", telefone);

        try {
            // Limpa o telefone removendo caracteres não numéricos
            String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
            logger.info("Telefone limpo: {}", telefoneLimpo);

            // Busca o cliente usando o Optional
            Optional<Cliente> clienteOpt = clienteRepository.findByTelefone(telefoneLimpo);
            
            if (clienteOpt.isEmpty()) {
                logger.info("Cliente não encontrado para o telefone: {}", telefoneLimpo);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cliente não encontrado"));
            }

            Cliente cliente = clienteOpt.get();
            logger.info("Cliente encontrado: {}", cliente);

            // Prepara a resposta
            Map<String, Object> response = new HashMap<>();
            response.put("posicao", cliente.getPosicao());
            response.put("status", cliente.getStatus() != null ? cliente.getStatus().name() : "AGUARDANDO");
            
            logger.info("Resposta preparada: {}", response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("ERRO ao verificar status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erro ao verificar status: " + e.getMessage()));
        }
    }
} 