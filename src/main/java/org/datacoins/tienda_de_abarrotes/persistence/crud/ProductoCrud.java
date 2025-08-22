package org.datacoins.tienda_de_abarrotes.persistence.crud;

import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.springframework.data.repository.CrudRepository;

public interface ProductoCrud extends CrudRepository<Producto, Integer> {
    //Sustituye al DAO
    //Esta interfaz tiene todos los metodos genericos del CRUD
}
