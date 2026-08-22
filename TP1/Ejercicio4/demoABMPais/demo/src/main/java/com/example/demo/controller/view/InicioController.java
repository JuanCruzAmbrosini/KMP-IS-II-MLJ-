package com.example.demo.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

	/*
	 * @GetMapping: Se utiliza para asignar solicitudes HTTP GET a métodos específicos de un controlador.
	 * En este caso, la ruta raíz ("/") se asigna al método inicio().
	 */
	@GetMapping("/")
	public String inicio() {
		return "view/inicio";
	}
}
