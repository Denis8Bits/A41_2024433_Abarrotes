// ===== IMPLEMENTACIÓN SERVICE COMPRA =====
package org.datacoins.tienda_de_abarrotes.dominio.service;

import org.datacoins.tienda_de_abarrotes.persistence.crud.CompraCrud;
import org.datacoins.tienda_de_abarrotes.persistence.crud.DetalleCompraCrud;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Compra;
import org.datacoins.tienda_de_abarrotes.persistence.entity.DetalleCompra;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService implements ICompraService {

    @Autowired
    private CompraCrud compraCrud;

    @Autowired
    private DetalleCompraCrud detalleCompraCrud;

    @Autowired
    private IProductoService productoService;

    @Override
    public List<Compra> listarCompras() {
        return compraCrud.findAllByOrderByFechaCompraDescHoraCompraDesc();
    }

    @Override
    public List<Compra> listarComprasConDetalles() {
        return compraCrud.findAllWithDetails();
    }

    @Override
    public Compra buscarCompraPorId(Integer codigo) {
        return compraCrud.findById(codigo).orElse(null);
    }

    @Override
    @Transactional
    public Compra guardarCompra(Compra compra) {
        return compraCrud.save(compra);
    }

    @Override
    @Transactional
    public void eliminarCompra(Compra compra) {
        // Al eliminar la compra, los detalles se eliminan automáticamente (CASCADE)
        // Pero debemos restaurar el stock si es necesario
        List<DetalleCompra> detalles = obtenerDetallesDeCompra(compra.getCodigoCompra());
        for (DetalleCompra detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoService.guardarProducto(producto);
        }
        compraCrud.delete(compra);
    }

    @Override
    public List<Compra> buscarComprasPorFecha(LocalDate fecha) {
        return compraCrud.findByFechaCompra(fecha);
    }

    @Override
    public List<Compra> buscarComprasPorCliente(String clienteNombre) {
        return compraCrud.findByClienteNombreContainingIgnoreCase(clienteNombre);
    }

    @Override
    public List<DetalleCompra> obtenerDetallesDeCompra(Integer codigoCompra) {
        return detalleCompraCrud.findDetallesConProductosByCompra(codigoCompra);
    }

    @Override
    @Transactional
    public Compra procesarCompraCompleta(Compra compra, List<DetalleCompra> detalles) {
        // 1. Validar stock disponible
        for (DetalleCompra detalle : detalles) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombreProducto());
            }
        }

        // 2. Guardar la compra
        Compra compraGuardada = guardarCompra(compra);

        // 3. Procesar cada detalle
        for (DetalleCompra detalle : detalles) {
            detalle.setCompra(compraGuardada);
            detalle.setPrecioUnitario(detalle.getProducto().getPrecioProducto());
            detalle.calcularSubtotal();

            // Actualizar stock del producto
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoService.guardarProducto(producto);

            // Guardar el detalle
            detalleCompraCrud.save(detalle);
        }

        // 4. Actualizar total de la compra
        compraGuardada.setDetalles(detalles);
        compraGuardada.calcularTotal();

        return guardarCompra(compraGuardada);
    }
}