package org.example.clima;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EjemploReflection {

    public static void main(String[] args) {
        try {
            // 1. Instanciamos un objeto normalmente
            Persona persona = new Persona("Juan Pérez", 30, "admin1234");
            System.out.println("=== ESTADO INICIAL DEL OBJETO ===");
            persona.saludar();
            System.out.println("Clave secreta inicial: " + persona.getClaveSecreta());
            System.out.println();

            // Obtenemos la referencia a la clase mediante Reflection
            Class<?> clasePersona = persona.getClass();

            // =====================================================================
            // PARTE 1: ACCESO Y MODIFICACIÓN DE ATRIBUTOS PRIVADOS
            // =====================================================================
            System.out.println("=== 1. ACCESO A ATRIBUTOS PRIVADOS ===");

            // Obtenemos el campo privado "claveSecreta" mediante getDeclaredField()
            Field campoClave = clasePersona.getDeclaredField("claveSecreta");

            // Permitimos el acceso al miembro privado anulando el control de acceso de Java
            campoClave.setAccessible(true);

            // Leer el valor del atributo privado en la instancia
            String valorClaveLeido = (String) campoClave.get(persona);
            System.out.println("-> Valor leído de 'claveSecreta': " + valorClaveLeido);

            // Modificar el valor del atributo privado
            campoClave.set(persona, "nuevaClaveSuperSegura999");
            System.out.println("-> Modificamos 'claveSecreta' mediante Reflection.");

            // También podemos modificar un atributo de tipo primitivo (ej: 'edad')
            Field campoEdad = clasePersona.getDeclaredField("edad");
            campoEdad.setAccessible(true);
            campoEdad.setInt(persona, 35);
            System.out.println("-> Modificamos 'edad' a: " + campoEdad.getInt(persona));
            System.out.println();

            // =====================================================================
            // PARTE 2: INVOCACIÓN DE MÉTODOS MEDIANTE REFLECTION
            // =====================================================================
            System.out.println("=== 2. INVOCACIÓN DE MÉTODOS ===");

            // A) Invocar método público sin parámetros ("saludar")
            Method metodoSaludar = clasePersona.getMethod("saludar");
            System.out.print("-> Invocando 'saludar()': ");
            metodoSaludar.invoke(persona);

            // B) Invocar método privado sin parámetros ("mostrarClaveSecreta")
            Method metodoPrivado = clasePersona.getDeclaredMethod("mostrarClaveSecreta");
            // Habilitamos el acceso ya que es privado
            metodoPrivado.setAccessible(true);
            System.out.print("-> Invocando método privado 'mostrarClaveSecreta()': ");
            metodoPrivado.invoke(persona);

            // C) Invocar método privado CON parámetros y retorno ("calcularCodigoSeguridad")
            // Se deben especificar los tipos de los parámetros: (String.class, int.class)
            Method metodoConParametros = clasePersona.getDeclaredMethod(
                    "calcularCodigoSeguridad",
                    String.class,
                    int.class
            );
            metodoConParametros.setAccessible(true);

            // Invocamos pasando la instancia y los argumentos correspondientes
            Object resultado = metodoConParametros.invoke(persona, "TOKEN", 2);
            System.out.println("-> Invocando 'calcularCodigoSeguridad(\"TOKEN\", 2)': " + resultado);
            System.out.println();

            // =====================================================================
            // VERIFICACIÓN FINAL
            // =====================================================================
            System.out.println("=== ESTADO FINAL DEL OBJETO ===");
            System.out.println("Nombre: " + persona.getNombre());
            System.out.println("Edad: " + persona.getEdad());
            System.out.println("Clave Secreta: " + persona.getClaveSecreta());

        } catch (NoSuchFieldException e) {
            System.err.println("El atributo especificado no existe: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.err.println("El método especificado no existe: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("No se tiene permiso para acceder al miembro: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
