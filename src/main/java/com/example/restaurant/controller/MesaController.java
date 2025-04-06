package com.example.restaurant.controller;

import com.example.restaurant.exception.MesaNaoEncontradaException;
import com.example.restaurant.exception.OperacaoInvalidaException;
import com.example.restaurant.model.Mesa;
import com.example.restaurant.model.StatusMesa;
import com.example.restaurant.service.MesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {
    private static final Logger logger = LoggerFactory.getLogger(MesaController.class);

    @Autowired
    private MesaService mesaService;

    @GetMapping
    public ResponseEntity<List<Mesa>> getMesas() {
        logger.info("Recebendo requisição para listar mesas");
        try {
            List<Mesa> mesas = mesaService.getMesas();
            logger.info("Retornando {} mesas", mesas.size());
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            logger.error("Erro ao listar mesas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatusMesa(
            @PathVariable Long id,
            @RequestParam StatusMesa status) {
        logger.info("Recebendo requisição para atualizar status da mesa {} para {}", id, status);
        try {
            mesaService.atualizarStatusMesa(id, status);
            logger.info("Status da mesa {} atualizado com sucesso", id);
            return ResponseEntity.ok().build();
        } catch (MesaNaoEncontradaException e) {
            logger.error("Mesa não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao atualizar status da mesa: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/alocar")
    public ResponseEntity<Void> alocarMesa(@PathVariable Long id) {
        logger.info("Recebendo requisição para alocar mesa {}", id);
        try {
            mesaService.alocarMesa(id);
            logger.info("Mesa {} alocada com sucesso", id);
            return ResponseEntity.ok().build();
        } catch (MesaNaoEncontradaException e) {
            logger.error("Mesa não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (OperacaoInvalidaException e) {
            logger.error("Operação inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro ao alocar mesa: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/agrupar")
    public ResponseEntity<Void> agruparMesas(
            @RequestParam Long mesaPrincipalId,
            @RequestParam Long mesaSecundariaId) {
        logger.info("Recebendo requisição para agrupar mesas {} e {}", mesaPrincipalId, mesaSecundariaId);
        try {
            mesaService.agruparMesas(mesaPrincipalId, mesaSecundariaId);
            logger.info("Mesas agrupadas com sucesso");
            return ResponseEntity.ok().build();
        } catch (MesaNaoEncontradaException e) {
            logger.error("Mesa não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (OperacaoInvalidaException e) {
            logger.error("Operação inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro ao agrupar mesas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/desagrupar")
    public ResponseEntity<Void> desagruparMesas(@PathVariable Long id) {
        logger.info("Recebendo requisição para desagrupar mesa {}", id);
        try {
            mesaService.desagruparMesas(id);
            logger.info("Mesas desagrupadas com sucesso");
            return ResponseEntity.ok().build();
        } catch (MesaNaoEncontradaException e) {
            logger.error("Mesa não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Erro ao desagrupar mesas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/liberar")
    public ResponseEntity<Void> liberarMesa(@PathVariable Long id) {
        logger.info("Recebendo requisição para liberar mesa {}", id);
        try {
            mesaService.liberarMesa(id);
            logger.info("Mesa {} liberada com sucesso", id);
            return ResponseEntity.ok().build();
        } catch (MesaNaoEncontradaException e) {
            logger.error("Mesa não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (OperacaoInvalidaException e) {
            logger.error("Operação inválida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Erro ao liberar mesa: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<Long> contarMesasDisponiveis() {
        logger.info("Recebendo requisição para contar mesas disponíveis");
        try {
            long count = mesaService.contarMesasDisponiveis();
            logger.info("Total de mesas disponíveis: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Erro ao contar mesas disponíveis: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/disponiveis-agrupamento")
    public ResponseEntity<List<Mesa>> getMesasDisponiveisParaAgrupamento() {
        logger.info("Recebendo requisição para listar mesas disponíveis para agrupamento");
        try {
            List<Mesa> mesas = mesaService.getMesasDisponiveisParaAgrupamento();
            logger.info("Retornando {} mesas disponíveis para agrupamento", mesas.size());
            return ResponseEntity.ok(mesas);
        } catch (Exception e) {
            logger.error("Erro ao listar mesas disponíveis para agrupamento: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Mesa> criarMesa(@RequestBody Mesa mesa) {
        logger.info("Recebendo requisição para criar nova mesa");
        try {
            Mesa novaMesa = mesaService.criarMesa(mesa);
            logger.info("Mesa criada com sucesso: {}", novaMesa);
            return ResponseEntity.ok(novaMesa);
        } catch (Exception e) {
            logger.error("Erro ao criar mesa: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
} 