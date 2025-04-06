package com.example.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "fila_espera")
@Data
@NoArgsConstructor
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Pattern(regexp = "^[0-9+()-]*$", message = "Telefone deve conter apenas números, +, -, ( e )")
    private String telefone;

    @Column(nullable = false)
    @NotNull(message = "Número de pessoas é obrigatório")
    @Min(value = 1, message = "Número de pessoas deve ser maior que zero")
    private Integer pessoas;

    @Column(name = "hora_entrada", nullable = false)
    private LocalDateTime horaEntrada;

    @Column(nullable = false)
    @Min(value = 0, message = "Prioridade não pode ser negativa")
    private Integer prioridade = 0;

    @Column(length = 500)
    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCliente status = StatusCliente.AGUARDANDO;

    @Column(nullable = false)
    private Integer posicao = 0;

    @PrePersist
    public void prePersist() {
        if (horaEntrada == null) {
            horaEntrada = LocalDateTime.now();
        }
        if (status == null) {
            status = StatusCliente.AGUARDANDO;
        }
        if (prioridade == null) {
            prioridade = 0;
        }
        if (posicao == null) {
            posicao = 0;
        }
    }
} 