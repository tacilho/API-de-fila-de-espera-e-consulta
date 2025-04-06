package com.example.restaurant.repository;

import com.example.restaurant.model.Cliente;
import com.example.restaurant.model.StatusCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByStatus(StatusCliente status);
    Optional<Cliente> findByTelefone(String telefone);
    List<Cliente> findByStatusOrderByPrioridadeDescHoraEntradaAsc(StatusCliente status);
    List<Cliente> findByStatusAndPessoasLessThanEqualOrderByPrioridadeDescHoraEntradaAsc(
        StatusCliente status, Integer pessoas);
} 