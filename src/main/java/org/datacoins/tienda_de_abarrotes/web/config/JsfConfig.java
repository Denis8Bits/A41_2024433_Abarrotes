package org.datacoins.tienda_de_abarrotes.web.config;

import jakarta.faces.webapp.FacesServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class JsfConfig {

    @Bean
    ServletRegistrationBean<FacesServlet> jsfServletRegistration() {
        ServletRegistrationBean<FacesServlet> srb = new ServletRegistrationBean<>();
        srb.setServlet(new FacesServlet());
        srb.setUrlMappings(Collections.singleton("*.xhtml"));
        srb.setLoadOnStartup(1);
        return srb;
    }
}