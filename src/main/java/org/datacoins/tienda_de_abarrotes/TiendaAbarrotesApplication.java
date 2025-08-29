package org.datacoins.tienda_de_abarrotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.datacoins.tienda_de_abarrotes")
public class TiendaAbarrotesApplication {

    public static void main(String[] args) {
        // Configurar propiedades del sistema para JSF
        System.setProperty("java.awt.headless", "true");
        System.setProperty("primefaces.THEME", "nova-light");

        SpringApplication.run(TiendaAbarrotesApplication.class, args);

        System.out.println("=================================");
        System.out.println(" TIENDA DE ABARROTES INICIADA");
        System.out.println("=================================");
        System.out.println(" http://localhost:8080/tienda-abarrotes");
        System.out.println("     Páginas disponibles:   ");
        System.out.println("   • Dashboard: /index.xhtml    ");
        System.out.println("   • Productos: /productos.xhtml    ");
        System.out.println("   • Compras: /compras.xhtml    ");
        System.out.println("=================================");
    }
}