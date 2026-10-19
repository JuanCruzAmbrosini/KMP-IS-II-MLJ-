package org.example.clima;

public class Persona {
    // Atributos privados
    private String nombre;
    private int edad;
    private String claveSecreta;

    // Constructor público
    public Persona(String nombre, int edad, String claveSecreta) {
        this.nombre = nombre;
        this.edad = edad;
        this.claveSecreta = claveSecreta;
    }

    // Método público
    public void saludar() {
        System.out.println("Hola, soy " + nombre + " y tengo " + edad + " años.");
    }

    // Método privado sin parámetros
    private void mostrarClaveSecreta() {
        System.out.println("[Método privado] Clave secreta actual: " + claveSecreta);
    }

    // Método privado con parámetros y retorno
    private String calcularCodigoSeguridad(String prefijo, int factor) {
        return prefijo + "-" + (edad * factor) + "-" + claveSecreta.toUpperCase();
    }

    // Getters para verificar el estado de los atributos
    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getClaveSecreta() {
        return claveSecreta;
    }
}
