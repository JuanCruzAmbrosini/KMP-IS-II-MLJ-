// Cliente REST API base URL
const API_BASE = '/cliente/api';

// Cache de datos para selección en relaciones
let cachedLocalidades = [];
let cachedDomicilios = [];
let cachedAutores = [];
let cachedLibros = [];

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
    }, 4000);
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

async function loadAll() {
    updateServerStatus('loading');
    try {
        await Promise.all([
            loadLocalidades(),
            loadDomicilios(),
            loadAutores(),
            loadLibros()
        ]);
        await loadPersonas();
        updateServerStatus(true);
    } catch (e) {
        console.error('Error cargando datos iniciales:', e);
        updateServerStatus(false);
    }
}

// ==========================================
// 1. LOCALIDADES
// ==========================================
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
                <td>${loc.denominacion}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editLocalidad(${loc.id})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteLocalidad(${loc.id})">Eliminar</button>
                </td>
            </tr>
        `).join('');

        populateLocalidadesSelect();
    } catch (e) {
        console.error(e);
        document.getElementById('localidadesTableBody').innerHTML = 
            `<tr><td colspan="3" class="loading">Error al cargar: ${e.message}</td></tr>`;
        throw e;
    }
}

function populateLocalidadesSelect() {
    const select = document.getElementById('domicilioLocalidad');
    if (!select) return;
    select.innerHTML = '<option value="">-- Seleccione Localidad --</option>' +
        cachedLocalidades.map(l => `<option value="${l.id}">${l.denominacion}</option>`).join('');
}

function openLocalidadModal(loc = null) {
    document.getElementById('localidadModalTitle').textContent = loc ? 'Editar Localidad' : 'Nueva Localidad';
    document.getElementById('localidadId').value = loc ? loc.id : '';
    document.getElementById('localidadDenominacion').value = loc ? loc.denominacion : '';
    document.getElementById('localidadModal').classList.remove('hidden');
}

function editLocalidad(id) {
    const loc = cachedLocalidades.find(l => l.id === id);
    if (loc) openLocalidadModal(loc);
}

async function saveLocalidad(e) {
    e.preventDefault();
    const id = document.getElementById('localidadId').value;
    const denominacion = document.getElementById('localidadDenominacion').value.trim();

    const payload = { denominacion };
    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/localidades/${id}` : `${API_BASE}/localidades`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar localidad');
        
        closeModal('localidadModal');
        showAlert(`Localidad ${id ? 'actualizada' : 'creada'} con éxito`);
        await loadLocalidades();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteLocalidad(id) {
    if (!confirm('¿Seguro que deseas eliminar esta localidad?')) return;
    try {
        const res = await fetch(`${API_BASE}/localidades/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar localidad');
        showAlert('Localidad eliminada');
        await loadLocalidades();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 2. DOMICILIOS
// ==========================================
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

        tbody.innerHTML = cachedDomicilios.map(d => {
            const hasCoords = hasValidCoords(d.latitud, d.longitud);
            const mapsUrl = hasCoords
                ? `https://www.google.com/maps?q=${encodeURIComponent(String(d.latitud).trim())},${encodeURIComponent(String(d.longitud).trim())}`
                : '#';
            const coordsContent = hasCoords
                ? `<span style="font-family:monospace;font-size:0.8rem;color:var(--text-secondary)">${d.latitud}, ${d.longitud}</span>
                   <div style="margin-top:4px;">
                       <a href="${mapsUrl}" target="_blank" rel="noopener noreferrer" class="btn-maps" title="Ver en Google Maps">📍 Ver Mapa</a>
                   </div>`
                : '<em style="color:var(--text-secondary);font-size:0.85rem">Sin coordenadas</em>';

            return `
                <tr>
                    <td><strong>#${d.id}</strong></td>
                    <td>${d.calle}</td>
                    <td>${d.numero}</td>
                    <td>${d.localidad ? `<span class="badge badge-primary">${d.localidad.denominacion}</span>` : '<em>Sin localidad</em>'}</td>
                    <td>${coordsContent}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="editDomicilio(${d.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteDomicilio(${d.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');

        populateDomiciliosSelect();
    } catch (e) {
        console.error(e);
        document.getElementById('domiciliosTableBody').innerHTML = 
            `<tr><td colspan="6" class="loading">Error: ${e.message}</td></tr>`;
        throw e;
    }
}

function populateDomiciliosSelect() {
    const select = document.getElementById('personaDomicilio');
    if (!select) return;
    select.innerHTML = '<option value="">-- Sin domicilio --</option>' +
        cachedDomicilios.map(d => `
            <option value="${d.id}">${d.calle} ${d.numero} (${d.localidad ? d.localidad.denominacion : 'Sin loc.'})${hasValidCoords(d.latitud, d.longitud) ? ' 📍' : ''}</option>
        `).join('');
}

function openDomicilioModal(dom = null) {
    document.getElementById('domicilioModalTitle').textContent = dom ? 'Editar Domicilio' : 'Nuevo Domicilio';
    document.getElementById('domicilioId').value = dom ? dom.id : '';
    document.getElementById('domicilioCalle').value = dom ? dom.calle : '';
    document.getElementById('domicilioNumero').value = dom ? dom.numero : '';
    document.getElementById('domicilioLocalidad').value = dom && dom.localidad ? dom.localidad.id : '';
    document.getElementById('domicilioLatitud').value = dom && dom.latitud ? dom.latitud : '';
    document.getElementById('domicilioLongitud').value = dom && dom.longitud ? dom.longitud : '';
    document.getElementById('domicilioModal').classList.remove('hidden');
}

function editDomicilio(id) {
    const d = cachedDomicilios.find(x => x.id === id);
    if (d) openDomicilioModal(d);
}

async function saveDomicilio(e) {
    e.preventDefault();
    const id = document.getElementById('domicilioId').value;
    const calle = document.getElementById('domicilioCalle').value.trim();
    const numero = parseInt(document.getElementById('domicilioNumero').value);
    const localidadId = document.getElementById('domicilioLocalidad').value;
    const latitud = document.getElementById('domicilioLatitud').value.trim();
    const longitud = document.getElementById('domicilioLongitud').value.trim();

    const payload = {
        calle,
        numero,
        latitud: latitud || null,
        longitud: longitud || null,
        localidad: localidadId ? cachedLocalidades.find(l => l.id == localidadId) : null
    };

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
        showAlert(`Domicilio ${id ? 'actualizado' : 'creado'} con éxito`);
        await loadDomicilios();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteDomicilio(id) {
    if (!confirm('¿Seguro que deseas eliminar este domicilio?')) return;
    try {
        const res = await fetch(`${API_BASE}/domicilios/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar domicilio');
        showAlert('Domicilio eliminado');
        await loadDomicilios();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 3. AUTORES
// ==========================================
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
                <td>${a.nombre}</td>
                <td>${a.apellido}</td>
                <td>${a.biografia || '<em>Sin biografía</em>'}</td>
                <td>
                    <button class="btn btn-secondary btn-sm" onclick="editAutor(${a.id})">Editar</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteAutor(${a.id})">Eliminar</button>
                </td>
            </tr>
        `).join('');

        renderAutoresChecklist();
    } catch (e) {
        console.error(e);
        document.getElementById('autoresTableBody').innerHTML = 
            `<tr><td colspan="5" class="loading">Error: ${e.message}</td></tr>`;
        throw e;
    }
}

function renderAutoresChecklist(selectedIds = []) {
    const container = document.getElementById('libroAutoresList');
    if (!container) return;
    if (cachedAutores.length === 0) {
        container.innerHTML = '<span style="color:var(--text-secondary);font-size:0.85rem">No hay autores disponibles. Cree uno primero.</span>';
        return;
    }
    container.innerHTML = cachedAutores.map(a => `
        <label class="check-item">
            <input type="checkbox" value="${a.id}" ${selectedIds.includes(a.id) ? 'checked' : ''}>
            <span>${a.nombre} ${a.apellido}</span>
        </label>
    `).join('');
}

function openAutorModal(autor = null) {
    document.getElementById('autorModalTitle').textContent = autor ? 'Editar Autor' : 'Nuevo Autor';
    document.getElementById('autorId').value = autor ? autor.id : '';
    document.getElementById('autorNombre').value = autor ? autor.nombre : '';
    document.getElementById('autorApellido').value = autor ? autor.apellido : '';
    document.getElementById('autorBiografia').value = autor ? autor.biografia : '';
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

    const payload = { nombre, apellido, biografia };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/autores/${id}` : `${API_BASE}/autores`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar autor');

        closeModal('autorModal');
        showAlert(`Autor ${id ? 'actualizado' : 'creado'} con éxito`);
        await loadAutores();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteAutor(id) {
    if (!confirm('¿Seguro que deseas eliminar este autor?')) return;
    try {
        const res = await fetch(`${API_BASE}/autores/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar autor');
        showAlert('Autor eliminado');
        await loadAutores();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 4. LIBROS
// ==========================================
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

            return `
                <tr>
                    <td><strong>#${lib.id}</strong></td>
                    <td><strong>${lib.titulo}</strong></td>
                    <td>${lib.fecha}</td>
                    <td><span class="badge">${lib.genero}</span></td>
                    <td>${lib.paginas}</td>
                    <td>${lib.autor || '-'}</td>
                    <td>${autoresBadges}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="editLibro(${lib.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteLibro(${lib.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');

        renderPersonasLibrosChecklist();
    } catch (e) {
        console.error(e);
        document.getElementById('librosTableBody').innerHTML = 
            `<tr><td colspan="8" class="loading">Error: ${e.message}</td></tr>`;
        throw e;
    }
}

function openLibroModal(lib = null) {
    document.getElementById('libroModalTitle').textContent = lib ? 'Editar Libro' : 'Nuevo Libro';
    document.getElementById('libroId').value = lib ? lib.id : '';
    document.getElementById('libroTitulo').value = lib ? lib.titulo : '';
    document.getElementById('libroFecha').value = lib ? lib.fecha : new Date().getFullYear();
    document.getElementById('libroGenero').value = lib ? lib.genero : '';
    document.getElementById('libroPaginas').value = lib ? lib.paginas : '';
    document.getElementById('libroAutorTexto').value = lib ? (lib.autor || '') : '';

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

    const checkedBoxes = document.querySelectorAll('#libroAutoresList input[type="checkbox"]:checked');
    const autores = Array.from(checkedBoxes).map(cb => {
        const aId = parseInt(cb.value);
        return cachedAutores.find(a => a.id === aId);
    }).filter(Boolean);

    const payload = { titulo, fecha, genero, paginas, autor, autores };

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
        showAlert(`Libro ${id ? 'actualizado' : 'creado'} con éxito`);
        await loadLibros();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteLibro(id) {
    if (!confirm('¿Seguro que deseas eliminar este libro?')) return;
    try {
        const res = await fetch(`${API_BASE}/libros/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar libro');
        showAlert('Libro eliminado');
        await loadLibros();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 5. PERSONAS
// ==========================================
async function loadPersonas() {
    try {
        const res = await fetch(`${API_BASE}/personas`);
        if (!res.ok) throw new Error('Error al consultar personas');
        const personas = await res.json();

        const tbody = document.getElementById('personasTableBody');
        if (personas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="loading">No hay personas registradas</td></tr>';
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
                ? p.libros.map(lib => `<span class="badge badge-primary">📖 ${lib.titulo}</span>`).join('')
                : '<em>Sin libros</em>';

            return `
                <tr>
                    <td><strong>#${p.id}</strong></td>
                    <td><strong>${p.nombre} ${p.apellido}</strong></td>
                    <td>${p.dni}</td>
                    <td>${domInfo}</td>
                    <td>${librosBadges}</td>
                    <td>
                        <button class="btn btn-secondary btn-sm" onclick="editPersona(${p.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deletePersona(${p.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        console.error(e);
        document.getElementById('personasTableBody').innerHTML = 
            `<tr><td colspan="6" class="loading">Error: ${e.message}</td></tr>`;
        throw e;
    }
}

function renderPersonasLibrosChecklist(selectedLibroIds = []) {
    const container = document.getElementById('personaLibrosList');
    if (!container) return;
    if (cachedLibros.length === 0) {
        container.innerHTML = '<span style="color:var(--text-secondary);font-size:0.85rem">No hay libros registrados aún.</span>';
        return;
    }
    container.innerHTML = cachedLibros.map(lib => `
        <label class="check-item">
            <input type="checkbox" value="${lib.id}" ${selectedLibroIds.includes(lib.id) ? 'checked' : ''}>
            <span>${lib.titulo} (${lib.genero}, ${lib.fecha})</span>
        </label>
    `).join('');
}

async function openPersonaModal(persona = null) {
    document.getElementById('personaModalTitle').textContent = persona ? 'Editar Persona' : 'Nueva Persona';
    document.getElementById('personaId').value = persona ? persona.id : '';
    document.getElementById('personaNombre').value = persona ? persona.nombre : '';
    document.getElementById('personaApellido').value = persona ? persona.apellido : '';
    document.getElementById('personaDni').value = persona ? persona.dni : '';
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
    const domicilioId = document.getElementById('personaDomicilio').value;

    const domicilio = domicilioId ? cachedDomicilios.find(d => d.id == domicilioId) : null;

    const checkedBoxes = document.querySelectorAll('#personaLibrosList input[type="checkbox"]:checked');
    const libros = Array.from(checkedBoxes).map(cb => {
        const libId = parseInt(cb.value);
        return cachedLibros.find(l => l.id === libId);
    }).filter(Boolean);

    const payload = { nombre, apellido, dni, domicilio, libros };

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
        showAlert(`Persona ${id ? 'actualizada' : 'creada'} con éxito`);
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deletePersona(id) {
    if (!confirm('¿Seguro que deseas eliminar esta persona?')) return;
    try {
        const res = await fetch(`${API_BASE}/personas/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar persona');
        showAlert('Persona eliminada');
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// Helpers
function closeModal(id) {
    document.getElementById(id).classList.add('hidden');
}
