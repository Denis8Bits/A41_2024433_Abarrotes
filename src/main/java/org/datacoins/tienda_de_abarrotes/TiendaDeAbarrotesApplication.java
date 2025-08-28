package org.datacoins.tienda_de_abarrotes;

import org.datacoins.tienda_de_abarrotes.dominio.service.IProductoService;
import org.datacoins.tienda_de_abarrotes.persistence.entity.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class TiendaDeAbarrotesApplication implements CommandLineRunner {

    // Inyección de dependencia - CORREGIDO: nombre de variable coherente
    @Autowired
    private IProductoService productoService;

    // CORREGIDO: Usar Logger de SLF4J en lugar de java.util.logging
    private static final Logger logger = LoggerFactory.getLogger(TiendaDeAbarrotesApplication.class);

    // Crear un objeto String para saltos de línea
    String sl = System.lineSeparator();

    public static void main(String[] args) {
        logger.info("AQUI INICIA LA APLICACION");
        SpringApplication.run(TiendaDeAbarrotesApplication.class, args);
        logger.info("AQUI TERMINA LA APLICACION");
    }

    @Override
    public void run(String... args) throws Exception {
        abarrotesProductoApp();
    }

    // CORREGIDO: Nombre del método en camelCase
    private void abarrotesProductoApp() {
        logger.info("+++++++++++APLICACION DE REGISTRO DE PRODUCTOS++++++++");
        var salir = false;
        var consola = new Scanner(System.in);
        while (!salir) {
            var opcion = mostrarMenu(consola);
            salir = ejecutarOpciones(consola, opcion);
            logger.info(sl);
        }
        consola.close(); // AGREGADO: Cerrar el scanner
    }

    // CORREGIDO: Nombre del método (mostrarMenu en lugar de mostarMenu)
    private int mostrarMenu(Scanner consola) {
        logger.info("""
                \n ***Aplicacion de Tienda de Abarrotes***
                1. Listar todos los Productos.
                2. Buscar producto por codigo
                3. Agregar nuevo producto.
                4. Modificar producto.
                5. Eliminar producto.
                6. Salir.
                Elija una opcion:\s""");
        var opcion = Integer.parseInt(consola.nextLine());
        return opcion;
    }

    private boolean ejecutarOpciones(Scanner consola, int opcion) {
        var salir = false;
        switch (opcion) {
            case 1 -> {
                logger.info(sl + "*** Listado de todos los productos ***" + sl);
                List<Producto> productos = productoService.listarProductos();
                productos.forEach(producto -> logger.info(producto.toString() + sl));
            }
            case 2 -> {
                logger.info(sl + "*** Buscar producto por su codigo ***" + sl);
                logger.info("Ingrese el codigo del producto: ");
                try {
                    var codigo = Integer.parseInt(consola.nextLine());
                    Producto producto = productoService.buscarProductoPorId(codigo);
                    if (producto != null) {
                        logger.info("Producto encontrado: " + sl + producto + sl);
                    } else {
                        logger.info("Producto NO encontrado con codigo: " + codigo + sl);
                    }
                } catch (NumberFormatException e) {
                    logger.info("Error: Debe ingresar un numero valido para el codigo." + sl);
                }
            }
            case 3 -> {
                logger.info(sl + "*** Agregar nuevo producto ***" + sl);
                logger.info("Ingrese el nombre del producto: ");
                var nombreProducto = consola.nextLine();
                logger.info("Ingrese la descripcion: ");
                var descripcionProducto = consola.nextLine();

                try {
                    logger.info("Ingrese el precio: ");
                    var precioProductoStr = consola.nextLine();
                    var precioProducto = Double.parseDouble(precioProductoStr);
                    logger.info("Ingrese la cantidad: ");
                    var stockStr = consola.nextLine();
                    var stock = Integer.parseInt(stockStr);

                    var producto = new Producto();
                    producto.setNombreProducto(nombreProducto);
                    producto.setDescripcionProducto(descripcionProducto);
                    producto.setPrecioProducto(precioProducto);
                    producto.setStock(stock);
                    productoService.guardarProducto(producto);
                    logger.info("Producto agregado: " + sl + producto + sl);
                } catch (NumberFormatException e) {
                    logger.info("Error: Debe ingresar numeros validos para precio y stock." + sl);
                }
            }
            case 4 -> {
                logger.info(sl + "*** Modificar producto ***" + sl);
                logger.info("Ingrese el codigo del producto a editar: ");
                try {
                    var codigo = Integer.parseInt(consola.nextLine());
                    Producto producto = productoService.buscarProductoPorId(codigo);
                    if (producto != null) {
                        logger.info("Producto encontrado: " + producto + sl);
                        logger.info("Ingrese el nuevo nombre: ");
                        var nombreProducto = consola.nextLine();
                        logger.info("Ingrese la nueva descripcion: ");
                        var descripcionProducto = consola.nextLine();

                        try {
                            logger.info("Ingrese el nuevo precio: ");
                            var precioProductoStr = consola.nextLine();
                            var precioProducto = Double.parseDouble(precioProductoStr);
                            logger.info("Ingrese el nuevo stock: ");
                            var stockStr = consola.nextLine();
                            var stock = Integer.parseInt(stockStr);

                            producto.setNombreProducto(nombreProducto);
                            producto.setDescripcionProducto(descripcionProducto);
                            producto.setPrecioProducto(precioProducto);
                            producto.setStock(stock);
                            productoService.guardarProducto(producto);
                            logger.info("Producto modificado: " + sl + producto + sl);
                        } catch (NumberFormatException e) {
                            logger.info("Error: Debe ingresar numeros validos para precio y stock." + sl);
                        }
                    } else {
                        logger.info("Producto NO encontrado con codigo: " + codigo + sl);
                    }
                } catch (NumberFormatException e) {
                    logger.info("Error: Debe ingresar un numero valido para el codigo." + sl);
                }
            }
            case 5 -> {
                logger.info(sl + "*** Eliminar producto ***" + sl);
                logger.info("Ingrese el codigo del producto a eliminar: ");
                try {
                    var codigo = Integer.parseInt(consola.nextLine());
                    var producto = productoService.buscarProductoPorId(codigo);
                    if (producto != null) {
                        productoService.eliminarProducto(producto);
                        logger.info("Producto eliminado: " + sl + producto + sl);
                    } else {
                        logger.info("Producto NO encontrado con codigo: " + codigo + sl);
                    }
                } catch (NumberFormatException e) {
                    logger.info("Error: Debe ingresar un numero valido para el codigo." + sl);
                }
            }
            case 6 -> {
                logger.info("¡Hasta luego!");
                salir = true;
            }
            default -> {
                logger.info("Opcion no valida. Por favor seleccione una opcion del 1 al 6.");
            }
        }
        return salir;
    }
}