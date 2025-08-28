package org.datacoins.tienda_de_abarrotes.dominio.service;

import org.datacoins.tienda_de_abarrotes.persistence.crud.ProductoCrud;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // AGREGADO: Anotación @Service para que Spring lo reconozca como un bean
public class ProductoService implements IProductoService {

    // Inyección de dependencias del repositorio (ProductoCrud)
    @Autowired
    private ProductoCrud crud;

    @Override
    public List<Producto> listarProductos() {
        Iterable<Producto> productos = crud.findAll();
        return (List<Producto>) productos;
    }

    @Override
    public Producto buscarProductoPorId(Integer codigo) { // CORREGIDO: Parámetro debe ser Integer, no Producto
        return crud.findById(codigo).orElse(null); // CORREGIDO: findById en lugar de findAllById
    }

    @Override
    public void guardarProducto(Producto producto) {
        crud.save(producto);
    }

    @Override
    public void eliminarProducto(Producto producto) {
        crud.delete(producto);
    }
}