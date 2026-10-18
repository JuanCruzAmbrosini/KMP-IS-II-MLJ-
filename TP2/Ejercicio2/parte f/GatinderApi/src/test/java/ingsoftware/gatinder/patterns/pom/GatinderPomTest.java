package ingsoftware.gatinder.patterns.pom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import ingsoftware.gatinder.controller.ViewController;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PATRÓN DE AUTOMATIZACIÓN: PRUEBA BASADA EN PAGE OBJECT MODEL (POM)
 * Objetivo: Ejecutar pruebas de navegación y verificación en Gatinder
 * interactuando exclusivamente a través de los métodos del Page Object.
 */
@SpringBootTest
public class GatinderPomTest {

    @Autowired
    private ViewController viewController;

    private MockMvc mockMvc;
    private PetRegistrationPage page;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(viewController).setViewResolvers(viewResolver).build();
        page = new PetRegistrationPage(mockMvc);
    }

    @Test
    @DisplayName("POM Test: Apertura de Login a traves del Page Object")
    void testPom_Login() throws Exception {
        MvcResult res = page.irALogin();
        assertNotNull(res);
    }

    @Test
    @DisplayName("POM Test: Apertura de Registro con carga de zonas mediante el Page Object")
    void testPom_RegistroConZonas() throws Exception {
        MvcResult res = page.irARegistro();
        assertNotNull(res.getModelAndView().getModel().get("zones"));
    }

    @Test
    @DisplayName("POM Test: Navegacion a Home a traves del Page Object")
    void testPom_Home() throws Exception {
        MvcResult res = page.irAHome();
        assertNotNull(res.getModelAndView());
    }
}
