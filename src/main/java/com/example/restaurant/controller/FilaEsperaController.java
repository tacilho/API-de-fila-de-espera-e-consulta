package com.example.restaurant.controller;

import com.example.restaurant.model.Cliente;
import com.example.restaurant.model.StatusCliente;
import com.example.restaurant.service.FilaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fila")
public class FilaEsperaController {
    @Autowired
    private FilaService filaService;

    @GetMapping
    public ResponseEntity<List<Cliente>> getFila() {
        try {
            List<Cliente> fila = filaService.getClientesAguardando();
            return ResponseEntity.ok(fila);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody StatusCliente status) {
        try {
            filaService.atualizarStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao atualizar status: " + e.getMessage());
        }
    }
}