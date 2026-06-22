package com.farmape.backend.compras.repository;

import com.farmape.backend.compras.model.FacturaCompra;
import com.farmape.backend.compras.model.PagoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface PagoProveedorRepository extends JpaRepository<PagoProveedor, Integer> {
    List<PagoProveedor> findByFacturaCompra(FacturaCompra facturaCompra);

    default BigDecimal totalPagado(FacturaCompra facturaCompra) {
        return findByFacturaCompra(facturaCompra).stream()
                .map(PagoProveedor::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
