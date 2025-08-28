package org.datacoins.tienda_de_abarrotes.dominio.service;

import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;

import java.util.List;

public interface IProductoService {

    List<Producto> listarProductos();

    Producto buscarProductoPorId(Integer codigo); // CORREGIDO: Parámetro Integer en lugar de Producto

    void guardarProducto(Producto producto);

    void eliminarProducto(Producto producto);
}