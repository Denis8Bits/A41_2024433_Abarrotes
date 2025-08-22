package org.datacoins.tienda_de_abarrotes.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Productos")

//Lombok
@Data //Getters y Setters
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode //Codigo de autenticacion de la entidad

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigoProducto;
    @Column
    private double precioP;
    private Integer stock;
}
