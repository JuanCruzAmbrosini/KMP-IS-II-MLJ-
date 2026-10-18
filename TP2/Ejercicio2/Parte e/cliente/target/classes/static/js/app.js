// Cliente REST API base URL
const API_BASE = '/cliente/api';

// Cache de datos para selección en relaciones
let cachedLocalidades = [];
let cachedDomicilios = [];
let cachedAutores = [];
let cachedLibros = [];
let cachedNotificaciones = [];

// Inicialización
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    loadAll();
});

function initTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            tabButtons.forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            
            btn.classList.add('active');
            const targetId = 'tab-' + btn.getAttribute('data-tab');
            const targetContent = document.getElementById(targetId);
            if (targetContent) targetContent.classList.add('active');

            if (btn.getAttribute('data-tab') === 'notificaciones') {
                loadNotificaciones();
            }
            if (btn.getAttribute('data-tab') === 'reportes') {
                loadReportes();
            }
        });
    });
}

function showAlert(message, type = 'success') {
    const alertBox = document.getElementById('alertBox');
    if (!alertBox) return;
    alertBox.textContent = message;
    alertBox.className = `alert-box ${type}`;
    alertBox.classList.remove('hidden');
    setTimeout(() => {
        alertBox.classList.add('hidden');
    }, 4500);
}

function updateServerStatus(status) {
    const statusBadge = document.getElementById('backendStatus');
    if (!statusBadge) return;
    const indicator = statusBadge.querySelector('.indicator');
    const text = statusBadge.querySelector('.status-text');
    if (status === true || status === 'online') {
        indicator.style.backgroundColor = 'var(--success)';
        text.textContent = 'Servidor Conectado';
    } else if (status === 'loading') {
        indicator.style.backgroundColor = '#f59e0b';
        text.textContent = 'Conectando...';
    } else {
        indicator.style.backgroundColor = 'var(--danger)';
        text.textContent = 'Servidor Desconectado';
    }
}

function hasValidCoords(lat, lon) {
    return lat != null && lon != null && String(lat).trim() !== '' && String(lon).trim() !== '';
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('hidden');
}

// Carga global de entidades
async function loadAll() {
    updateServerStatus('loading');
    try {
        await Promise.all([
            loadLocalidades(),
            loadAutores(),
            loadDomicilios(),
            loadLibros()
        ]);
        await loadPersonas();
        await loadNotificaciones();
        await loadReportes();
        updateServerStatus(true);
    } catch (err) {
        console.error('Error cargando datos iniciales:', err);
        updateServerStatus(false);
    }
}

// ====================================================================
// SECCIÓN LOCALIDADES
// ====================================================================
async function loadLocalidades() {
    try {
        const res = await fetch(`${API_BASE}/localidades`);
        if (!res.ok) throw new Error('Error al consultar localidades');
        cachedLocalidades = await res.json();
        
        const tbody = document.getElementById('localidadesTableBody');
        if (cachedLocalidades.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="loading">No hay localidades registradas</td></tr>';
            return;
        }

        tbody.innerHTML = cachedLocalidades.map(loc => `
            <tr>
                <td><strong>#${loc.id}</strong></td>
                <td><strong>${loc.denominacion}</strong></td>
                <td>
                    <div class="action-buttons">
                        <button class="btn btn-secondary btn-sm" onclick="editLocalidad(${loc.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteLocalidad(${loc.id})">Eliminar</button>
                    </div>
                </td>
            </tr>
        `).join('');

        populateLocalidadesSelect();
    } catch (e) {
        document.getElementById('localidadesTableBody').innerHTML = `<tr><td colspan="3" class="loading error-text">${e.message}</td></tr>`;
    }
}

function populateLocalidadesSelect() {
    const select = document.getElementById('domicilioLocalidad');
    if (!select) return;
    const currentVal = select.value;
    select.innerHTML = '<option value="">-- Seleccione Localidad --</option>' +
        cachedLocalidades.map(loc => `<option value="${loc.id}">${loc.denominacion}</option>`).join('');
    if (currentVal) select.value = currentVal;
}

function openLocalidadModal(loc = null) {
    document.getElementById('localidadModalTitle').textContent = loc ? 'Editar Localidad' : 'Nueva Localidad';
    document.getElementById('localidadId').value = loc ? loc.id : '';
    document.getElementById('localidadDenominacion').value = loc ? loc.denominacion : '';
    document.getElementById('localidadModal').classList.remove('hidden');
}

function editLocalidad(id) {
    const loc = cachedLocalidades.find(x => x.id === id);
    if (loc) openLocalidadModal(loc);
}

async function saveLocalidad(e) {
    e.preventDefault();
    const id = document.getElementById('localidadId').value;
    const denominacion = document.getElementById('localidadDenominacion').value.trim();

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/localidades/${id}` : `${API_BASE}/localidades`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ denominacion })
        });
        if (!res.ok) throw new Error('Error al guardar localidad');
        closeModal('localidadModal');
        showAlert(id ? 'Localidad actualizada' : 'Localidad creada con éxito');
        await loadLocalidades();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteLocalidad(id) {
    if (!confirm('¿Seguro que deseas eliminar esta localidad?')) return;
    try {
        const res = await fetch(`${API_BASE}/localidades/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('No se pudo eliminar la localidad');
        showAlert('Localidad eliminada');
        await loadLocalidades();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

// ====================================================================
// SECCIÓN DOMICILIOS (GOOGLE MAPS)
// ====================================================================
async function loadDomicilios() {
    try {
        const res = await fetch(`${API_BASE}/domicilios`);
        if (!res.ok) throw new Error('Error al consultar domicilios');
        cachedDomicilios = await res.json();

        const tbody = document.getElementById('domiciliosTableBody');
        if (cachedDomicilios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="loading">No hay domicilios registrados</td></tr>';
            return;
        }

        tbody.innerHTML = cachedDomicilios.map(dom => {
            const locDenom = dom.localidad ? dom.localidad.denominacion : '<em>Sin asignar</em>';
            let mapsHtml = '<span class="text-muted">Sin coordenadas</span>';
            if (hasValidCoords(dom.latitud, dom.longitud)) {
                const lat = encodeURIComponent(String(dom.latitud).trim());
                const lon = encodeURIComponent(String(dom.longitud).trim());
                const mapsUrl = `https://www.google.com/maps?q=${lat},${lon}`;
                mapsHtml = `
                    <div style="display:flex; align-items:center; gap:0.5rem; flex-wrap:wrap;">
                        <code style="font-size:0.8rem; background:rgba(0,0,0,0.25); padding:2px 6px; border-radius:4px;">${dom.latitud}, ${dom.longitud}</code>
                        <a href="${mapsUrl}" target="_blank" rel="noopener noreferrer" class="btn-maps" title="Abrir ubicación en Google Maps">
                            📍 Google Maps
                        </a>
                    </div>
                `;
            }

            return `
                <tr>
                    <td><strong>#${dom.id}</strong></td>
                    <td>${dom.calle}</td>
                    <td>${dom.numero}</td>
                    <td><span class="badge badge-secondary">${locDenom}</span></td>
                    <td>${mapsHtml}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-secondary btn-sm" onclick="editDomicilio(${dom.id})">Editar</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteDomicilio(${dom.id})">Eliminar</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');

        populateDomiciliosSelect();
    } catch (e) {
        document.getElementById('domiciliosTableBody').innerHTML = `<tr><td colspan="6" class="loading error-text">${e.message}</td></tr>`;
    }
}

function populateDomiciliosSelect() {
    const select = document.getElementById('personaDomicilio');
    if (!select) return;
    const currentVal = select.value;
    select.innerHTML = '<option value="">-- Sin domicilio --</option>' +
        cachedDomicilios.map(d => {
            const loc = d.localidad ? ` (${d.localidad.denominacion})` : '';
            return `<option value="${d.id}">${d.calle} ${d.numero}${loc}</option>`;
        }).join('');
    if (currentVal) select.value = currentVal;
}

function openDomicilioModal(dom = null) {
    document.getElementById('domicilioModalTitle').textContent = dom ? 'Editar Domicilio' : 'Nuevo Domicilio';
    document.getElementById('domicilioId').value = dom ? dom.id : '';
    document.getElementById('domicilioCalle').value = dom ? dom.calle : '';
    document.getElementById('domicilioNumero').value = dom ? dom.numero : '';
    document.getElementById('domicilioLocalidad').value = (dom && dom.localidad) ? dom.localidad.id : '';
    document.getElementById('domicilioLatitud').value = dom && dom.latitud ? dom.latitud : '';
    document.getElementById('domicilioLongitud').value = dom && dom.longitud ? dom.longitud : '';
    document.getElementById('domicilioModal').classList.remove('hidden');
}

function editDomicilio(id) {
    const dom = cachedDomicilios.find(x => x.id === id);
    if (dom) openDomicilioModal(dom);
}

async function saveDomicilio(e) {
    e.preventDefault();
    const id = document.getElementById('domicilioId').value;
    const calle = document.getElementById('domicilioCalle').value.trim();
    const numero = parseInt(document.getElementById('domicilioNumero').value);
    const locId = document.getElementById('domicilioLocalidad').value;
    const latitud = document.getElementById('domicilioLatitud').value.trim();
    const longitud = document.getElementById('domicilioLongitud').value.trim();

    const localidad = locId ? cachedLocalidades.find(l => l.id == locId) : null;
    const payload = { calle, numero, localidad, latitud, longitud };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/domicilios/${id}` : `${API_BASE}/domicilios`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar domicilio');
        closeModal('domicilioModal');
        showAlert(id ? 'Domicilio actualizado' : 'Domicilio guardado con éxito');
        await loadDomicilios();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteDomicilio(id) {
    if (!confirm('¿Seguro que deseas eliminar este domicilio?')) return;
    try {
        const res = await fetch(`${API_BASE}/domicilios/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('No se pudo eliminar el domicilio');
        showAlert('Domicilio eliminado');
        await loadDomicilios();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

// ====================================================================
// SECCIÓN AUTORES
// ====================================================================
async function loadAutores() {
    try {
        const res = await fetch(`${API_BASE}/autores`);
        if (!res.ok) throw new Error('Error al consultar autores');
        cachedAutores = await res.json();

        const tbody = document.getElementById('autoresTableBody');
        if (cachedAutores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="loading">No hay autores registrados</td></tr>';
            return;
        }

        tbody.innerHTML = cachedAutores.map(a => `
            <tr>
                <td><strong>#${a.id}</strong></td>
                <td><strong>${a.nombre}</strong></td>
                <td><strong>${a.apellido}</strong></td>
                <td><small>${a.biografia || '<em>Sin biografía</em>'}</small></td>
                <td>
                    <div class="action-buttons">
                        <button class="btn btn-secondary btn-sm" onclick="editAutor(${a.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteAutor(${a.id})">Eliminar</button>
                    </div>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        document.getElementById('autoresTableBody').innerHTML = `<tr><td colspan="5" class="loading error-text">${e.message}</td></tr>`;
    }
}

function openAutorModal(autor = null) {
    document.getElementById('autorModalTitle').textContent = autor ? 'Editar Autor' : 'Nuevo Autor';
    document.getElementById('autorId').value = autor ? autor.id : '';
    document.getElementById('autorNombre').value = autor ? autor.nombre : '';
    document.getElementById('autorApellido').value = autor ? autor.apellido : '';
    document.getElementById('autorBiografia').value = autor ? (autor.biografia || '') : '';
    document.getElementById('autorModal').classList.remove('hidden');
}

function editAutor(id) {
    const a = cachedAutores.find(x => x.id === id);
    if (a) openAutorModal(a);
}

async function saveAutor(e) {
    e.preventDefault();
    const id = document.getElementById('autorId').value;
    const nombre = document.getElementById('autorNombre').value.trim();
    const apellido = document.getElementById('autorApellido').value.trim();
    const biografia = document.getElementById('autorBiografia').value.trim();

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/autores/${id}` : `${API_BASE}/autores`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, apellido, biografia })
        });
        if (!res.ok) throw new Error('Error al guardar autor');
        closeModal('autorModal');
        showAlert(id ? 'Autor actualizado' : 'Autor guardado con éxito');
        await loadAutores();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteAutor(id) {
    if (!confirm('¿Seguro que deseas eliminar este autor?')) return;
    try {
        const res = await fetch(`${API_BASE}/autores/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('No se pudo eliminar el autor');
        showAlert('Autor eliminado');
        await loadAutores();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

// ====================================================================
// SECCIÓN LIBROS
// ====================================================================
async function loadLibros() {
    try {
        const res = await fetch(`${API_BASE}/libros`);
        if (!res.ok) throw new Error('Error al consultar libros');
        cachedLibros = await res.json();

        const tbody = document.getElementById('librosTableBody');
        if (cachedLibros.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="loading">No hay libros registrados</td></tr>';
            return;
        }

        tbody.innerHTML = cachedLibros.map(lib => {
            const autoresBadges = (lib.autores && lib.autores.length > 0)
                ? lib.autores.map(a => `<span class="badge badge-primary">${a.nombre} ${a.apellido}</span>`).join('')
                : '<em>Sin autores</em>';

            let vencimientoBadge = '<span class="text-muted">Sin préstamo</span>';
            if (lib.fechaVencimientoDevolucion) {
                vencimientoBadge = `<span class="badge badge-secondary" style="background:#0284c7; color:#fff;">📅 ${lib.fechaVencimientoDevolucion}</span>`;
            }

            return `
                <tr>
                    <td><strong>#${lib.id}</strong></td>
                    <td><strong>${lib.titulo}</strong></td>
                    <td>${lib.fecha}</td>
                    <td><span class="badge badge-secondary">${lib.genero}</span></td>
                    <td>${lib.paginas}</td>
                    <td>${vencimientoBadge}</td>
                    <td>${autoresBadges}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-secondary btn-sm" onclick="editLibro(${lib.id})">Editar</button>
                            <button class="btn btn-danger btn-sm" onclick="deleteLibro(${lib.id})">Eliminar</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        document.getElementById('librosTableBody').innerHTML = `<tr><td colspan="8" class="loading error-text">${e.message}</td></tr>`;
    }
}

function renderAutoresChecklist(selectedIds = []) {
    const container = document.getElementById('libroAutoresList');
    if (!container) return;
    if (cachedAutores.length === 0) {
        container.innerHTML = '<span class="text-muted">No hay autores creados para vincular</span>';
        return;
    }
    container.innerHTML = cachedAutores.map(a => {
        const isChecked = selectedIds.includes(a.id) ? 'checked' : '';
        return `
            <div class="checklist-item">
                <input type="checkbox" id="autor_cb_${a.id}" value="${a.id}" ${isChecked}>
                <label for="autor_cb_${a.id}">${a.nombre} ${a.apellido}</label>
            </div>
        `;
    }).join('');
}

function openLibroModal(lib = null) {
    document.getElementById('libroModalTitle').textContent = lib ? 'Editar Libro' : 'Nuevo Libro';
    document.getElementById('libroId').value = lib ? lib.id : '';
    document.getElementById('libroTitulo').value = lib ? lib.titulo : '';
    document.getElementById('libroFecha').value = lib ? lib.fecha : new Date().getFullYear();
    document.getElementById('libroGenero').value = lib ? lib.genero : '';
    document.getElementById('libroPaginas').value = lib ? lib.paginas : '';
    document.getElementById('libroAutorTexto').value = lib ? (lib.autor || '') : '';
    document.getElementById('libroFechaVencimiento').value = (lib && lib.fechaVencimientoDevolucion) ? lib.fechaVencimientoDevolucion : '';

    const selectedAutorIds = lib && lib.autores ? lib.autores.map(a => a.id) : [];
    renderAutoresChecklist(selectedAutorIds);

    document.getElementById('libroModal').classList.remove('hidden');
}

function editLibro(id) {
    const lib = cachedLibros.find(x => x.id === id);
    if (lib) openLibroModal(lib);
}

async function saveLibro(e) {
    e.preventDefault();
    const id = document.getElementById('libroId').value;
    const titulo = document.getElementById('libroTitulo').value.trim();
    const fecha = parseInt(document.getElementById('libroFecha').value);
    const genero = document.getElementById('libroGenero').value.trim();
    const paginas = parseInt(document.getElementById('libroPaginas').value);
    const autor = document.getElementById('libroAutorTexto').value.trim();
    const fechaVencimientoDevolucion = document.getElementById('libroFechaVencimiento').value || null;

    const checkedBoxes = document.querySelectorAll('#libroAutoresList input[type="checkbox"]:checked');
    const autores = Array.from(checkedBoxes).map(cb => {
        const aId = parseInt(cb.value);
        return cachedAutores.find(a => a.id === aId);
    }).filter(Boolean);

    const payload = { titulo, fecha, genero, paginas, autor, fechaVencimientoDevolucion, autores };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/libros/${id}` : `${API_BASE}/libros`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar libro');
        closeModal('libroModal');
        showAlert(id ? 'Libro actualizado' : 'Libro creado con éxito');
        await loadLibros();
        await loadNotificaciones();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteLibro(id) {
    if (!confirm('¿Seguro que deseas eliminar este libro?')) return;
    try {
        const res = await fetch(`${API_BASE}/libros/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('No se pudo eliminar el libro');
        showAlert('Libro eliminado');
        await loadLibros();
        await loadNotificaciones();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

// ====================================================================
// SECCIÓN PERSONAS
// ====================================================================
async function loadPersonas() {
    try {
        const res = await fetch(`${API_BASE}/personas`);
        if (!res.ok) throw new Error('Error al consultar personas');
        const personas = await res.json();

        const tbody = document.getElementById('personasTableBody');
        if (personas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="loading">No hay personas registradas</td></tr>';
            return;
        }

        tbody.innerHTML = personas.map(p => {
            let domInfo = '<em>Sin domicilio</em>';
            if (p.domicilio) {
                const calleNumero = `${p.domicilio.calle || ''} ${p.domicilio.numero || ''}`.trim();
                const loc = p.domicilio.localidad ? ` (${p.domicilio.localidad.denominacion})` : '';
                let mapsBtn = '';
                if (hasValidCoords(p.domicilio.latitud, p.domicilio.longitud)) {
                    const lat = encodeURIComponent(String(p.domicilio.latitud).trim());
                    const lon = encodeURIComponent(String(p.domicilio.longitud).trim());
                    const mapsUrl = `https://www.google.com/maps?q=${lat},${lon}`;
                    mapsBtn = `
                        <div style="margin-top: 6px;">
                            <a href="${mapsUrl}" target="_blank" rel="noopener noreferrer" class="btn-maps" title="Ver ubicación en Google Maps">
                                📍 Google Maps
                            </a>
                        </div>
                    `;
                }
                domInfo = `<div><strong>${calleNumero}</strong>${loc}</div>${mapsBtn}`;
            }

            const librosBadges = (p.libros && p.libros.length > 0)
                ? p.libros.map(lib => {
                    const venc = lib.fechaVencimientoDevolucion ? ` <small>(Vence: ${lib.fechaVencimientoDevolucion})</small>` : '';
                    return `<span class="badge badge-primary">📖 ${lib.titulo}${venc}</span>`;
                }).join('')
                : '<em>Sin libros</em>';

            const emailDisplay = p.email ? `<div style="font-size:0.85rem; color:#38bdf8;">✉️ ${p.email}</div>` : '<div class="text-muted"><small>Sin email</small></div>';
            const cumpleDisplay = p.fechaNacimiento ? `<div style="font-size:0.85rem; color:#f472b6;">🎂 ${p.fechaNacimiento}</div>` : '<div class="text-muted"><small>Sin fecha nac.</small></div>';

            return `
                <tr>
                    <td><strong>#${p.id}</strong></td>
                    <td><strong>${p.nombre} ${p.apellido}</strong></td>
                    <td>${p.dni}</td>
                    <td>${emailDisplay}${cumpleDisplay}</td>
                    <td>${domInfo}</td>
                    <td>${librosBadges}</td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-secondary btn-sm" onclick="editPersona(${p.id})">Editar</button>
                            <button class="btn btn-danger btn-sm" onclick="deletePersona(${p.id})">Eliminar</button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        document.getElementById('personasTableBody').innerHTML = `<tr><td colspan="7" class="loading error-text">${e.message}</td></tr>`;
    }
}

function renderPersonasLibrosChecklist(selectedIds = []) {
    const container = document.getElementById('personaLibrosList');
    if (!container) return;
    if (cachedLibros.length === 0) {
        container.innerHTML = '<span class="text-muted">No hay libros cargados en el catálogo</span>';
        return;
    }
    container.innerHTML = cachedLibros.map(lib => {
        const isChecked = selectedIds.includes(lib.id) ? 'checked' : '';
        const venc = lib.fechaVencimientoDevolucion ? ` [Vence: ${lib.fechaVencimientoDevolucion}]` : '';
        return `
            <div class="checklist-item">
                <input type="checkbox" id="persona_lib_${lib.id}" value="${lib.id}" ${isChecked}>
                <label for="persona_lib_${lib.id}">📖 ${lib.titulo} (${lib.genero})${venc}</label>
            </div>
        `;
    }).join('');
}

async function openPersonaModal(persona = null) {
    document.getElementById('personaModalTitle').textContent = persona ? 'Editar Persona' : 'Nueva Persona';
    document.getElementById('personaId').value = persona ? persona.id : '';
    document.getElementById('personaNombre').value = persona ? persona.nombre : '';
    document.getElementById('personaApellido').value = persona ? persona.apellido : '';
    document.getElementById('personaDni').value = persona ? persona.dni : '';
    document.getElementById('personaEmail').value = persona && persona.email ? persona.email : '';
    document.getElementById('personaFechaNacimiento').value = persona && persona.fechaNacimiento ? persona.fechaNacimiento : '';
    document.getElementById('personaDomicilio').value = (persona && persona.domicilio) ? persona.domicilio.id : '';

    const selectedLibroIds = (persona && persona.libros) ? persona.libros.map(l => l.id) : [];
    renderPersonasLibrosChecklist(selectedLibroIds);

    document.getElementById('personaModal').classList.remove('hidden');
}

async function editPersona(id) {
    try {
        const res = await fetch(`${API_BASE}/personas/${id}`);
        if (!res.ok) throw new Error('No se pudo obtener la persona');
        const p = await res.json();
        openPersonaModal(p);
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

async function savePersona(e) {
    e.preventDefault();
    const id = document.getElementById('personaId').value;
    const nombre = document.getElementById('personaNombre').value.trim();
    const apellido = document.getElementById('personaApellido').value.trim();
    const dni = parseInt(document.getElementById('personaDni').value);
    const email = document.getElementById('personaEmail').value.trim();
    const fechaNacimiento = document.getElementById('personaFechaNacimiento').value || null;
    const domicilioId = document.getElementById('personaDomicilio').value;

    const domicilio = domicilioId ? cachedDomicilios.find(d => d.id == domicilioId) : null;

    const checkedBoxes = document.querySelectorAll('#personaLibrosList input[type="checkbox"]:checked');
    const libros = Array.from(checkedBoxes).map(cb => {
        const libId = parseInt(cb.value);
        return cachedLibros.find(l => l.id === libId);
    }).filter(Boolean);

    const payload = { nombre, apellido, dni, email, fechaNacimiento, domicilio, libros };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/personas/${id}` : `${API_BASE}/personas`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar persona');
        closeModal('personaModal');
        showAlert(id ? 'Persona actualizada' : 'Persona guardada con éxito');
        await loadPersonas();
        await loadNotificaciones();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deletePersona(id) {
    if (!confirm('¿Seguro que deseas eliminar esta persona?')) return;
    try {
        const res = await fetch(`${API_BASE}/personas/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('No se pudo eliminar la persona');
        showAlert('Persona eliminada');
        await loadPersonas();
        await loadNotificaciones();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

async function ejecutarMigracion() {
    const resultado = document.getElementById('migracionResultado');
    resultado.textContent = 'Leyendo migración.txt...';
    try {
        const res = await fetch(`${API_BASE}/migracion`, { method: 'POST' });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'No se pudo ejecutar la migración');

        const errores = data.errores && data.errores.length
            ? `<br><strong>Observaciones:</strong><br>${data.errores.join('<br>')}`
            : '';
        resultado.innerHTML = `<strong>${data.archivo}</strong>: ${data.importados} registros importados, ` +
            `${data.omitidos} omitidos.${errores}`;
        showAlert(`Migración completada: ${data.importados} registros importados`);
        await Promise.all([loadDomicilios(), loadPersonas()]);
    } catch (e) {
        resultado.textContent = e.message;
        showAlert(e.message, 'error');
    }
}

// ====================================================================
// SECCIÓN NOTIFICACIONES & SCHEDULING (EJERCICIO 2.C)
// ====================================================================
async function loadNotificaciones() {
    await Promise.all([
        loadResumenHoy(),
        loadHistorialNotificaciones()
    ]);
}

async function loadResumenHoy() {
    try {
        const res = await fetch(`${API_BASE}/notificaciones/resumen-hoy`);
        if (!res.ok) throw new Error('Error al cargar resumen del día');
        const data = await res.json();

        // Cumpleañeros
        const cumpleContainer = document.getElementById('cumpleanerosResumen');
        if (data.cumpleanerosHoy && data.cumpleanerosHoy.length > 0) {
            cumpleContainer.innerHTML = data.cumpleanerosHoy.map(c => `
                <div class="card-item">
                    <strong>🎉 ${c.nombre}</strong><br>
                    <small>✉️ ${c.email || 'Sin correo'}</small>
                </div>
            `).join('');
        } else {
            cumpleContainer.innerHTML = '<span class="text-muted">Ningún usuario cumple años hoy.</span>';
        }

        // Vencimientos mañana
        const vencContainer = document.getElementById('vencimientosResumen');
        if (data.prestamosPorVencerManana && data.prestamosPorVencerManana.length > 0) {
            vencContainer.innerHTML = data.prestamosPorVencerManana.map(v => `
                <div class="card-item">
                    <strong>📖 ${v.libroTitulo}</strong><br>
                    <small>👤 Prestado a: ${v.personaNombre} (${v.personaEmail})</small><br>
                    <small style="color:#f59e0b;">⏳ Vence mañana: ${v.fechaVencimiento}</small>
                </div>
            `).join('');
        } else {
            vencContainer.innerHTML = '<span class="text-muted">No hay libros que venzan mañana (24h).</span>';
        }

    } catch (e) {
        console.warn('Error cargando resumen del día:', e);
    }
}

async function loadHistorialNotificaciones() {
    try {
        const res = await fetch(`${API_BASE}/notificaciones/historial`);
        if (!res.ok) throw new Error('Error al cargar historial de notificaciones');
        cachedNotificaciones = await res.json();

        const tbody = document.getElementById('notificacionesTableBody');
        if (cachedNotificaciones.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="loading">No hay notificaciones emitidas aún. Dispara una tarea automática con los botones superiores.</td></tr>';
            return;
        }

        tbody.innerHTML = cachedNotificaciones.map(log => {
            const isCumple = log.tipo === 'SALUTACION_CUMPLEANIOS';
            const tipoBadge = isCumple
                ? '<span class="badge-email-type badge-cumple">🎂 Cumpleaños</span>'
                : '<span class="badge-email-type badge-vencimiento">⏰ Vencimiento (24h)</span>';

            const statusClass = log.estado.includes('SMTP') ? 'badge-status-smtp' : 'badge-status-sim';
            const estadoBadge = `<span class="badge-email-type ${statusClass}">${log.estado}</span>`;

            const fechaFormat = log.fechaEnvio ? log.fechaEnvio.replace('T', ' ').substring(0, 19) : '-';

            return `
                <tr>
                    <td><strong>#${log.id}</strong></td>
                    <td>${tipoBadge}</td>
                    <td>
                        <div><strong>${log.nombreDestinatario || 'Usuario'}</strong></div>
                        <small style="color:#38bdf8;">${log.destinatario}</small>
                    </td>
                    <td>${log.asunto}</td>
                    <td><small>${fechaFormat}</small></td>
                    <td>${estadoBadge}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="verEmailModal(${log.id})">
                            👁️ Ver Email HTML
                        </button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        document.getElementById('notificacionesTableBody').innerHTML = `<tr><td colspan="7" class="loading error-text">${e.message}</td></tr>`;
    }
}

async function ejecutarCumpleaniosManual() {
    try {
        showAlert('⏳ Ejecutando verificación de cumpleaños...', 'loading');
        const res = await fetch(`${API_BASE}/notificaciones/ejecutar-cumpleanios`, { method: 'POST' });
        if (!res.ok) throw new Error('Error ejecutando tarea de cumpleaños');
        const resp = await res.json();
        showAlert(`🎉 Tarea ejecutada con éxito. Total correos emitidos: ${resp.totalEnviados}`);
        await loadHistorialNotificaciones();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

async function ejecutarVencimientosManual() {
    try {
        showAlert('⏳ Ejecutando verificación de préstamos por vencer...', 'loading');
        const res = await fetch(`${API_BASE}/notificaciones/ejecutar-vencimientos`, { method: 'POST' });
        if (!res.ok) throw new Error('Error ejecutando tarea de vencimientos');
        const resp = await res.json();
        showAlert(`⏰ Tarea ejecutada con éxito. Total recordatorios emitidos: ${resp.totalEnviados}`);
        await loadHistorialNotificaciones();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

function verEmailModal(logId) {
    const item = cachedNotificaciones.find(n => n.id === logId);
    if (!item) return;

    document.getElementById('emailPreviewModalTitle').textContent = `Vista Previa: ${item.asunto}`;
    document.getElementById('emailMetaInfo').innerHTML = `
        <div><strong>👤 Destinatario:</strong> ${item.nombreDestinatario} &lt;${item.destinatario}&gt;</div>
        <div><strong>📝 Asunto:</strong> ${item.asunto}</div>
        <div><strong>🕒 Fecha de Envío:</strong> ${item.fechaEnvio ? item.fechaEnvio.replace('T', ' ').substring(0, 19) : '-'} | <strong>Estado:</strong> ${item.estado}</div>
    `;

    const iframe = document.getElementById('emailPreviewIframe');
    iframe.srcdoc = item.cuerpoHtml;

    document.getElementById('emailPreviewModal').classList.remove('hidden');
}

// ====================================================================
// SECCIÓN REPORTES (INCISO 2.d: PDF & EXCEL)
// ====================================================================
async function loadReportes() {
    try {
        // 1. Estadísticas / KPIs
        const resStats = await fetch(`${API_BASE}/reportes/estadisticas`);
        if (resStats.ok) {
            const stats = await resStats.json();
            const elTotalP = document.getElementById('kpiTotalPersonas');
            const elAlqP = document.getElementById('kpiPersonasAlquiler');
            const elTotalL = document.getElementById('kpiTotalLibros');
            const elAlqL = document.getElementById('kpiLibrosAlquilados');
            const elDispL = document.getElementById('kpiLibrosDisponibles');

            if (elTotalP) elTotalP.textContent = stats.totalPersonas ?? '-';
            if (elAlqP) elAlqP.textContent = stats.personasConAlquiler ?? '-';
            if (elTotalL) elTotalL.textContent = stats.totalLibros ?? '-';
            if (elAlqL) elAlqL.textContent = stats.librosAlquilados ?? '-';
            if (elDispL) elDispL.textContent = stats.librosDisponibles ?? '-';
        }

        // 2. Previsualización de personas con préstamos (para reporte PDF)
        const resAlq = await fetch(`${API_BASE}/reportes/alquileres/datos`);
        const tbodyAlq = document.getElementById('previewAlquileresTableBody');
        if (resAlq.ok && tbodyAlq) {
            const personas = await resAlq.json();
            if (!personas || personas.length === 0) {
                tbodyAlq.innerHTML = '<tr><td colspan="3" class="loading">No hay personas con alquileres activos</td></tr>';
            } else {
                tbodyAlq.innerHTML = personas.map(p => {
                    const cantLibros = (p.libros && p.libros.length) ? p.libros.length : 0;
                    const titulos = (p.libros && p.libros.length)
                        ? p.libros.map(l => l.titulo).join(', ')
                        : 'Sin libros';
                    return `
                        <tr>
                            <td><strong>${p.apellido}, ${p.nombre}</strong></td>
                            <td>${p.dni}</td>
                            <td><span class="badge badge-primary">${cantLibros} libro(s)</span> <small style="color: var(--text-secondary);">${titulos}</small></td>
                        </tr>
                    `;
                }).join('');
            }
        }

        // 3. Previsualización de libros disponibles (para reporte Excel)
        const resDisp = await fetch(`${API_BASE}/reportes/libros-disponibles/datos`);
        const tbodyDisp = document.getElementById('previewDisponiblesTableBody');
        if (resDisp.ok && tbodyDisp) {
            const libros = await resDisp.json();
            if (!libros || libros.length === 0) {
                tbodyDisp.innerHTML = '<tr><td colspan="4" class="loading">No hay libros disponibles en catálogo</td></tr>';
            } else {
                tbodyDisp.innerHTML = libros.map(l => {
                    const autorNom = (l.autores && l.autores.length)
                        ? l.autores.map(a => a.nombre + ' ' + a.apellido).join(', ')
                        : (l.autor || 'Sin autor');
                    return `
                        <tr>
                            <td><strong>${l.titulo}</strong></td>
                            <td>${autorNom}</td>
                            <td><span class="badge">${l.genero || '-'}</span></td>
                            <td>${l.paginas || '-'}</td>
                        </tr>
                    `;
                }).join('');
            }
        }
    } catch (e) {
        console.error('Error cargando reportes:', e);
    }
}

function descargarAlquileresPdf() {
    showAlert('📄 Generando y descargando reporte PDF de personas con alquileres...', 'success');
    const url = `${API_BASE}/reportes/alquileres/pdf`;
    const link = document.createElement('a');
    link.href = url;
    link.download = 'reporte_personas_alquileres.pdf';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

function verAlquileresPdfNuevaVentana() {
    showAlert('👁️ Abriendo reporte PDF en una nueva ventana...', 'success');
    window.open(`${API_BASE}/reportes/alquileres/pdf`, '_blank');
}

function descargarLibrosExcel() {
    showAlert('📊 Generando y descargando reporte Excel de libros disponibles (.xlsx)...', 'success');
    const url = `${API_BASE}/reportes/libros-disponibles/excel`;
    const link = document.createElement('a');
    link.href = url;
    link.download = 'libros_disponibles.xlsx';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
