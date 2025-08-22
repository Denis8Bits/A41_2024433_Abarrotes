package org.datacoins.tienda_de_abarrotes.dominio.service;


import org.datacoins.tienda_de_abarrotes.persistence.crud.ProductoCrud;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ProductoService implements IProductoService {

    //Inyeccion de dependencias del repositorio (ProductoCrud)
    @Autowired
    private ProductoCrud crud;

    @Override
    public List<Producto> listarProductos
}
