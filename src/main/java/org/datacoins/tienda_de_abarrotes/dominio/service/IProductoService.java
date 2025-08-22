package org.datacoins.tienda_de_abarrotes.dominio.service;

import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;

import java.util.List;

public interface IProductoService {
    //Firmas de metodo
    List<Producto> listarProducto();
    Producto buscarProductoPorId(Producto producto);
    void guardarProducto(Producto producto);
    void eliminarProducto(Producto producto;)
}
