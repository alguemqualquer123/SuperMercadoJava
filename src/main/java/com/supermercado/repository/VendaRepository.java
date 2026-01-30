package com.supermercado.repository;

import com.supermercado.model.Venda;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByDataVendaBetweenOrderByDataVendaDesc(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT v FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA' ORDER BY v.dataVenda DESC")
    List<Venda> findVendasFinalizadasDoDia(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA'")
    BigDecimal calcularTotalPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COUNT(v) FROM Venda v WHERE v.dataVenda BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA'")
    long countVendasPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT iv.nomeProduto, SUM(iv.quantidade), SUM(iv.subtotal) " +
            "FROM ItemVenda iv JOIN iv.venda v " +
            "WHERE v.dataVenda BETWEEN :inicio AND :fim AND v.status = 'FINALIZADA' " +
            "GROUP BY iv.nomeProduto " +
            "ORDER BY SUM(iv.quantidade) DESC")
    List<Object[]> findProdutosMaisVendidos(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim,
            Pageable pageable);
}
