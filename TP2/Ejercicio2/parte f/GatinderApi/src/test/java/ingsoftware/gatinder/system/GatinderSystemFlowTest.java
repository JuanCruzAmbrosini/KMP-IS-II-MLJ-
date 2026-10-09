package ingsoftware.gatinder.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ingsoftware.gatinder.controller.ViewController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TIPO DE PRUEBA: PRUEBAS DEL SISTEMA (SYSTEM TESTING / E2E)
 * Objetivo: Validar el flujo del sistema de extremo a extremo, simulando las peticiones
 * del navegador hacia los controladores web y comprobando el renderizado de vistas Thymeleaf.
 */
@SpringBootTest
public class GatinderSystemFlowTest {

    @Autowired
    private ViewController viewController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(viewController).setViewResolvers(viewResolver).build();
    }

    @Test
    @DisplayName("System Test: GET '/' responde HTTP 200 y renderiza la vista 'index'")
    void testRoot_RespondeOk() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("System Test: GET '/login' responde HTTP 200 y renderiza la vista 'login'")
    void testLogin_RespondeOk() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("System Test: GET '/register' responde HTTP 200 y provee listado de zonas al modelo")
    void testRegister_RespondeOk() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("zones"));
    }

    @Test
    @DisplayName("System Test: GET '/home' responde HTTP 200 y renderiza la vista 'home'")
    void testHome_RespondeOk() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }
}
