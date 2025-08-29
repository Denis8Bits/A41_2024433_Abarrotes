// ===== ENTIDAD DETALLE COMPRA =====
package org.datacoins.tienda_de_abarrotes.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DetalleCompras")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigoDetalle")
    private Integer codigoDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigoCompra", nullable = false)
    @ToString.Exclude // Evitamos problemas de recursión en toString :)
    private Compra compra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "codigoProducto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precioUnitario", nullable = false)
    private Double precioUnitario;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    // Constructor con parámetros básicos
    public DetalleCompra(Producto producto, Integer cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioProducto();
        calcularSubtotal();
    }

    // Constructor sin ID's
    public DetalleCompra(Compra compra, Producto producto, Integer cantidad) {
        this.compra = compra;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioProducto();
        calcularSubtotal();
    }

    // Metodo para calcular subtotal
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        }
    }

    // Setter para recalcular subtotal
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    // Setter para recalcular subtotal
    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
}