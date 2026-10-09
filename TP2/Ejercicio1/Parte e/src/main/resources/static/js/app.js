/**
 * ClimaHub - Logica FrontEnd
 * Consume los endpoints REST de Spring Boot para visualizar datos de la API externa
 */

document.addEventListener('DOMContentLoaded', () => {
    // Estado global de la aplicacion
    let currentUnit = 'C'; // 'C' o 'F'
    let currentData = null;
    let popularCitiesData = [];
    let debounceTimer = null;

    // Elementos DOM
    const formBusqueda = document.getElementById('formBusqueda');
    const inputCiudad = document.getElementById('inputCiudad');
    const dropdownSugerencias = document.getElementById('dropdownSugerencias');
    const spinnerBuscar = document.getElementById('spinnerBuscar');
    const loaderClima = document.getElementById('loaderClima');

    const cardCiudad = document.getElementById('cardCiudad');
    const cardPaisBadge = document.getElementById('cardPaisBadge');
    const cardRegion = document.getElementById('cardRegion');
    const cardFechaHora = document.getElementById('cardFechaHora');
    const cardCondicionBadge = document.getElementById('cardCondicionBadge');
    const cardTemp = document.getElementById('cardTemp');
    const cardTempUnit = document.getElementById('cardTempUnit');
    const cardIcono = document.getElementById('cardIcono');
    const cardCondicionTexto = document.getElementById('cardCondicionTexto');
    const cardFuente = document.getElementById('cardFuente');

    const metricSensacion = document.getElementById('metricSensacion');
    const metricHumedad = document.getElementById('metricHumedad');
    const metricViento = document.getElementById('metricViento');
    const metricPresion = document.getElementById('metricPresion');
    const metricDirViento = document.getElementById('metricDirViento');
    const metricProbLluvia = document.getElementById('metricProbLluvia');

    const contenedorPronosticoDias = document.getElementById('contenedorPronosticoDias');
    const listaPopulares = document.getElementById('listaPopulares');
    const btnRecargarPopulares = document.getElementById('btnRecargarPopulares');

    const btnCelsius = document.getElementById('btnCelsius');
    const btnFahrenheit = document.getElementById('btnFahrenheit');

    const apiStatusBadge = document.getElementById('apiStatusBadge');
    const apiStatusText = document.getElementById('apiStatusText');

    // WhatsApp Form
    const formWhatsApp = document.getElementById('formWhatsApp');
    const inputWaTelefono = document.getElementById('inputWaTelefono');
    const inputWaCiudad = document.getElementById('inputWaCiudad');
    const spinnerWa = document.getElementById('spinnerWa');
    const codeWaPayload = document.getElementById('codeWaPayload');
    const waRespuestaContainer = document.getElementById('waRespuestaContainer');

    // =========================================================================
    // INICIALIZACION
    // =========================================================================

    verificarEstadoApi();
    cargarPronostico('Buenos Aires');
    cargarCiudadesPopulares();

    // =========================================================================
    // EVENT LISTENERS
    // =========================================================================

    formBusqueda.addEventListener('submit', (e) => {
        e.preventDefault();
        const query = inputCiudad.value.trim();
        if (query) {
            dropdownSugerencias.style.display = 'none';
            cargarPronostico(query);
        }
    });

    // Autocompletado con debounce
    inputCiudad.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        const query = inputCiudad.value.trim();
        if (query.length < 2) {
            dropdownSugerencias.style.display = 'none';
            dropdownSugerencias.innerHTML = '';
            return;
        }

        debounceTimer = setTimeout(() => {
            buscarSugerencias(query);
        }, 300);
    });

    // Cerrar dropdown al hacer click fuera
    document.addEventListener('click', (e) => {
        if (!formBusqueda.contains(e.target)) {
            dropdownSugerencias.style.display = 'none';
        }
    });

    // City pills rapidos
    document.querySelectorAll('.city-pill').forEach(pill => {
        pill.addEventListener('click', (e) => {
            e.preventDefault();
            document.querySelectorAll('.city-pill').forEach(p => p.classList.remove('active'));
            pill.classList.add('active');
            const ciudad = pill.dataset.ciudad;
            inputCiudad.value = ciudad;
            cargarPronostico(ciudad);
        });
    });

    // Botones de unidad C / F
    btnCelsius.addEventListener('click', () => {
        if (currentUnit !== 'C') {
            currentUnit = 'C';
            btnCelsius.classList.add('active');
            btnFahrenheit.classList.remove('active');
            actualizarUnidadesEnUI();
        }
    });

    btnFahrenheit.addEventListener('click', () => {
        if (currentUnit !== 'F') {
            currentUnit = 'F';
            btnFahrenheit.classList.add('active');
            btnCelsius.classList.remove('active');
            actualizarUnidadesEnUI();
        }
    });

    // Recargar populares
    btnRecargarPopulares.addEventListener('click', () => {
        cargarCiudadesPopulares();
    });

    // Formulario de simulacion WhatsApp Cloud API
    formWhatsApp.addEventListener('submit', (e) => {
        e.preventDefault();
        simularEnvioWhatsApp();
    });

    // =========================================================================
    // CONSUMO DE ENDPOINTS REST (BACKEND SPRING BOOT)
    // =========================================================================

    /**
     * Consulta el endpoint /api/clima/pronostico?ciudad=...
     */
    async function cargarPronostico(ciudad) {
        mostrarLoader(true);
        try {
            const resp = await fetch(`/api/clima/pronostico?ciudad=${encodeURIComponent(ciudad)}`);
            if (!resp.ok) throw new Error(`HTTP error ${resp.status}`);
            const data = await resp.json();
            currentData = data;
            renderizarClimaPrincipal(data);
            renderizarPronosticoExtendido(data.dias);
            actualizarPayloadWhatsApp(data.actual);
        } catch (err) {
            console.error('Error al obtener pronostico:', err);
            mostrarErrorToast(`No se pudo conectar con el servidor: ${err.message}`);
        } finally {
            mostrarLoader(false);
        }
    }

    /**
     * Consulta sugerencias de ciudades /api/clima/buscar?query=...
     */
    async function buscarSugerencias(query) {
        try {
            const resp = await fetch(`/api/clima/buscar?query=${encodeURIComponent(query)}`);
            if (!resp.ok) return;
            const sugerencias = await resp.json();
            renderizarSugerencias(sugerencias);
        } catch (err) {
            console.warn('Error al buscar sugerencias:', err);
        }
    }

    /**
     * Consulta las ciudades populares /api/clima/ciudades/populares
     */
    async function cargarCiudadesPopulares() {
        listaPopulares.innerHTML = `
            <div class="text-center py-4 text-secondary">
                <div class="spinner-border spinner-border-sm me-2"></div> Solicitando datos a API externa...
            </div>`;

        try {
            const resp = await fetch('/api/clima/ciudades/populares');
            if (!resp.ok) throw new Error('Error al cargar populares');
            popularCitiesData = await resp.json();
            renderizarPopulares(popularCitiesData);
        } catch (err) {
            listaPopulares.innerHTML = `
                <div class="alert alert-warning small p-2">
                    <i class="bi bi-exclamation-triangle me-1"></i> No se pudieron cargar las ciudades populares.
                </div>`;
        }
    }

    /**
     * Consulta el estado de salud de la API /api/clima/estado
     */
    async function verificarEstadoApi() {
        try {
            const resp = await fetch('/api/clima/estado');
            if (!resp.ok) throw new Error('API no disponible');
            const data = await resp.json();

            apiStatusText.textContent = `API: ${data.estado} (${data.latenciaMs}ms)`;
            apiStatusBadge.classList.remove('bg-danger-subtle', 'text-danger');
            apiStatusBadge.classList.add('bg-success-subtle', 'text-success');
        } catch (err) {
            apiStatusText.textContent = 'API: Modo Resiliente / Offline';
            apiStatusBadge.classList.remove('bg-success-subtle', 'text-success');
            apiStatusBadge.classList.add('bg-warning-subtle', 'text-warning');
        }
    }

    /**
     * Simula el envio de mensaje a WhatsApp Business Cloud API vía Backend RestTemplate
     */
    async function simularEnvioWhatsApp() {
        const telefono = inputWaTelefono.value.trim();
        const ciudad = currentData?.actual?.ciudad || 'Buenos Aires';
        const temp = formatTemp(currentData?.actual?.temperatura || 20);
        const cond = currentData?.actual?.condicionDescripcion || 'Cielo despejado';

        spinnerWa.classList.remove('d-none');

        try {
            const resp = await fetch('/api/clima/whatsapp/simular', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    telefono: telefono,
                    ciudad: ciudad,
                    temperatura: temp,
                    condicion: cond
                })
            });

            const data = await resp.json();

            // Activar tab de respuesta
            const triggerTab = document.querySelector('button[data-bs-target="#tab-respuesta-meta"]');
            const tabInstance = bootstrap.Tab.getOrCreateInstance(triggerTab);
            tabInstance.show();

            waRespuestaContainer.innerHTML = `
                <div class="card bg-black border-success p-3 rounded-3 mb-2">
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <span class="badge bg-success"><i class="bi bi-check-circle-fill me-1"></i>HTTP 200 - ACCEPTED</span>
                        <small class="text-secondary font-monospace">${data.mensajeId || 'wamid.HBgL...'}</small>
                    </div>
                    <p class="small text-light-emphasis mb-2"><strong>Explicación:</strong> ${data.explicacionTecnica}</p>
                    <div class="p-2 bg-dark rounded-2 small text-secondary font-monospace">
                        <strong>Cuerpo del mensaje emitido:</strong><br>
                        ${data.cuerpoMensaje}
                    </div>
                </div>
                <details class="small">
                    <summary class="text-secondary cursor-pointer">Ver respuesta JSON cruda de Meta Graph API</summary>
                    <pre class="code-terminal mt-2 mb-0"><code>${escapeHtml(data.respuestaRaw)}</code></pre>
                </details>
            `;

        } catch (err) {
            console.error('Error al simular WhatsApp:', err);
            waRespuestaContainer.innerHTML = `
                <div class="alert alert-danger small mb-0">
                    <i class="bi bi-x-circle me-1"></i> Error en la petición: ${err.message}
                </div>`;
        } finally {
            spinnerWa.classList.add('d-none');
        }
    }

    // =========================================================================
    // RENDERIZADO EN EL DOM
    // =========================================================================

    function renderizarClimaPrincipal(data) {
        const act = data.actual;
        cardCiudad.textContent = act.ciudad;
        cardPaisBadge.textContent = act.pais ? act.pais.substring(0, 3).toUpperCase() : 'EXT';
        cardRegion.textContent = (act.region ? act.region + ', ' : '') + act.pais;
        cardFechaHora.textContent = act.fechaHora || 'En tiempo real';
        cardCondicionBadge.textContent = act.condicionDescripcion;
        cardCondicionTexto.textContent = act.condicionDescripcion;

        cardTemp.textContent = formatTempValueOnly(act.temperatura);
        cardTempUnit.textContent = `°${currentUnit}`;

        // Icono dinámico
        cardIcono.className = `bi ${act.condicionIcono || 'bi-sun-fill'} weather-icon-large`;

        // Métricas
        metricSensacion.textContent = formatTempValueOnly(act.sensacionTermica);
        document.querySelectorAll('.unit-text').forEach(el => el.textContent = `°${currentUnit}`);
        metricHumedad.textContent = act.humedad;
        metricViento.textContent = act.velocidadViento;
        metricPresion.textContent = act.presion;
        metricDirViento.textContent = act.direccionViento;

        // Probabilidad de lluvia del primer dia del pronostico
        if (data.dias && data.dias.length > 0) {
            metricProbLluvia.textContent = data.dias[0].probPrecipitacion;
        }

        cardFuente.textContent = act.fuente;
        inputWaCiudad.value = act.ciudad;
    }

    function renderizarPronosticoExtendido(dias) {
        if (!dias || dias.length === 0) {
            contenedorPronosticoDias.innerHTML = '<div class="col-12 text-secondary">Sin pronóstico disponible.</div>';
            return;
        }

        contenedorPronosticoDias.innerHTML = dias.map(dia => `
            <div class="col-6 col-sm-4 col-md-3 col-lg">
                <div class="forecast-card h-100 d-flex flex-column justify-content-between">
                    <div>
                        <div class="forecast-day-name text-truncate">${dia.diaSemana}</div>
                        <small class="text-secondary opacity-75" style="font-size:0.75rem">${dia.fecha}</small>
                    </div>
                    <div class="my-2">
                        <i class="bi ${dia.condicionIcono} forecast-icon"></i>
                        <div class="small text-secondary text-truncate" title="${dia.condicionDescripcion}">
                            ${dia.condicionDescripcion}
                        </div>
                    </div>
                    <div>
                        <div class="d-flex justify-content-center align-items-baseline gap-2 mb-1">
                            <span class="fw-bold text-light">${formatTempValueOnly(dia.tempMax)}°</span>
                            <span class="text-secondary small">${formatTempValueOnly(dia.tempMin)}°</span>
                        </div>
                        <div class="progress bg-dark" style="height: 4px;" title="Probabilidad de lluvia: ${dia.probPrecipitacion}%">
                            <div class="progress-bar bg-info" style="width: ${dia.probPrecipitacion}%"></div>
                        </div>
                        <small class="text-secondary opacity-75 d-block mt-1" style="font-size:0.7rem">
                            <i class="bi bi-droplet-fill text-info me-1"></i>${dia.probPrecipitacion}%
                        </small>
                    </div>
                </div>
            </div>
        `).join('');
    }

    function renderizarPopulares(ciudades) {
        if (!ciudades || ciudades.length === 0) {
            listaPopulares.innerHTML = '<div class="text-secondary small">No hay ciudades disponibles.</div>';
            return;
        }

        listaPopulares.innerHTML = ciudades.map(c => `
            <div class="popular-city-card d-flex align-items-center justify-content-between" data-ciudad="${c.ciudad}">
                <div class="d-flex align-items-center gap-3">
                    <i class="bi ${c.condicionIcono} fs-4"></i>
                    <div>
                        <div class="fw-semibold text-light">${c.ciudad}</div>
                        <small class="text-secondary text-truncate d-block" style="max-width: 150px;">${c.condicionDescripcion}</small>
                    </div>
                </div>
                <div class="text-end">
                    <span class="fw-bold fs-5 text-light">${formatTempValueOnly(c.temperatura)}°${currentUnit}</span>
                    <small class="text-secondary d-block" style="font-size: 0.72rem;">${c.pais || ''}</small>
                </div>
            </div>
        `).join('');

        // Event listener a cada tarjeta popular
        document.querySelectorAll('.popular-city-card').forEach(card => {
            card.addEventListener('click', () => {
                const nombre = card.dataset.ciudad;
                inputCiudad.value = nombre;
                cargarPronostico(nombre);
            });
        });
    }

    function renderizarSugerencias(sugerencias) {
        if (!sugerencias || sugerencias.length === 0) {
            dropdownSugerencias.style.display = 'none';
            return;
        }

        dropdownSugerencias.innerHTML = sugerencias.map(s => `
            <div class="suggestion-item d-flex justify-content-between align-items-center"
                 data-nombre="${s.nombre}" data-lat="${s.latitud}" data-lon="${s.longitud}">
                <div>
                    <i class="bi bi-geo-alt me-1 text-primary"></i>
                    <strong>${s.nombre}</strong>
                    <span class="text-secondary small ms-1">${s.region ? s.region + ', ' : ''}${s.pais}</span>
                </div>
                <span class="badge bg-secondary-subtle text-secondary small">${s.codigoPais || ''}</span>
            </div>
        `).join('');

        dropdownSugerencias.style.display = 'block';

        dropdownSugerencias.querySelectorAll('.suggestion-item').forEach(item => {
            item.addEventListener('click', () => {
                const nombre = item.dataset.nombre;
                inputCiudad.value = nombre;
                dropdownSugerencias.style.display = 'none';
                cargarPronostico(nombre);
            });
        });
    }

    function actualizarUnidadesEnUI() {
        if (currentData) {
            renderizarClimaPrincipal(currentData);
            renderizarPronosticoExtendido(currentData.dias);
        }
        if (popularCitiesData && popularCitiesData.length > 0) {
            renderizarPopulares(popularCitiesData);
        }
    }

    function actualizarPayloadWhatsApp(act) {
        if (!act) return;
        const tempFormateada = formatTemp(act.temperatura);
        codeWaPayload.textContent = JSON.stringify({
            messaging_product: "whatsapp",
            recipient_type: "individual",
            to: inputWaTelefono.value.trim() || "5491144445555",
            type: "template",
            template: {
                name: "alerta_meteorologica_diaria",
                language: { code: "es_AR" },
                components: [
                    {
                        type: "body",
                        parameters: [
                            { type: "text", text: act.ciudad },
                            { type: "text", text: tempFormateada },
                            { type: "text", text: act.condicionDescripcion }
                        ]
                    }
                ]
            }
        }, null, 2);
    }

    // =========================================================================
    // UTILIDADES DE CONVERSION Y UI
    // =========================================================================

    function formatTempValueOnly(tempCelsius) {
        if (tempCelsius == null) return '--';
        if (currentUnit === 'F') {
            return (tempCelsius * 9/5 + 32).toFixed(1);
        }
        return Number(tempCelsius).toFixed(1);
    }

    function formatTemp(tempCelsius) {
        return `${formatTempValueOnly(tempCelsius)}°${currentUnit}`;
    }

    function mostrarLoader(show) {
        if (show) {
            loaderClima.style.display = 'flex';
            spinnerBuscar.classList.remove('d-none');
        } else {
            loaderClima.style.display = 'none';
            spinnerBuscar.classList.add('d-none');
        }
    }

    function mostrarErrorToast(msg) {
        alert(msg);
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    }
});
