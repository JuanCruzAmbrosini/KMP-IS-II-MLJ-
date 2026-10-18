package ingsoftware.gatinder.patterns.pom;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PATRÓN DE DISEÑO: MODELO DE OBJETO DE PÁGINAS (PAGE OBJECT MODEL - POM)
 * Objetivo: Abstraer los elementos de la interfaz de usuario de Gatinder
 * (vistas de registro, login e inicio) en una clase reusable, separando la navegación
 * y verificación visual de la lógica de aserción en los tests.
 */
public class PetRegistrationPage {

    private final MockMvc mockMvc;

    public PetRegistrationPage(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    /**
     * Navega a la vista pública de login.
     */
    public MvcResult irALogin() throws Exception {
        return mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andReturn();
    }

    /**
     * Navega a la vista de registro de nuevo usuario.
     */
    public MvcResult irARegistro() throws Exception {
        return mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("zones"))
                .andReturn();
    }

    /**
     * Navega a la pantalla principal home.
     */
    public MvcResult irAHome() throws Exception {
        return mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andReturn();
    }
}

