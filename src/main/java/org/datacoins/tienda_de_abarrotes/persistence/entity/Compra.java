// ===== ENTIDAD COMPRA =====
package org.datacoins.tienda_de_abarrotes.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigoCompra")
    private Integer codigoCompra;

    @Column(name = "fechaCompra", nullable = false)
    private LocalDate fechaCompra;

    @Column(name = "horaCompra")
    private LocalTime horaCompra;

    @Column(name = "totalCompra", nullable = false)
    private Double totalCompra = 0.0;

    @Column(name = "clienteNombre", length = 100)
    private String clienteNombre;

    @Column(name = "metodoPago", length = 50)
    private String metodoPago = "Efectivo";

    // Relación uno a muchos con DetalleCompra
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Evita problemas de recursión en toString
    private List<DetalleCompra> detalles = new ArrayList<>();

    // Constructor con parámetros básicos
    public Compra(LocalDate fechaCompra, String clienteNombre, String metodoPago) {
        this.fechaCompra = fechaCompra;
        this.horaCompra = LocalTime.now();
        this.clienteNombre = clienteNombre;
        this.metodoPago = metodoPago;
        this.totalCompra = 0.0;
        this.detalles = new ArrayList<>();
    }

    // Metodo para calcular el total
    public void calcularTotal() {
        this.totalCompra = detalles.stream()
                .mapToDouble(DetalleCompra::getSubtotal)
                .sum();
    }

    // Metodo para agregar detalle
    public void agregarDetalle(DetalleCompra detalle) {
        detalle.setCompra(this);
        this.detalles.add(detalle);
        calcularTotal();
    }
}