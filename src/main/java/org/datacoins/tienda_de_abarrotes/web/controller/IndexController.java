package org.datacoins.tienda_de_abarrotes.web.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import lombok.Data;
import org.datacoins.tienda_de_abarrotes.dominio.service.ICompraService;
import org.datacoins.tienda_de_abarrotes.dominio.service.IProductoService;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Compra;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Data
@ViewScoped
public class IndexController implements Serializable {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private ICompraService compraService;

    // Estadísticas del dashboard
    private int totalProductos;
    private int comprasHoy;
    private int productosStockBajo;
    private double ingresosHoy;
    private List<Producto> productosConStockBajo;
    private List<Compra> ventasHoy;
    private String fechaActual;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @PostConstruct
    public void init() {
        cargarDashboard();
        logger.info("IndexController inicializado correctamente");
    }

    public void cargarDashboard() {
        try {
            // Establecer fecha actual
            this.fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Cargar productos
            List<Producto> productos = productoService.listarProductos();
            this.totalProductos = productos.size();

            // Calcular productos con stock bajo
            this.productosConStockBajo = productos.stream()
                    .filter(p -> p.getStock() != null && p.getStock() < 10)
                    .collect(Collectors.toList());
            this.productosStockBajo = this.productosConStockBajo.size();

            // Cargar compras de hoy
            this.ventasHoy = compraService.buscarComprasPorFecha(LocalDate.now());
            this.comprasHoy = this.ventasHoy.size();

            // Calcular ingresos de hoy
            this.ingresosHoy = this.ventasHoy.stream()
                    .mapToDouble(Compra::getTotalCompra)
                    .sum();

            logger.info("Dashboard cargado - Productos: {}, Compras hoy: {}, Stock bajo: {}, Ingresos: {}",
                    totalProductos, comprasHoy, productosStockBajo, ingresosHoy);

        } catch (Exception e) {
            logger.error("Error al cargar dashboard", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al cargar información del dashboard"));
        }
    }

    public void mostrarVentasHoy() {
        try {
            if (ventasHoy.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Información",
                                "No hay ventas registradas para hoy"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Ventas de Hoy",
                                String.format("Se han realizado %d ventas por un total de $%.2f",
                                        comprasHoy, ingresosHoy)));
            }
        } catch (Exception e) {
            logger.error("Error al mostrar ventas de hoy", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al obtener información de ventas"));
        }
    }

    public void mostrarProductosPopulares() {
        try {
            // Aquí podrías implementar lógica para obtener productos más vendidos
            // Por ahora, mostraremos un mensaje informativo
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Productos Populares",
                            "Funcionalidad en desarrollo. Próximamente disponible."));
        } catch (Exception e) {
            logger.error("Error al mostrar productos populares", e);
        }
    }

    public void mostrarStockBajo() {
        try {
            if (productosStockBajo == 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Stock Óptimo",
                                "Todos los productos tienen stock suficiente"));
            } else {
                StringBuilder mensaje = new StringBuilder("Productos con stock bajo:\n");
                for (Producto producto : productosConStockBajo) {
                    mensaje.append(String.format("• %s: %d unidades\n",
                            producto.getNombreProducto(), producto.getStock()));
                }

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Alerta de Stock",
                                String.format("%d productos necesitan reabastecimiento", productosStockBajo)));
            }
        } catch (Exception e) {
            logger.error("Error al mostrar stock bajo", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                            "Error al obtener información de stock"));
        }
    }

    // Métodos de utilidad para el dashboard
    public String getEstadoStock() {
        if (productosStockBajo == 0) {
            return "Óptimo";
        } else if (productosStockBajo <= 3) {
            return "Atención";
        } else {
            return "Crítico";
        }
    }

    public String getClaseEstadoStock() {
        if (productosStockBajo == 0) {
            return "text-green-600";
        } else if (productosStockBajo <= 3) {
            return "text-orange-600";
        } else {
            return "text-red-600";
        }
    }

    public String getResumenDia() {
        return String.format("Hoy: %d ventas - $%.2f ingresos - %d productos con stock bajo",
                comprasHoy, ingresosHoy, productosStockBajo);
    }

    public double getPromedioVentaPorTransaccion() {
        if (comprasHoy == 0) return 0.0;
        return ingresosHoy / comprasHoy;
    }

    public boolean isTieneDatos() {
        return totalProductos > 0;
    }

    public boolean isStockCritico() {
        return productosStockBajo > 0;
    }

    public String getMensajeBienvenida() {
        int hora = java.time.LocalTime.now().getHour();
        String saludo;

        if (hora < 12) {
            saludo = "Buenos días";
        } else if (hora < 18) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }

        return saludo + "! Bienvenido al sistema de gestión.";
    }
}