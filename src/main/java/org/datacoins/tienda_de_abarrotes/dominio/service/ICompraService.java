// ===== INTERFAZ SERVICE COMPRA =====
package org.datacoins.tienda_de_abarrotes.dominio.service;

import org.datacoins.tienda_de_abarrotes.persistence.entity.Compra;
import org.datacoins.tienda_de_abarrotes.persistence.entity.DetalleCompra;
import java.time.LocalDate;
import java.util.List;

public interface ICompraService {

    List<Compra> listarCompras();

    List<Compra> listarComprasConDetalles();

    Compra buscarCompraPorId(Integer codigo);

    Compra guardarCompra(Compra compra);

    void eliminarCompra(Compra compra);

    List<Compra> buscarComprasPorFecha(LocalDate fecha);

    List<Compra> buscarComprasPorCliente(String clienteNombre);

    List<DetalleCompra> obtenerDetallesDeCompra(Integer codigoCompra);

    // Metodo para procesar compra completa (actualiza stock)
    Compra procesarCompraCompleta(Compra compra, List<DetalleCompra> detalles);
}