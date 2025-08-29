package org.datacoins.tienda_de_abarrotes.web.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import org.datacoins.tienda_de_abarrotes.dominio.service.IProductoService;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@FacesConverter(value = "productoConverter", managed = true)
public class ProductoConverter implements Converter<Producto> {

    @Autowired
    private IProductoService productoService;

    @Override
    public Producto getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            Integer id = Integer.valueOf(value);
            return productoService.buscarProductoPorId(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Producto producto) {
        if (producto == null) {
            return "";
        }
        return producto.getCodigoProducto().toString();
    }
}