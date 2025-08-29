// ===== REPOSITORIO DETALLE COMPRA =====
package org.datacoins.tienda_de_abarrotes.persistence.crud;

import org.datacoins.tienda_de_abarrotes.persistence.entity.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DetalleCompraCrud extends JpaRepository<DetalleCompra, Integer> {

    // Buscar detalles por código de compra
    List<DetalleCompra> findByCompra_CodigoCompra(Integer codigoCompra);

    // Buscar detalles por producto
    List<DetalleCompra> findByProducto_CodigoProducto(Integer codigoProducto);

    // Consulta para obtener detalles con información completa
    @Query("SELECT d FROM DetalleCompra d JOIN FETCH d.producto JOIN FETCH d.compra WHERE d.compra.codigoCompra = :codigoCompra")
    List<DetalleCompra> findDetallesConProductosByCompra(@Param("codigoCompra") Integer codigoCompra);
}