package com.example.restaurant.repository;

import com.example.restaurant.model.HistoricoAlocacao;
import com.example.restaurant.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoAlocacaoRepository extends JpaRepository<HistoricoAlocacao, Long> {
    List<HistoricoAlocacao> findByMesa(Mesa mesa);
} 