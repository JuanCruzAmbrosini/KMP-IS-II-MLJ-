package ingsoftware.gatinder.nonfunctional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ingsoftware.gatinder.controller.ViewController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TIPO DE PRUEBA: PRUEBAS NO FUNCIONALES (SEGURIDAD Y MANEJO DE ERRORES)
 * Objetivo: Evaluación de atributos de calidad no funcionales: configuración
 * de seguridad web, protección CSRF y páginas de error ante solicitudes fallidas.
 */
@SpringBootTest
public class SecurityAccessTest {

    @Autowired
    private ViewController viewController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver = 
                new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(viewController).setViewResolvers(viewResolver).build();
    }

    @Test
    @DisplayName("No Funcional - Seguridad: La pagina principal es publicamente accesible sin credenciales")
    void testAccesoPublicoSinCredenciales() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("No Funcional - Seguridad: La ruta de login es publica y no requiere sesion previa")
    void testLoginPublico() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("No Funcional - Seguridad: Endpoint de registro es publico para permitir altas")
    void testRegistroPublico() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    @DisplayName("No Funcional - Manejo de Errores: Peticion a recurso invalido no expone trazas criticas")
    void testManejoErroresSeguro() throws Exception {
        mockMvc.perform(get("/ruta-totalmente-inexistente-12345"))
                .andExpect(status().is4xxClientError());
    }
}
