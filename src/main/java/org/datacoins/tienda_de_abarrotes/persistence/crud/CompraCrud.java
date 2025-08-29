// ===== REPOSITORIO COMPRA =====
package org.datacoins.tienda_de_abarrotes.persistence.crud;

import org.datacoins.tienda_de_abarrotes.persistence.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface CompraCrud extends JpaRepository<Compra, Integer> {

    // Buscar compras por fecha
    List<Compra> findByFechaCompra(LocalDate fecha);

    // Buscar compras por cliente
    List<Compra> findByClienteNombreContainingIgnoreCase(String clienteNombre);

    // Buscar compras por metodo de pago
    List<Compra> findByMetodoPago(String metodoPago);

    // Compras ordenadas por fecha descendente
    List<Compra> findAllByOrderByFechaCompraDescHoraCompraDesc();

    // Consulta para obtener compras con detalles
    @Query("SELECT DISTINCT c FROM Compra c LEFT JOIN FETCH c.detalles d LEFT JOIN FETCH d.producto")
    List<Compra> findAllWithDetails();
}