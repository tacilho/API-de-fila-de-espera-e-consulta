package com.example.restaurant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mesas")
@Data
@NoArgsConstructor
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Número da mesa é obrigatório")
    @Min(value = 1, message = "Número da mesa deve ser maior que zero")
    private Integer numero;

    @Column(nullable = false)
    @NotNull(message = "Capacidade é obrigatória")
    @Min(value = 1, message = "Capacidade deve ser maior que zero")
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMesa status = StatusMesa.DISPONIVEL;
    
    @ManyToOne
    @JoinColumn(name = "mesa_agrupada_id")
    private Mesa mesaAgrupada;
    
    @Column(name = "capacidade_agrupada")
    private Integer capacidadeAgrupada;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = StatusMesa.DISPONIVEL;
        }
        if (capacidadeAgrupada == null) {
            capacidadeAgrupada = capacidade;
        }
    }
} 