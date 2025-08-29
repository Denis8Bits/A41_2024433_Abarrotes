package org.datacoins.tienda_de_abarrotes.web.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.datacoins.tienda_de_abarrotes.dominio.service.ICompraService;
import org.datacoins.tienda_de_abarrotes.dominio.service.IProductoService;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Compra;
import org.datacoins.tienda_de_abarrotes.persistence.entity.DetalleCompra;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Data
@ViewScoped
public class ComprasController implements Serializable {

    @Autowired
    private ICompraService compraService;

    @Autowired
    private IProductoService productoService;

    // Listas principales
    private List<Compra> compras;
    private List<Producto> productosDisponibles;
    private Compra compraSeleccionada;

    // Para la creación de compras
    private List<DetalleCompra> detallesCompra;
    private List<Producto> productosSeleccionados;
    private DetalleCompra detalleActual;
    private Producto productoParaAgregar;
    private Integer cantidadParaAgregar;

    // Variables de estado
    private boolean modoEdicion = false;
    private double totalCompra = 0.0;

    private static final Logger logger = LoggerFactory.getLogger(ComprasController.class);

    @PostConstruct
    public void init() {
        cargarDatos();
        inicializarNuevaCompra();
        logger.info("ComprasController inicializado correctamente");
    }

    public void cargarDatos() {
        try {
            this.compras = compraService.listarComprasConDetalles();
            this.productosDisponibles = productoService.listarProductos();
            logger.info("Compras cargadas: {}, Productos disponibles: {}",
                    this.compras.size(), this.productosDisponibles.size());
        } catch (Exception e) {
            logger.error("Error al cargar datos", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "No se pudieron cargar los datos"));
        }
    }

    public void inicializarNuevaCompra() {
        this.compraSeleccionada = new Compra();
        this.compraSeleccionada.setFechaCompra(LocalDate.now());
        this.compraSeleccionada.setHoraCompra(LocalTime.now());
        this.compraSeleccionada.setMetodoPago("Efectivo");

        this.detallesCompra = new ArrayList<>();
        this.productosSeleccionados = new ArrayList<>();
        this.totalCompra = 0.0;
        this.modoEdicion = false;

        // Limpiar variables de agregar producto
        this.productoParaAgregar = null;
        this.cantidadParaAgregar = 1;
    }

    public void nuevaCompra() {
        inicializarNuevaCompra();
        logger.info("Preparando nueva compra");
    }

    public void agregarProductoACompra() {
        try {
            if (productoParaAgregar == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Debe seleccionar un producto"));
                return;
            }

            if (cantidadParaAgregar == null || cantidadParaAgregar <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "La cantidad debe ser mayor a 0"));
                return;
            }

            if (cantidadParaAgregar > productoParaAgregar.getStock()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Stock insuficiente. Disponible: " + productoParaAgregar.getStock()));
                return;
            }

            // Verificar si el producto ya está en la lista
            boolean productoExistente = false;
            for (DetalleCompra detalle : detallesCompra) {
                if (detalle.getProducto().getCodigoProducto().equals(productoParaAgregar.getCodigoProducto())) {
                    // Actualizar cantidad existente
                    int nuevaCantidad = detalle.getCantidad() + cantidadParaAgregar;
                    if (nuevaCantidad > productoParaAgregar.getStock()) {
                        FacesContext.getCurrentInstance().addMessage(null,
                                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                        "La cantidad total excedería el stock disponible"));
                        return;
                    }
                    detalle.setCantidad(nuevaCantidad);
                    detalle.calcularSubtotal();
                    productoExistente = true;
                    break;
                }
            }

            if (!productoExistente) {
                // Agregar nuevo producto
                DetalleCompra nuevoDetalle = new DetalleCompra(productoParaAgregar, cantidadParaAgregar);
                detallesCompra.add(nuevoDetalle);

                if (!productosSeleccionados.contains(productoParaAgregar)) {
                    productosSeleccionados.add(productoParaAgregar);
                }
            }

            // Recalcular total
            calcularTotalCompra();

            // Limpiar selección
            this.productoParaAgregar = null;
            this.cantidadParaAgregar = 1;

            // Actualizar UI
            PrimeFaces.current().ajax().update("formulario-modal:panel-productos-seleccionados",
                    "formulario-modal:total-compra", "formulario-modal:agregar-producto-panel");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto agregado a la compra"));

            logger.info("Producto agregado: {} - Cantidad: {}",
                    (productoExistente ? "actualizado" : "nuevo"), cantidadParaAgregar);

        } catch (Exception e) {
            logger.error("Error al agregar producto", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al agregar producto: " + e.getMessage()));
        }
    }

    public void removerProductoDeCompra(DetalleCompra detalle) {
        try {
            detallesCompra.remove(detalle);
            productosSeleccionados.remove(detalle.getProducto());
            calcularTotalCompra();

            PrimeFaces.current().ajax().update("formulario-modal:panel-productos-seleccionados",
                    "formulario-modal:total-compra");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Producto Removido",
                            "Se eliminó " + detalle.getProducto().getNombreProducto() + " de la compra"));

        } catch (Exception e) {
            logger.error("Error al remover producto", e);
        }
    }

    public void actualizarCantidadDetalle(DetalleCompra detalle) {
        try {
            if (detalle.getCantidad() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "La cantidad debe ser mayor a 0"));
                return;
            }

            if (detalle.getCantidad() > detalle.getProducto().getStock()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Stock insuficiente para " + detalle.getProducto().getNombreProducto()));
                detalle.setCantidad(1); // Resetear a 1
                return;
            }

            detalle.calcularSubtotal();
            calcularTotalCompra();

            PrimeFaces.current().ajax().update("formulario-modal:panel-productos-seleccionados",
                    "formulario-modal:total-compra");

        } catch (Exception e) {
            logger.error("Error al actualizar cantidad", e);
        }
    }

    private void calcularTotalCompra() {
        this.totalCompra = detallesCompra.stream()
                .mapToDouble(DetalleCompra::getSubtotal)
                .sum();
        this.compraSeleccionada.setTotalCompra(this.totalCompra);
    }

    public void guardarCompra() {
        try {
            // Validaciones
            if (compraSeleccionada.getClienteNombre() == null ||
                    compraSeleccionada.getClienteNombre().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "El nombre del cliente es obligatorio"));
                return;
            }

            if (detallesCompra.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "Debe agregar al menos un producto a la compra"));
                return;
            }

            logger.info("Procesando compra: Cliente: {}, Total: {}, Productos: {}",
                    compraSeleccionada.getClienteNombre(), totalCompra, detallesCompra.size());

            // Procesar compra completa (actualiza stock automáticamente)
            Compra compraGuardada = compraService.procesarCompraCompleta(compraSeleccionada, detallesCompra);

            // Recargar datos
            cargarDatos();
            inicializarNuevaCompra();

            // Cerrar modal y mostrar mensaje
            PrimeFaces.current().executeScript("PF('VentanaModalCompra').hide()");
            PrimeFaces.current().ajax().update("formulario-compras:mensaje_emergente",
                    "formulario-compras:tabla-compras");

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Venta Exitosa",
                            String.format("Compra #%d procesada correctamente. Total: $%.2f",
                                    compraGuardada.getCodigoCompra(), compraGuardada.getTotalCompra())));

            logger.info("Compra guardada exitosamente: ID {}", compraGuardada.getCodigoCompra());

        } catch (Exception e) {
            logger.error("Error al guardar compra", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al procesar la compra: " + e.getMessage()));
        }
    }

    public void eliminarCompra() {
        try {
            if (compraSeleccionada == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                                "No hay compra seleccionada"));
                return;
            }

            logger.info("Eliminando compra: {}", compraSeleccionada.getCodigoCompra());

            // Eliminar compra (restaura stock automáticamente)
            compraService.eliminarCompra(compraSeleccionada);

            // Remover de la lista
            compras.remove(compraSeleccionada);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                            "Compra eliminada y stock restaurado correctamente"));

            // Actualizar UI
            PrimeFaces.current().ajax().update("formulario-compras:mensaje_emergente",
                    "formulario-compras:tabla-compras");

            this.compraSeleccionada = null;

        } catch (Exception e) {
            logger.error("Error al eliminar compra", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al eliminar la compra: " + e.getMessage()));
        }
    }

    public void verDetalleCompra() {
        try {
            if (compraSeleccionada != null) {
                // Cargar detalles de la compra seleccionada
                List<DetalleCompra> detalles = compraService.obtenerDetallesDeCompra(
                        compraSeleccionada.getCodigoCompra());
                compraSeleccionada.setDetalles(detalles);

                logger.info("Mostrando detalles de compra: {}", compraSeleccionada.getCodigoCompra());
            }
        } catch (Exception e) {
            logger.error("Error al cargar detalles", e);
        }
    }

    // Métodos de utilidad
    public boolean isCompraVacia() {
        return detallesCompra.isEmpty();
    }

    public int getCantidadTotalProductos() {
        return detallesCompra.stream()
                .mapToInt(DetalleCompra::getCantidad)
                .sum();
    }

    public String getResumenCompra() {
        if (isCompraVacia()) {
            return "Sin productos seleccionados";
        }
        return String.format("%d productos - $%.2f total",
                getCantidadTotalProductos(), totalCompra);
    }

    // Getters para productos con stock disponible
    public List<Producto> getProductosConStock() {
        return productosDisponibles.stream()
                .filter(p -> p.getStock() != null && p.getStock() > 0)
                .collect(java.util.stream.Collectors.toList());
    }
}