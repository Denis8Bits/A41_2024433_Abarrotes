package org.datacoins.tienda_de_abarrotes.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Productos") // Nombre exacto de la tabla (con mayúscula inicial)
// Lombok
@Data // Getters y Setters
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode // Código de autenticación de la entidad
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigoProducto") // Importante, renombra a camelcase xd
    private Integer codigoProducto;

    @Column(name = "nombreProducto")
    private String nombreProducto;

    @Column(name = "descripcionProducto")
    private String descripcionProducto;

    @Column(name = "precioProducto")
    private double precioProducto;

    @Column(name = "stock")
    private Integer stock;
}