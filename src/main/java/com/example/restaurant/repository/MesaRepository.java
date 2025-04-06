package com.example.restaurant.repository;

import com.example.restaurant.model.Mesa;
import com.example.restaurant.model.StatusMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {
    List<Mesa> findByMesaAgrupada(Mesa mesaAgrupada);
    List<Mesa> findByStatusAndMesaAgrupadaIsNull(StatusMesa status);
    List<Mesa> findByStatus(StatusMesa status);
} 