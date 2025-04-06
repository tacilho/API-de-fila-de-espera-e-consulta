package com.example.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_alocacao")
@Data
@NoArgsConstructor
public class HistoricoAlocacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_cliente", nullable = false, length = 100)
    @NotBlank(message = "Nome do cliente é obrigatório")
    @Size(max = 100, message = "Nome do cliente deve ter no máximo 100 caracteres")
    private String nomeCliente;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    @Pattern(regexp = "^[0-9+()-]*$", message = "Telefone deve conter apenas números, +, -, ( e )")
    private String telefone;

    @Column(name = "data_hora_chamada", nullable = false)
    private LocalDateTime dataHoraChamada;

    @ManyToOne
    @JoinColumn(name = "mesa_id", nullable = false)
    @NotNull(message = "Mesa é obrigatória")
    private Mesa mesa;

    @PrePersist
    public void prePersist() {
        if (dataHoraChamada == null) {
            dataHoraChamada = LocalDateTime.now();
        }
    }
} 