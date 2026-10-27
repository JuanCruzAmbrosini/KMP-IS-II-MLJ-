# 📚 Catalog Management API - Spring Boot & CI/CD con GitHub Actions

![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3.3.4-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI%2FCD-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
![H2 Database](https://img.shields.io/badge/Database-H2_In--Memory-007acc?style=for-the-badge)

---

## 📖 Descripción del Proyecto

**Catalog Management API** es una aplicación RESTful desarrollada con **Java 17** y el ecosistema **Spring Boot 3**. Su objetivo principal es ofrecer una solución limpia, escalable y robusta para la gestión del catálogo de libros (*books*) de una biblioteca o librería virtual.

La aplicación implementa una arquitectura en capas siguiendo las mejores prácticas de la industria:
- **Capa de Controladores (`Controller`)**: Expone los endpoints HTTP RESTful, valida la entrada de datos (`@Valid`) y gestiona los códigos de respuesta HTTP adecuados (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found`).
- **Capa de Servicios (`Service`)**: Encapsula la lógica de negocio y las reglas de validación (por ejemplo, unicidad de ISBN y transaccionalidad).
- **Capa de Acceso a Datos (`Repository`)**: Utiliza **Spring Data JPA** para interactuar con la persistencia de forma declarativa.
- **Modelo de Dominio (`Model`)**: Representa la entidad `Book` persistida mediante JPA en una base de datos **H2** en memoria.
- **Manejo Global de Excepciones**: Un controlador asesor (`@RestControllerAdvice`) que captura excepciones de validación y recursos no encontrados, estandarizando las respuestas de error en formato JSON.
- **Pipeline de CI/CD**: Automatización integral mediante **GitHub Actions** para validar código y generar artefactos en cada cambio.

---

## 🛠️ Arquitectura de la Solución

```
Proyecto/
├── .github/
│   └── workflows/
│       └── catalog-management.yml    # Pipeline de CI/CD (GitHub Actions)
├── src/
│   ├── main/
│   │   ├── java/com/example/catalog/
│   │   │   ├── CatalogManagementApplication.java
│   │   │   ├── controller/
│   │   │   │   └── BookController.java
│   │   │   ├── exception/
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   ├── model/
│   │   │   │   └── Book.java
│   │   │   ├── repository/
│   │   │   │   └── BookRepository.java
│   │   │   └── service/
│   │   │       ├── BookService.java
│   │   │       └── impl/
│   │   │           └── BookServiceImpl.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/catalog/
│           ├── CatalogManagementApplicationTests.java
│           ├── controller/
│           │   └── BookControllerTest.java
│           └── service/
│               └── BookServiceTest.java
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🚀 Guía de Uso: Ejecución Local

### Prerrequisitos
- **Java Development Kit (JDK) 17** o superior instalado.
- **Apache Maven 3.8+** instalado y configurado en el `PATH` del sistema.
- **Git** para clonar el repositorio.
- *(Opcional)* Un cliente HTTP como cURL, Postman o Insomnia.

### 1. Clonar el repositorio
```bash
git clone <URL-DEL-REPOSITORIO>
cd Proyecto
```

### 2. Compilar y ejecutar pruebas unitarias
Para validar que todo el código fuente compila y que todas las pruebas automatizadas se ejecutan con éxito:
```bash
mvn test
```

### 3. Empaquetar la aplicación
Genera el paquete ejecutable `.jar` dentro del directorio `target/`:
```bash
mvn clean package
```

### 4. Ejecutar la aplicación
Puedes iniciar la aplicación directamente con Maven:
```bash
mvn spring-boot:run
```

O ejecutando el artefacto `.jar` empaquetado:
```bash
java -jar target/catalog-management-1.0.0.jar
```

La aplicación iniciará en el puerto predeterminado **`8000`**.

### 5. Acceso a la Consola de Base de Datos H2
Durante la ejecución local, la consola web de H2 está habilitada para inspeccionar las tablas y registros:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:catalogdb`
- **User Name**: `sa`
- **Password**: *(vacío)*

---

## 📡 Catálogo de la API REST

Ruta base: `http://localhost:8000/api/books`

| Método | Endpoint | Descripción | Código HTTP Exitoso |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/books` | Lista todos los libros del catálogo | `200 OK` |
| `GET` | `/api/books/{id}` | Obtiene los detalles de un libro específico | `200 OK` |
| `POST` | `/api/books` | Crea y da de alta un nuevo libro en el catálogo | `201 Created` |
| `PUT` | `/api/books/{id}` | Actualiza la información de un libro existente | `200 OK` |
| `DELETE` | `/api/books/{id}` | Elimina un libro del catálogo | `204 No Content` |

### Ejemplos de uso con cURL

#### Crear un libro (`POST`):
```bash
curl -X POST http://localhost:8000/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "978-0132350884",
    "price": 42.50,
    "stock": 15
  }'
```

#### Obtener todos los libros (`GET`):
```bash
curl -X GET http://localhost:8000/api/books
```

#### Actualizar un libro (`PUT`):
```bash
curl -X PUT http://localhost:8000/api/books/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code - Revised Edition",
    "author": "Robert C. Martin",
    "isbn": "978-0132350884",
    "price": 45.00,
    "stock": 20
  }'
```

#### Eliminar un libro (`DELETE`):
```bash
curl -X DELETE http://localhost:8000/api/books/1
```

---

## ⚙️ ¿Cómo funciona nuestro CI/CD?

En la ingeniería de software moderna, la entrega continua y confiable de valor depende de la automatización de procesos críticos mediante prácticas de **DevOps**. Esta sección detalla la arquitectura de integración y entrega continua configurada para este proyecto.

### 1. El papel de GitHub Actions
**GitHub Actions** es una plataforma nativa de automatización integrada directamente en GitHub. Permite definir flujos de trabajo (*workflows*) como código mediante archivos declarativos YAML ubicados en `.github/workflows/`. 

GitHub Actions provee:
- **Event-Driven Triggers**: Reacciona instantáneamente a eventos del ciclo de vida del repositorio (como `push`, `pull_request`, creación de tags o despliegues).
- **Runners Aislados y Efímeros**: Entornos virtuales limpios (en este caso contenedores `ubuntu-latest`) que se aprovisionan bajo demanda para cada ejecución.
- **Ecosistema Modular**: Uso de acciones reutilizables comunitarias y oficiales (como `actions/checkout`, `actions/setup-java`, `actions/upload-artifact`).

### 2. Diferencia conceptual: Continuous Integration (CI) vs. Continuous Deployment (CD)

| Concepto | Definición | Alcance en este Proyecto |
| :--- | :--- | :--- |
| **Continuous Integration (CI)** | Práctica en la cual los desarrolladores integran código frecuentemente en una rama compartida (`main`). Cada integración dispara automáticamente una compilación y la ejecución de la suite de pruebas unitarias e integración para detectar defectos tempranamente (*fail-fast*). | **Implementado**: Job de `test` que descarga el código, monta el JDK 17 y corre `mvn test`. Si una sola prueba falla, el pipeline aborta de inmediato. |
| **Continuous Delivery (CD)** | Extensión de CI donde el software empaquetado y verificado se deja siempre en un estado listo para ser desplegado a producción en cualquier momento, generando los binarios o artefactos auditables. | **Implementado**: Job de `build` que empaqueta la aplicación con `mvn clean package` y almacena el `.jar` resultante como artefacto descargable en GitHub. |
| **Continuous Deployment (CD)** | Fase posterior donde cada cambio que pasa satisfactoriamente las etapas anteriores se despliega **automáticamente** a los entornos de producción o staging sin intervención humana manual. | Es el paso siguiente en una infraestructura empresarial (por ejemplo, desplegando el `.jar` en AWS ECS, Kubernetes o un servidor cloud). |

### 3. Automatización del ciclo de vida del software con `.github/workflows/catalog-management.yml`

El flujo de trabajo automatizado se esquematiza de la siguiente manera:

```mermaid
flowchart TD
    A[Desarrollador realiza Commit] --> B[Push / Pull Request a 'main']
    B --> C[Disparo del Workflow en GitHub Actions]
    
    subgraph Job: test [Job 1: test]
        D[Checkout del Código] --> E[Setup Java 17 Temurin]
        E --> F[mvn test: Pruebas Unitarias & Integración]
    end
    
    C --> Job: test
    
    F -->|¿Pruebas Exitosas?| G{Aprobado}
    G -->|No - Falla| H[Alerta roja: Bloqueo de PR y notificación]
    G -->|Sí| I[Trigger Job build: needs = test]
    
    subgraph Job: build [Job 2: build]
        J[Checkout del Código] --> K[Setup Java 17 Temurin]
        K --> L[mvn clean package: Genera JAR]
        L --> M[upload-artifact: Publica target/*.jar]
    end
    
    I --> Job: build
    M --> N[Artefacto JAR listo para Distribución o Despliegue]
```

#### Análisis detallado del archivo YAML

```yaml
name: Java Spring Boot CI/CD Pipeline

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
```
- **Disparador (`on`)**: Se activa de forma automática ante cualquier `push` directo a la rama principal `main` o cuando se abre/actualiza un `pull_request` con destino a `main`. Esto garantiza que **ningún código roto o sin probar pueda fusionarse en la rama productiva**.

---

```yaml
jobs:
  test:
    name: Unit & Integration Tests
    runs-on: ubuntu-latest

    steps:
      - name: Checkout del repositorio
        uses: actions/checkout@v4

      - name: Configurar Java JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'

      - name: Ejecutar pruebas unitarias
        run: mvn test
```
- **Fase de Integración Continua (`test`)**:
  1. `actions/checkout@v4`: Clona el código fuente en la máquina virtual.
  2. `actions/setup-java@v4`: Descarga e instala JDK 17 (distribución Temurin / Eclipse Adoptium) y habilita la caché de dependencias de Maven para acelerar ejecuciones posteriores.
  3. `mvn test`: Ejecuta todas las pruebas unitarias y de integración (`BookServiceTest`, `BookControllerTest`, `CatalogManagementApplicationTests`). Si alguna prueba falla o lanza una aserción errónea, el job falla y finaliza el workflow inmediatamente.

---

```yaml
  build:
    name: Build & Package JAR
    needs: test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout del repositorio
        uses: actions/checkout@v4

      - name: Configurar Java JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'

      - name: Empaquetar artefacto JAR
        run: mvn clean package

      - name: Publicar artefacto de compilación (JAR)
        uses: actions/upload-artifact@v4
        with:
          name: catalog-management-application
          path: target/*.jar
          retention-days: 5
```
- **Fase de Empaquetado y Entrega (`build`)**:
  1. `needs: test`: **Dependencia estricta**. Este job solo se ejecutará si el job `test` concluyó con estado de éxito (`success`). Si las pruebas fallan, el empaquetado nunca se ejecuta, previniendo la generación de binarios defectuosos.
  2. `mvn clean package`: Limpia el directorio de salida y compila el paquete binario de producción (`catalog-management-1.0.0.jar`) que contiene el servidor Tomcat embebido y todas las dependencias.
  3. `actions/upload-artifact@v4`: Almacena el archivo `.jar` resultante en la infraestructura de GitHub Actions por 5 días, permitiendo que cualquier miembro del equipo u otro pipeline de despliegue lo descargue y utilice sin necesidad de recompilar.

