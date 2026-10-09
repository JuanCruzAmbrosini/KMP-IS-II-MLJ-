# INFORME DE INVESTIGACIÓN: APIS EXTERNAS Y API DE WHATSAPP BUSINESS

**Asignatura:** Ingeniería de Software II  
**Trabajo Práctico N° 2:** Ejercicio 1 - Parte e  
**Fecha:** Octubre 2026  

---

## 1. INTRODUCCIÓN Y FUNDAMENTOS DE LAS APIS EXTERNAS

### 1.1 ¿Qué es una API Externa y por qué se utiliza?
Una **API Externa (Application Programming Interface)** es un conjunto de contratos de comunicación, definiciones y protocolos que una organización o proveedor publica en la red para permitir que aplicaciones desarrolladas por terceros consuman sus datos, algoritmos o servicios de infraestructura.

En la arquitectura de software contemporánea, el desarrollo de aplicaciones ya no se concibe como un bloque monolítico autocontenido. Adoptar APIs externas ofrece ventajas de primer nivel:
- **Reducción de Time-to-Market:** Permite incorporar funcionalidades críticas (geolocalización, pasarelas de pago, procesamiento meteorológico, mensajería instantánea, inteligencia artificial) en días o semanas, evitando meses de desarrollo propio.
- **Acceso a Datos Especializados:** Ciertos datos (como pronósticos meteorológicos de modelos numéricos globales o cartografía satelital) requieren infraestructura de satélites, estaciones terrenas y supercómputo que ninguna empresa común puede mantener por sí misma.
- **Mantenimiento y Actualización Delegados:** El proveedor externo se encarga de la escalabilidad, la calibración de los algoritmos y la disponibilidad de los servidores.

### 1.2 El Patrón Backend-For-Frontend (BFF) y el Rol de RestTemplate
Uno de los errores más comunes en proyectos de software novatos es intentar consumir las APIs externas directamente desde el FrontEnd (mediante JavaScript en el navegador). Esto genera severos problemas:
1. **Fuga de Credenciales (API Keys):** Cualquier clave o token incluido en el código JavaScript del cliente queda visible a través de las herramientas de inspección del navegador (`F12`), comprometiendo la cuenta y la facturación del proyecto.
2. **Políticas CORS (Cross-Origin Resource Sharing):** La mayoría de las APIs restringen o bloquean llamadas directas desde navegadores no registrados.
3. **Falta de Abstracción y Acoplamiento Fuerte:** Si la API externa cambia su formato JSON o su URL, todo el frontend se rompe.
4. **Falta de Resiliencia y Cacheo:** El navegador no puede implementar eficientemente mecanismos de reintento con *backoff*, almacenamiento intermedio o conmutación por falla (*fallback*).

**La solución adoptada en este trabajo:**
El **BackEnd en Java (Spring Boot)** actúa como intermediario seguro y de alto rendimiento utilizando la clase **`RestTemplate`**. El backend:
- Almacena las claves secretas y URLs en variables de entorno o `application.properties`.
- Realiza las peticiones HTTP (`GET`, `POST`) hacia los servidores remotos.
- Mapea y filtra los datos crudos a **DTOs tipados** propios del dominio de la aplicación.
- Provee mecanismos de resiliencia frente a tiempos de espera (*timeouts*).
- Expone endpoints limpios y locales (`/api/clima/...`) para que el **FrontEnd (Bootstrap 5)** los visualice de forma desacoplada y elegante.

---

## 2. INVESTIGACIÓN DE PLATAFORMAS DE APIS EXTERNAS

A continuación, se detalla el análisis de las plataformas y servicios solicitados en la consigna:

### 2.1 RapidAPI (https://rapidapi.com/)
**RapidAPI** es el *marketplace* y concentrador de APIs más grande del mundo, con más de 40.000 APIs activas y millones de desarrolladores registrados.

#### Características Principales:
- **Hub Centralizado de Autenticación:** El principal valor de RapidAPI radica en que permite a los desarrolladores utilizar **una única cuenta y una única API Key** (`X-RapidAPI-Key`) para autenticarse y consumir cientos de servicios de diferentes empresas y autores, simplificando enormemente la gestión de secretos.
- **Consola Interactiva de Pruebas:** Permite probar cualquier endpoint en tiempo real desde el navegador, inspeccionando parámetros de consulta, encabezados y respuestas antes de escribir una sola línea de código. Genera además snippets automáticos en Java (OkHttp, Unirest), Python, Node.js y cURL.
- **Gestión Unificada de Facturación:** Si un proyecto utiliza 5 APIs comerciales distintas dentro de RapidAPI, todas se liquidan en una sola factura unificada con monitoreo de cuotas y alertas de consumo.
- **Métricas de Latencia y Disponibilidad:** Cada API listada exhibe métricas públicas de tiempo medio de respuesta (en ms), porcentaje de *uptime* y puntuación de popularidad.

---

### 2.2 OpenWeatherMap (https://openweathermap.org/api)
**OpenWeatherMap** es uno de los servicios comerciales y meteorológicos más populares y extendidos del planeta.

#### Características de su Arquitectura:
- **Mecanismo de Autenticación:** Requiere registrarse para obtener un identificador alfanumérico único denominado `appid`. Este token se transmite obligatoriamente en la query string de cada solicitud HTTP (ej. `?q=Buenos+Aires&appid={API_KEY}&units=metric&lang=es`).
- **Endpoints Clave:**
  - *Current Weather Data (`/weather`):* Devuelve condiciones en tiempo real (temperatura, nubosidad, presión, viento) para más de 200.000 ciudades por nombre, ID o coordenadas.
  - *5 Day / 3 Hour Forecast (`/forecast`):* Pronóstico a 5 días con resolución temporal de intervalos de 3 horas.
  - *One Call API 3.0 (`/onecall`):* Endpoint unificado que devuelve clima actual, pronóstico minuto a minuto para la próxima hora, pronóstico horario para 48 horas y pronóstico diario para 8 días, además de alertas de clima severo y datos históricos de 45 años.
- **Políticas y Límites del Plan Gratuito (Free Tier):**
  - El plan gratuito tradicional ofrece 60 llamadas por minuto y hasta 1.000.000 de llamadas mensuales para la API de clima actual 2.5.
  - Para *One Call API 3.0*, se otorgan 1.000 llamadas gratuitas diarias; superar este umbral requiere ingresar tarjeta de crédito con cobro por llamada excedente.
- **Formato de Respuesta:** JSON estándar con arrays de condiciones climáticas codificadas por identificadores numéricos y códigos de iconos (`01d`, `02n`, etc.).

---

### 2.3 Magic Loops (https://magicloops.dev/es)
**Magic Loops** representa una evolución moderna en el ecosistema de APIs: es una plataforma que permite **crear, automatizar y desplegar micro-APIs personalizadas** mediante una interfaz visual asistida por Inteligencia Artificial y flujos de trabajo (*serverless workflows*).

#### Componentes de un "Loop":
1. **Triggers (Disparadores de Entrada):**
   - *Peticiones HTTP Webhook:* Permite que cualquier sistema externo dispare el flujo enviando un POST o GET.
   - *Cron Schedules:* Ejecución periódica basada en tiempo (ej. todos los días a las 08:00 AM).
   - *Eventos de Correo Electrónico:* Reacción ante la llegada de un email a una dirección dedicada.
2. **Nodos de Procesamiento (Lógica e Inteligencia):**
   - *Nodos de LLM / IA:* Permiten conectar modelos de lenguaje como GPT-4o o Claude para transformar texto, clasificar datos, extraer entidades o resumir información.
   - *Nodos de Código (Python / JavaScript):* Ejecutan scripts ligeros en contenedores efímeros sin configurar servidores ni entornos virtuales.
3. **Nodos de Salida (Acciones y Respuestas):**
   - Retornar una respuesta HTTP JSON personalizada al emisor de la API.
   - Enviar notificaciones a canales como Discord, Slack, Telegram o disparar un webhook hacia un Backend en Spring Boot.

#### Utilidad Arquitectónica:
Permite a un equipo de desarrollo construir "adaptadores inteligentes" entre APIs rígidas o lentas y el Backend principal, agregando capas de enriquecimiento semántico con IA sin sobrecargar el código de la aplicación.

---

### 2.4 Open-Meteo (API Seleccionada para la Solución del Software)
Para la implementación técnica del software desarrollado en este ejercicio, se seleccionó como proveedor primario **Open-Meteo** (https://open-meteo.com/).

#### Ventajas Técnicas:
- **Acceso Abierto y Sin API Key:** No requiere registro de tarjetas de crédito ni claves API para propósitos de desarrollo y prueba, garantizando que el software funcione de manera inmediata en la corrección o defensa del TP sin configuraciones complejas de credenciales.
- **Datos Científicos de Alta Precisión:** Utiliza datos de modelos meteorológicos nacionales oficiales: DWD (Alemania), NOAA (EE.UU.), ECMWF (Centro Europeo) y Météo-France.
- **Estándar WMO (World Meteorological Organization):** Retorna códigos numéricos internacionales oficiales para la clasificación del tiempo (despejado, llovizna, nieve, niebla, tormenta eléctrica).
- **API de Geocodificación Integrada:** Permite resolver nombres de cualquier ciudad del mundo a coordenadas geográficas (latitud y longitud) en español, con resolución de provincia y país.

---

### 2.5 Tabla Comparativa Técnica

| Criterio | RapidAPI | OpenWeatherMap | Magic Loops | Open-Meteo (Implementada) |
| :--- | :--- | :--- | :--- | :--- |
| **Tipo de Servicio** | Marketplace / Hub de APIs | Proveedor Meteorológico | Generador de APIs con IA | Servicio Meteorológico Abierto |
| **Autenticación** | Clave Unificada en Header | Parámetro `appid` en URL | Token de Webhook / API Key | No requiere clave obligatoria |
| **Plan Gratuito** | Varía según cada API | 60 req/min o 1.000 día | Tier gratuito limitado | Hasta 10.000 peticiones diarias |
| **Formato de Salida** | JSON / XML estandarizado | JSON estructurado | JSON dinámico configurable | JSON plano de alto rendimiento |
| **Casos de Uso Típicos**| Integración multicanal | Apps móviles y portales clima | Automatizaciones con LLMs | APIs backend y visualizadores |

---

## 3. INVESTIGACIÓN EXHAUSTIVA DE LA API DE WHATSAPP BUSINESS

### 3.1 Contexto: WhatsApp Business Platform (Cloud API de Meta)
WhatsApp es el canal de mensajería con mayor penetración en América Latina y Europa (>85% de la población activa). Para permitir que los sistemas informáticos corporativos se comuniquen de forma automatizada y legal con sus usuarios, Meta provee la **WhatsApp Business Platform**.

A partir de 2022, Meta introdujo la **Cloud API**, una versión de la API de WhatsApp alojada directamente en la infraestructura en la nube de Meta (mediante la **Graph API**, actualmente v19.0+), reemplazando la antigua arquitectura On-Premises que exigía a las empresas mantener servidores locales Docker con bases de datos MySQL dedicadas.

---

### 3.2 Proceso de Acceso y Habilitación Paso a Paso

Para que una aplicación (como nuestro backend en Spring Boot) pueda enviar mensajes por WhatsApp, el equipo debe seguir este procedimiento:

```
┌─────────────────────────┐     ┌─────────────────────────┐     ┌─────────────────────────┐
│ 1. Meta for Developers  │ ──> │ 2. Business Manager     │ ──> │ 3. Número de Teléfono   │
│ Crear App tipo Business │     │ Crear / Verificar WABA  │     │ Registrar y validar OTP │
└─────────────────────────┘     └─────────────────────────┘     └─────────────────────────┘
                                                                             │
┌─────────────────────────┐     ┌─────────────────────────┐                  │
│ 5. Integración Backend  │ <── │ 4. Generación Tokens    │ <────────────────┘
│ RestTemplate + Webhooks │     │ System User + Permisos  │
└─────────────────────────┘     └─────────────────────────┘
```

1. **Creación de Cuenta de Desarrollador:**
   - Ingresar a [https://developers.facebook.com/](https://developers.facebook.com/) con una cuenta de Meta.
   - Ir a *"Mis Apps"* y seleccionar *"Crear App"*.
   - Seleccionar el caso de uso **"Negocios" (Business)**.
2. **Configuración del Producto WhatsApp:**
   - En el panel de control de la aplicación, añadir el producto **WhatsApp**.
   - Meta genera automáticamente un entorno de prueba gratuito con un número emisor de prueba (`Test Number`), un identificador de número de teléfono (`Phone Number ID`) y un ID de cuenta de WhatsApp Business (`WABA ID`).
3. **Registro de un Número de Teléfono Real:**
   - Para producción, se debe adquirir un número telefónico (fijo o móvil) que **no esté registrado actualmente** en la app WhatsApp Messenger ni WhatsApp Business convencional.
   - En el panel de Meta, se inicia el proceso de verificación ingresando un código OTP de 6 dígitos recibido por SMS o llamada de voz.
4. **Generación de Tokens de Autenticación (Seguridad):**
   - *Token Temporal:* Válido por 24 horas, provisto por Meta exclusivamente para desarrollo.
   - *Token Permanente (Producción):* En el **Meta Business Manager**, se crea un **Usuario del Sistema (System User)** con rol de administrador, se le asocia la WABA y se genera un *Access Token* de duración indefinida con los permisos:
     - `whatsapp_business_messaging` (envío y recepción de mensajes).
     - `whatsapp_business_management` (creación y gestión de plantillas).
5. **Configuración de Webhooks (Recepción de Mensajes y Estados):**
   - Para recibir respuestas de los clientes y estados de entrega (*sent*, *delivered*, *read*), se debe exponer un endpoint HTTPS público en nuestro backend (ej. usando un túnel seguro o servidor con certificado SSL válido).
   - Se valida mediante el protocolo de suscripción de Meta (*Verify Token Challenge*).

---

### 3.3 Reglas de Mensajería: Plantillas vs. Mensajes de Sesión

Meta establece políticas estrictas para prevenir el *spam* en la plataforma, dividiendo la interacción en dos escenarios:

#### A. Mensajes de Plantilla (Template Messages) - Salientes / Iniciados por la Empresa
- **Cuándo se usan:** Cuando la empresa necesita iniciar la conversación con un usuario o cuando han pasado más de 24 horas desde el último mensaje del usuario.
- **Regla:** El mensaje no puede ser texto libre arbitrario; debe ajustarse a una **Plantilla pre-aprobada por Meta** con marcadores de posición dinámicos `{{1}}`, `{{2}}`.
- **Categorías Oficiales:**
  1. **Utility (Utilidad):** Notificaciones transaccionales directas (ej. *"Su reserva del libro 'Cien Años de Soledad' vence mañana"* o *"Su turno médico fue confirmado"*).
  2. **Authentication (Autenticación):** Envío de códigos OTP de un solo uso para inicios de sesión o verificación en dos pasos (2FA).
  3. **Marketing:** Ofertas comerciales, novedades o promociones (tienen el costo por mensaje más elevado).
- **Requisito de Opt-In:** El usuario debe haber dado su consentimiento explícito para ser contactado por WhatsApp.

#### B. Mensajes de Sesión Libre (Customer Care Window de 24 Horas)
- **Cuándo se usan:** Cuando un usuario escribe un mensaje a la línea de WhatsApp de la empresa.
- **Mecánica:** Se abre una **ventana de servicio de 24 horas** a partir del último mensaje del cliente.
- **Ventaja:** Dentro de esta ventana, el backend puede enviar mensajes de texto libre, menús interactivos con botones (`quick_reply`), listas desplegables, imágenes y documentos PDF sin requerir plantillas ni costos adicionales por mensaje enviado.

---

### 3.4 Modelo de Precios y Costos de WhatsApp Business API
Meta implementa el modelo **Conversation-Based Pricing (Precios por Conversación)**:
- Una "conversación" se factura como una sesión única de 24 horas que comienza con el primer mensaje entregado.
- Todos los mensajes adicionales enviados dentro de esa ventana de 24 horas están incluidos sin costo adicional.
- **Capa Gratuita (Free Tier):** Meta otorga a cada cuenta WABA **1.000 conversaciones de servicio (iniciadas por el usuario) gratuitas por mes**.
- Las tarifas varían según el código de país del destinatario y la categoría (Utility es más económica que Marketing; Authentication tiene precio intermedio).

---

### 3.5 Alternativas de Integración: Meta Cloud API vs. BSPs vs. Librerías No Oficiales

| Enfoque | Proveedor / Ejemplo | Ventajas | Desventajas / Riesgos |
| :--- | :--- | :--- | :--- |
| **Meta Cloud API (Oficial Directa)** | Meta Graph API (v19.0) | Costo oficial directo, soporte de Meta, 1.000 chats gratis/mes, alta velocidad. | Requiere que el equipo configure la verificación comercial en Meta. |
| **Business Solution Providers (BSP)** | Twilio, MessageBird, Infobip | SDKs maduros, soporte al cliente 24/7, consolas multicanal (SMS + WhatsApp + Email). | Cobran un margen adicional sobre la tarifa de Meta ($0.005 extra por mensaje). |
| **Librerías No Oficiales** | Baileys, Puppeteer, WPPConnect | Gratuitas, no requieren verificación de Meta, código abierto. | **ALTO RIESGO:** Viola los Términos de Servicio de Meta. El número suele ser **baneado permanentemente** tras pocos días. Frágil ante cambios de la interfaz web. |

> **Conclusión del Equipo:** Para cualquier desarrollo profesional o institucional, la única opción viable y confiable es la **Meta Cloud API Oficial**.

---

### 3.6 UTILIDAD QUE EL EQUIPO PODRÍA DARLE EN PROYECTOS FUTUROS

En el marco de la materia **Ingeniería de Software II**, nuestro equipo ha trabajado en proyectos como el **Sistema de Gestión de Biblioteca** (en las partes anteriores de este TP2) y sistemas de **Turnos y Pagos Médicos**. 

Integrar la API de WhatsApp mediante Spring Boot aportaría un salto cualitativo significativo en los siguientes casos de uso reales:

#### 1. Notificaciones Proactivas de Vencimiento de Préstamos de Libros
- **Problema:** Los usuarios suelen olvidar la fecha límite de devolución de los libros de la biblioteca, generando multas y desorganización del inventario.
- **Solución con WhatsApp:** Un servicio programado (`@Scheduled` en Spring Boot) analiza diariamente los préstamos que vencen en 24 o 48 horas e invoca `RestTemplate` para enviar una plantilla de tipo *Utility*:
  > *"Hola Mateo, te recordamos que tu préstamo del libro 'Clean Code' vence mañana 10/10/2026. Responde 'RENOVAR' para extender el plazo 7 días más o acércate a la biblioteca para la devolución."*

#### 2. Envío Automatizado de Comprobantes de Préstamo o Turnos en PDF
- **Problema:** Enviar comprobantes por correo electrónico tiene baja tasa de apertura (<20%) o los correos caen en la carpeta de *Spam*.
- **Solución con WhatsApp:** Cuando el bibliotecario registra un nuevo préstamo o un paciente reserva un turno médico, el backend genera el PDF (como se implementó en la *Parte d* del TP) y la API de WhatsApp lo envía inmediatamente como mensaje multimedia de tipo `document` al celular del usuario. Tasa de entrega superior al 98%.

#### 3. Autenticación en Dos Pasos (2FA) y Restablecimiento Seguro de Contraseña (OTP)
- **Problema:** El envío de códigos por SMS convencional es costoso y vulnerable a ataques de *SIM swapping*.
- **Solución con WhatsApp:** Mediante plantillas de la categoría *Authentication*, el backend genera un código de 6 dígitos con tiempo de expiración (ej. 5 minutos) y un botón interactivo *"Copiar Código"*, elevando la seguridad en el acceso de usuarios al sistema web de la biblioteca.

#### 4. Chatbot Asistente para Búsqueda y Reserva en el Catálogo
- **Problema:** Los usuarios deben ingresar a la web, iniciar sesión y buscar en formularios para saber si un ejemplar está disponible.
- **Solución con WhatsApp:** A través de los webhooks de Meta, el usuario envía un mensaje de texto *"¿Tienen disponible 'El Aleph'?"*. El backend en Spring Boot procesa el texto, consulta la base de datos `biblioteca.db` y responde en menos de 2 segundos con las copias disponibles y un botón de reserva.

#### 5. Alertas Críticas para Administradores del Sistema
- En caso de que ocurra una excepción no controlada (`500 Internal Server Error`), saturación de la base de datos o caída de una API externa, el interceptor de excepciones de Spring Boot envía automáticamente un mensaje de alerta inmediata al teléfono de guardia del equipo de desarrollo.

---

### 3.7 IMPLEMENTACIÓN TÉCNICA EN JAVA CON RESTTEMPLATE

A continuación se presenta el diseño del código Java implementado en nuestra aplicación para despachar mensajes a la WhatsApp Cloud API:

```java
@Service
public class WhatsAppNotificationService {

    private final RestTemplate restTemplate;
    
    @Value("${whatsapp.cloud-api.phone-number-id}")
    private String phoneNumberId;
    
    @Value("${whatsapp.cloud-api.access-token}")
    private String accessToken;

    public WhatsAppNotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void enviarRecordatorioPrestamo(String telefonoDestino, String nombreUsuario, String tituloLibro, String fechaVencimiento) {
        String url = "https://graph.facebook.com/v19.0/" + phoneNumberId + "/messages";

        // 1. Construcción de los encabezados HTTP con Bearer Token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // 2. Construcción del payload JSON oficial requerido por Meta
        String jsonPayload = String.format("""
            {
              "messaging_product": "whatsapp",
              "recipient_type": "individual",
              "to": "%s",
              "type": "template",
              "template": {
                "name": "aviso_vencimiento_prestamo",
                "language": { "code": "es_AR" },
                "components": [
                  {
                    "type": "body",
                    "parameters": [
                      { "type": "text", "text": "%s" },
                      { "type": "text", "text": "%s" },
                      { "type": "text", "text": "%s" }
                    ]
                  }
                ]
              }
            }
            """, telefonoDestino, nombreUsuario, tituloLibro, fechaVencimiento);

        // 3. Ejecución de la llamada HTTP POST mediante RestTemplate
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Mensaje enviado con éxito: " + response.getBody());
        }
    }
}
```

---

## 4. CONCLUSIONES GENERALES

1. **Eficiencia en la Reutilización:** El consumo de APIs externas mediante arquitecturas desacopladas permite a los equipos de desarrollo enfocarse en las reglas de negocio críticas del dominio propio, apalancándose en la infraestructura global de terceros para tareas de alta complejidad (meteorología, mapas, notificaciones).
2. **Seguridad y Centralización con RestTemplate:** Centralizar el consumo de servicios en la capa de Backend en Java evita exponer tokens sensibles en el navegador, supera las barreras de CORS, permite la transformación tipada a DTOs y dota al sistema de mecanismos de tolerancia a fallos.
3. **Potencial Transformador de WhatsApp:** Integrar la WhatsApp Business Cloud API en proyectos futuros del equipo ofrece una vía de contacto directo con los usuarios con tasas de apertura superiores al 95%, resultando especialmente idónea para recordatorios de vencimiento de libros, alertas meteorológicas, entrega de comprobantes digitales y autenticación en dos pasos.
