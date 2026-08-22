package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * La anotación @SpringBootApplication es una combinación de tres anotaciones:
 * @Configuration: Indica que la clase puede contener definiciones de beans.
 * @EnableAutoConfiguration: Habilita la configuración automática de Spring Boot.
 * @ComponentScan: Permite escanear componentes, configuraciones y servicios en el paquete actual y sus subpaquetes.
 */
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// Inicia la aplicación Spring Boot. Este método es el punto de entrada de la aplicación.
		// SpringApplication.run() arranca la aplicación, crea el contexto de Spring y lanza el servidor web embebido (si es necesario).
		// El método toma como parámetros la clase principal de la aplicación y los argumentos de línea de comandos.
		SpringApplication.run(DemoApplication.class, args);
	}

}
