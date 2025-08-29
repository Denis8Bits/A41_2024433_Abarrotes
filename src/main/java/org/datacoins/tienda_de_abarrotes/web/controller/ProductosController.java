package org.datacoins.tienda_de_abarrotes.web.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.datacoins.tienda_de_abarrotes.dominio.service.IProductoService;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@Data
@ViewScoped
public class ProductosController implements Serializable {

    @Autowired
    private IProductoService productoService;

    private List<Producto> productos;
    private Producto productoSeleccionado;

    private static final Logger logger = LoggerFactory.getLogger(ProductosController.class);

    @PostConstruct
    public void init() {
        cargarDatos();
        logger.info("ProductosController inicializado correctamente");
    }

    public void cargarDatos() {
        try {
            this.productos = this.productoService.listarProductos();
            logger.info("Productos cargados: {}", this.productos.size());
            this.productos.forEach(producto -> logger.debug(producto.toString()));
        } catch (Exception e) {
            logger.error("Error al cargar productos", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron cargar los productos"));
        }
    }

    public void agregarProducto() {
        this.productoSeleccionado = new Producto();
        logger.info("Preparando nuevo producto");
    }

    public void guardarProducto() {
        try {
            logger.info("Producto a guardar: {}", this.productoSeleccionado);

            // Validaciones básicas
            if (this.productoSeleccionado.getNombreProducto() == null ||
                    this.productoSeleccionado.getNombreProducto().trim().isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El nombre del producto es obligatorio"));
                return;
            }

            if (this.productoSeleccionado.getPrecioProducto() == null ||
                    this.productoSeleccionado.getPrecioProducto() <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El precio debe ser mayor a 0"));
                return;
            }

            if (this.productoSeleccionado.getStock() == null ||
                    this.productoSeleccionado.getStock() < 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "El stock no puede ser negativo"));
                return;
            }

            // Determinar si es nuevo o actualización
            boolean esNuevo = this.productoSeleccionado.getCodigoProducto() == null;

            // Guardar el producto
            this.productoService.guardarProducto(this.productoSeleccionado);

            if (esNuevo) {
                // Recargar lista para obtener el ID generado
                cargarDatos();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto agregado correctamente"));
                logger.info("Producto nuevo guardado con ID: {}", this.productoSeleccionado.getCodigoProducto());
            } else {
                // Actualizar el producto en la lista
                int index = productos.indexOf(this.productoSeleccionado);
                if (index >= 0) {
                    productos.set(index, this.productoSeleccionado);
                }
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente"));
                logger.info("Producto actualizado: {}", this.productoSeleccionado.getCodigoProducto());
            }

            // Cerrar modal y actualizar componentes
            PrimeFaces.current().executeScript("PF('VentanaModalProducto').hide()");
            PrimeFaces.current().ajax().update("formulario-productos:mensaje_emergente",
                    "formulario-productos:tabla-productos");

            // Limpiar selección
            this.productoSeleccionado = null;

        } catch (Exception e) {
            logger.error("Error al guardar producto", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error al guardar el producto: " + e.getMessage()));
        }
    }

    public void eliminarProducto() {
        try {
            if (this.productoSeleccionado == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No hay producto seleccionado"));
                return;
            }

            logger.info("Producto a eliminar: {}", this.productoSeleccionado);

            // Eliminar el producto
            this.productoService.eliminarProducto(this.productoSeleccionado);

            // Remover de la lista
            this.productos.remove(this.productoSeleccionado);

            // Mensaje de éxito
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito",
                            "Producto '" + this.productoSeleccionado.getNombreProducto() + "' eliminado correctamente"));

            logger.info("Producto eliminado: {}", this.productoSeleccionado.getCodigoProducto());

            // Limpiar selección
            this.productoSeleccionado = null;

            // Actualizar componentes
            PrimeFaces.current().ajax().update("formulario-productos:mensaje_emergente",
                    "formulario-productos:tabla-productos");

        } catch (Exception e) {
            logger.error("Error al eliminar producto", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al eliminar el producto. Puede estar siendo utilizado en compras existentes."));
        }
    }

    // Metodo para verificar stock bajo
    public String getEstiloStock(Integer stock) {
        if (stock == null) return "";

        if (stock < 10) {
            return "color: red; font-weight: bold;";
        } else if (stock < 25) {
            return "color: orange; font-weight: bold;";
        } else {
            return "color: green;";
        }
    }

    // Metodo para obtener clase CSS según stock
    public String getClaseStock(Integer stock) {
        if (stock == null) return "";

        if (stock < 10) {
            return "stock-bajo";
        } else if (stock < 25) {
            return "stock-medio";
        } else {
            return "stock-alto";
        }
    }

    // Método para contar productos con stock bajo
    public long getProductosStockBajo() {
        if (productos == null) return 0;
        return productos.stream()
                .filter(p -> p.getStock() != null && p.getStock() < 10)
                .count();
    }

    // Metodo para obtener valor total del inventario
    public double getValorTotalInventario() {
        if (productos == null) return 0.0;
        return productos.stream()
                .filter(p -> p.getPrecioProducto() != null && p.getStock() != null)
                .mapToDouble(p -> p.getPrecioProducto() * p.getStock())
                .sum();
    }
}