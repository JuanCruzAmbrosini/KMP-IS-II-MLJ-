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
// SECCIÓN DOMICILIOS
// ====================================================================
async function loadDomicilios() {
    try {
        const res = await fetch(`${API_BASE}/domicilios`);
        if (!res.ok) throw new Error('Error al consultar domicilios');
        cachedDomicilios = await res.json();

        const tbody = document.getElementById('domiciliosTableBody');
        if (cachedDomicilios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="loading">No hay domicilios registrados</td></tr>';
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
                    <td>${dom.calle} ${dom.numero}</td>
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
        document.getElementById('domiciliosTableBody').innerHTML = `<tr><td colspan="5" class="loading error-text">${e.message}</td></tr>`;
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

    const localidad = locId ? cachedLocalidades.find(l => l.id === parseInt(locId)) : null;

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/domicilios/${id}` : `${API_BASE}/domicilios`;
        const res = await fetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ calle, numero, latitud, longitud, localidad })
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
// SECCIÓN AUTORES (PATRÓN CREACIONAL PROTOTYPE)
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
                        <button class="btn btn-success btn-sm" onclick="openCloneModal(${a.id})" title="Clonar autor usando Patrón Prototype">
                            🧬 Clonar
                        </button>
                        <button class="btn btn-secondary btn-sm" onclick="editAutor(${a.id})">Editar</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteAutor(${a.id})">Eliminar</button>
                    </div>
                </td>
            </tr>
        `).join('');

        populateCloneAuthorSelect();
    } catch (e) {
        document.getElementById('autoresTableBody').innerHTML = `<tr><td colspan="5" class="loading error-text">${e.message}</td></tr>`;
    }
}

function populateCloneAuthorSelect() {
    const select = document.getElementById('cloneAutorSelect');
    if (!select) return;
    select.innerHTML = '<option value="">-- Seleccionar autor a clonar --</option>' +
        cachedAutores.map(a => `<option value="${a.id}">${a.nombre} ${a.apellido} (#${a.id})</option>`).join('');
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

// Patrón Prototype: Clonar autor desde Modal
function openCloneModal(autorId) {
    const autor = cachedAutores.find(a => a.id === autorId);
    if (!autor) return;

    document.getElementById('modalCloneAutorId').value = autor.id;
    document.getElementById('modalCloneAutorInfo').textContent = `Prototipo original: #${autor.id} - ${autor.nombre} ${autor.apellido}`;
    document.getElementById('modalCloneNombre').value = autor.nombre;
    document.getElementById('modalCloneApellido').value = `${autor.apellido} (Clon)`;
    document.getElementById('modalCloneBiografia').value = autor.biografia ? `${autor.biografia} [Copia Prototipo]` : '';
    document.getElementById('clonarAutorModal').classList.remove('hidden');
}

async function confirmarClonacionModal(e) {
    e.preventDefault();
    const autorId = document.getElementById('modalCloneAutorId').value;
    const nombre = document.getElementById('modalCloneNombre').value.trim();
    const apellido = document.getElementById('modalCloneApellido').value.trim();
    const biografia = document.getElementById('modalCloneBiografia').value.trim();

    try {
        const res = await fetch(`${API_BASE}/autores/${autorId}/clonar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nombre, apellido, biografia })
        });
        if (!res.ok) throw new Error('Error al clonar el autor con Prototype');
        const clon = await res.json();
        closeModal('clonarAutorModal');
        showAlert(`🧬 ¡Autor clonado exitosamente! ID del nuevo clon: #${clon.id} (${clon.nombre} ${clon.apellido})`);
        await loadAutores();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// Patrón Prototype: Clonar desde la tarjeta de patrones
async function ejecutarClonacionAutor(e) {
    e.preventDefault();
    const autorId = document.getElementById('cloneAutorSelect').value;
    if (!autorId) {
        showAlert('Selecciona un autor para clonar', 'error');
        return;
    }

    const nombre = document.getElementById('cloneNombre').value.trim();
    const apellido = document.getElementById('cloneApellido').value.trim();
    const biografia = document.getElementById('cloneBiografia').value.trim();

    const payload = {};
    if (nombre) payload.nombre = nombre;
    if (apellido) payload.apellido = apellido;
    if (biografia) payload.biografia = biografia;

    try {
        const res = await fetch(`${API_BASE}/autores/${autorId}/clonar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Fallo al clonar autor');
        const clon = await res.json();
        showAlert(`🧬 Patrón Prototype: Autor clonado como #${clon.id} - ${clon.nombre} ${clon.apellido}`);
        await loadAutores();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ====================================================================
// SECCIÓN LIBROS (BUILDER, FACTORY Y ADAPTER)
// ====================================================================
async function loadLibros() {
    try {
        const res = await fetch(`${API_BASE}/libros`);
        if (!res.ok) throw new Error('Error al consultar libros');
        cachedLibros = await res.json();

        const tbody = document.getElementById('librosTableBody');
        if (cachedLibros.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="loading">No hay libros registrados</td></tr>';
            return;
        }

        tbody.innerHTML = cachedLibros.map(lib => {
            const autoresBadges = (lib.autores && lib.autores.length > 0)
                ? lib.autores.map(a => `<span class="badge badge-primary">${a.nombre} ${a.apellido}</span>`).join('')
                : '<em>Sin autores</em>';

            const esFisico = (lib.tipoLibro === 'FISICO');
            const tipoBadge = esFisico
                ? '<span class="type-pill fisico">📘 Físico</span>'
                : '<span class="type-pill digital">💻 Digital</span>';

            let detallesHtml = '';
            if (esFisico) {
                detallesHtml = `<small>Estantería: <strong>${lib.numeroEstanteria || '-'}</strong><br>Ubicación: ${lib.ubicacion || '-'}</small>`;
            } else {
                const linkDescarga = lib.urlDescarga ? `<a href="${lib.urlDescarga}" target="_blank" style="color:#38bdf8;">Descargar</a>` : '';
                detallesHtml = `<small>Formato: <strong>${lib.formato || 'PDF'}</strong> (${lib.tamanioMB || '-'} MB)<br>${linkDescarga}</small>`;
            }

            return `
                <tr>
                    <td><strong>#${lib.id}</strong></td>
                    <td>${tipoBadge}</td>
                    <td><strong>${lib.titulo}</strong></td>
                    <td>${lib.fecha}</td>
                    <td><span class="badge badge-secondary">${lib.genero}</span></td>
                    <td>${lib.paginas} págs</td>
                    <td>${detallesHtml}</td>
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
        document.getElementById('librosTableBody').innerHTML = `<tr><td colspan="9" class="loading error-text">${e.message}</td></tr>`;
    }
}

function toggleLibroModalTypeFields() {
    const tipo = document.getElementById('libroTipo').value;
    const fisicoFields = document.getElementById('modalFisicoFields');
    const digitalFields = document.getElementById('modalDigitalFields');

    if (tipo === 'DIGITAL') {
        fisicoFields.classList.add('hidden');
        digitalFields.classList.remove('hidden');
    } else {
        fisicoFields.classList.remove('hidden');
        digitalFields.classList.add('hidden');
    }
}

function toggleQuickBookFields() {
    const tipo = document.getElementById('qbTipoLibro').value;
    const fisicoFields = document.getElementById('qbFisicoFields');
    const digitalFields = document.getElementById('qbDigitalFields');

    if (tipo === 'DIGITAL') {
        fisicoFields.classList.add('hidden');
        digitalFields.classList.remove('hidden');
    } else {
        fisicoFields.classList.remove('hidden');
        digitalFields.classList.add('hidden');
    }
}

// Fabricar libro desde la tarjeta de patrones (Factory + Builder + Adapter)
async function crearLibroConFabrica(e) {
    e.preventDefault();
    const tipoLibro = document.getElementById('qbTipoLibro').value;
    const titulo = document.getElementById('qbTitulo').value.trim();
    const fecha = parseInt(document.getElementById('qbFecha').value);
    const genero = document.getElementById('qbGenero').value.trim();
    const paginas = parseInt(document.getElementById('qbPaginas').value);

    const payload = {
        tipoLibro,
        titulo,
        fecha,
        genero,
        paginas,
        autor: 'Colección General'
    };

    if (tipoLibro === 'FISICO') {
        payload.numeroEstanteria = document.getElementById('qbEstanteria').value.trim();
        payload.ubicacion = document.getElementById('qbUbicacion').value.trim();
    } else {
        payload.formato = document.getElementById('qbFormato').value;
        payload.tamanioMB = parseFloat(document.getElementById('qbTamanioMB').value);
        payload.urlDescarga = document.getElementById('qbUrlDescarga').value.trim();
    }

    try {
        const res = await fetch(`${API_BASE}/libros`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al fabricar libro con Builder/Factory');
        const creado = await res.json();
        showAlert(`🚀 Patrón Fábrica & Builder: ¡Libro ${creado.tipoLibro} fabricado exitosamente! ID #${creado.id}`);
        document.getElementById('quickBookForm').reset();
        await loadLibros();
    } catch (err) {
        showAlert(err.message, 'error');
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
    document.getElementById('libroModalTitle').textContent = lib ? 'Editar Libro' : 'Nuevo Libro (Fábrica & Builder)';
    document.getElementById('libroId').value = lib ? lib.id : '';
    document.getElementById('libroTipo').value = lib && lib.tipoLibro ? lib.tipoLibro : 'FISICO';
    document.getElementById('libroTitulo').value = lib ? lib.titulo : '';
    document.getElementById('libroFecha').value = lib ? lib.fecha : new Date().getFullYear();
    document.getElementById('libroGenero').value = lib ? lib.genero : '';
    document.getElementById('libroPaginas').value = lib ? lib.paginas : '';
    document.getElementById('libroAutorTexto').value = lib ? (lib.autor || '') : '';
    document.getElementById('libroFechaVencimiento').value = (lib && lib.fechaVencimientoDevolucion) ? lib.fechaVencimientoDevolucion : '';

    document.getElementById('libroNumeroEstanteria').value = (lib && lib.numeroEstanteria) ? lib.numeroEstanteria : '';
    document.getElementById('libroUbicacion').value = (lib && lib.ubicacion) ? lib.ubicacion : '';
    document.getElementById('libroFormato').value = (lib && lib.formato) ? lib.formato : 'PDF';
    document.getElementById('libroTamanioMB').value = (lib && lib.tamanioMB) ? lib.tamanioMB : '';
    document.getElementById('libroUrlDescarga').value = (lib && lib.urlDescarga) ? lib.urlDescarga : '';

    toggleLibroModalTypeFields();

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
    const tipoLibro = document.getElementById('libroTipo').value;
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

    const payload = {
        tipoLibro,
        titulo,
        fecha,
        genero,
        paginas,
        autor,
        fechaVencimientoDevolucion,
        autores
    };

    if (tipoLibro === 'FISICO') {
        payload.numeroEstanteria = document.getElementById('libroNumeroEstanteria').value.trim();
        payload.ubicacion = document.getElementById('libroUbicacion').value.trim();
    } else {
        payload.formato = document.getElementById('libroFormato').value;
        payload.tamanioMB = parseFloat(document.getElementById('libroTamanioMB').value) || null;
        payload.urlDescarga = document.getElementById('libroUrlDescarga').value.trim();
    }

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
        showAlert(id ? 'Libro actualizado' : 'Libro creado y adaptado vía DTO');
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
// SECCIÓN PATRÓN STRATEGY (BÚSQUEDA)
// ====================================================================
async function ejecutarBusquedaStrategy() {
    const tipo = document.getElementById('strategyTipo').value;
    const criterio = document.getElementById('strategyCriterio').value.trim();

    try {
        const res = await fetch(`${API_BASE}/libros/buscar?tipo=${encodeURIComponent(tipo)}&criterio=${encodeURIComponent(criterio)}`);
        if (!res.ok) throw new Error('Error ejecutando estrategia de búsqueda');
        const libros = await res.json();

        const box = document.getElementById('strategyResults');
        const countSpan = document.getElementById('strategyResultsCount');
        const list = document.getElementById('strategyResultsList');

        countSpan.textContent = libros.length;
        box.classList.remove('hidden');

        if (libros.length === 0) {
            list.innerHTML = '<li class="text-muted">No se encontraron libros para la estrategia seleccionada.</li>';
            return;
        }

        list.innerHTML = libros.map(l => {
            const pill = l.tipoLibro === 'FISICO' ? '<span class="type-pill fisico">Físico</span>' : '<span class="type-pill digital">Digital</span>';
            const extra = l.tipoLibro === 'FISICO' ? `(${l.numeroEstanteria || 'Sin estantería'})` : `(${l.formato || 'PDF'})`;
            return `
                <li>
                    <div>
                        <strong>${l.titulo}</strong> (${l.genero}) - ${l.autor || 'Sin autor principal'}
                        <small style="color:var(--text-muted); margin-left:0.5rem;">${extra}</small>
                    </div>
                    ${pill}
                </li>
            `;
        }).join('');
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

// ====================================================================
// SECCIÓN PATRÓN ITERATOR (FILTRADO POR AUTOR)
// ====================================================================
async function ejecutarIteradorAutor() {
    const autor = document.getElementById('iteratorAutorInput').value.trim();
    if (!autor) {
        showAlert('Ingresa el nombre o apellido del autor para iterar', 'error');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/libros/filtrar-por-autor?autor=${encodeURIComponent(autor)}`);
        if (!res.ok) throw new Error('Error al filtrar con el Iterador');
        const libros = await res.json();

        const box = document.getElementById('iteratorResults');
        const countSpan = document.getElementById('iteratorResultsCount');
        const list = document.getElementById('iteratorResultsList');

        countSpan.textContent = libros.length;
        box.classList.remove('hidden');

        if (libros.length === 0) {
            list.innerHTML = `<li class="text-muted">El iterador recorrió toda la colección y no halló libros de "${autor}".</li>`;
            return;
        }

        list.innerHTML = libros.map(l => {
            const pill = l.tipoLibro === 'FISICO' ? '<span class="type-pill fisico">Físico</span>' : '<span class="type-pill digital">Digital</span>';
            return `
                <li>
                    <div>
                        <strong>${l.titulo}</strong> [${l.fecha}] - ${l.genero}
                        <br><small style="color:#93c5fd;">Autor identificado: ${l.autor || autor}</small>
                    </div>
                    ${pill}
                </li>
            `;
        }).join('');
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

            let cumpleBadge = '';
            if (p.fechaNacimiento) {
                const [y, m, d] = p.fechaNacimiento.split('-');
                const hoy = new Date();
                const esHoy = (hoy.getMonth() + 1 === parseInt(m)) && (hoy.getDate() === parseInt(d));
                cumpleBadge = esHoy
                    ? `<span class="badge badge-success" style="background:#16a34a; color:#fff; font-weight:700;">🎂 ¡Cumple Hoy! (${d}/${m})</span>`
                    : `<span class="text-muted" style="font-size:0.8rem;">Cumple: ${d}/${m}/${y}</span>`;
            }

            const librosList = (p.libros && p.libros.length > 0)
                ? p.libros.map(lib => `<span class="badge badge-secondary" style="margin-bottom:2px;">📖 ${lib.titulo}</span>`).join('')
                : '<em>Sin libros</em>';

            return `
                <tr>
                    <td><strong>#${p.id}</strong></td>
                    <td><strong>${p.nombre} ${p.apellido}</strong></td>
                    <td>${p.dni}</td>
                    <td>
                        <div><a href="mailto:${p.email}" style="color:#38bdf8;">${p.email}</a></div>
                        <div style="margin-top:4px;">${cumpleBadge}</div>
                    </td>
                    <td>${domInfo}</td>
                    <td><div style="display:flex; flex-direction:column; gap:2px;">${librosList}</div></td>
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
        container.innerHTML = '<span class="text-muted">No hay libros cargados para asignar</span>';
        return;
    }
    container.innerHTML = cachedLibros.map(l => {
        const isChecked = selectedIds.includes(l.id) ? 'checked' : '';
        const tipoPill = l.tipoLibro === 'FISICO' ? '📘' : '💻';
        return `
            <div class="checklist-item">
                <input type="checkbox" id="plibro_cb_${l.id}" value="${l.id}" ${isChecked}>
                <label for="plibro_cb_${l.id}">${tipoPill} ${l.titulo} (${l.genero})</label>
            </div>
        `;
    }).join('');
}

function openPersonaModal(p = null) {
    document.getElementById('personaModalTitle').textContent = p ? 'Editar Persona' : 'Nueva Persona';
    document.getElementById('personaId').value = p ? p.id : '';
    document.getElementById('personaNombre').value = p ? p.nombre : '';
    document.getElementById('personaApellido').value = p ? p.apellido : '';
    document.getElementById('personaDni').value = p ? p.dni : '';
    document.getElementById('personaEmail').value = p ? p.email : '';
    document.getElementById('personaFechaNacimiento').value = p ? (p.fechaNacimiento || '') : '';
    document.getElementById('personaDomicilio').value = (p && p.domicilio) ? p.domicilio.id : '';

    const selectedLibroIds = (p && p.libros) ? p.libros.map(l => l.id) : [];
    renderPersonasLibrosChecklist(selectedLibroIds);

    document.getElementById('personaModal').classList.remove('hidden');
}

function editPersona(id) {
    fetch(`${API_BASE}/personas/${id}`)
        .then(r => r.json())
        .then(p => openPersonaModal(p))
        .catch(e => showAlert(e.message, 'error'));
}

async function savePersona(e) {
    e.preventDefault();
    const id = document.getElementById('personaId').value;
    const nombre = document.getElementById('personaNombre').value.trim();
    const apellido = document.getElementById('personaApellido').value.trim();
    const dni = parseInt(document.getElementById('personaDni').value);
    const email = document.getElementById('personaEmail').value.trim();
    const fechaNacimiento = document.getElementById('personaFechaNacimiento').value || null;
    const domId = document.getElementById('personaDomicilio').value;

    const domicilio = domId ? cachedDomicilios.find(d => d.id === parseInt(domId)) : null;

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
        if (!res.ok) return;
        const data = await res.json();
        document.getElementById('cardPrestamosVencenHoy').textContent = data.prestamosPorVencerManiana ?? 0;
        document.getElementById('cardCumpleaniosHoy').textContent = data.cumpleaniosHoy ?? 0;
        document.getElementById('cardTotalEnviados').textContent = data.totalHistoricoEnviados ?? 0;
    } catch (e) {
        console.error('Error cargando resumen del día:', e);
    }
}

async function loadHistorialNotificaciones() {
    try {
        const res = await fetch(`${API_BASE}/notificaciones/historial`);
        if (!res.ok) throw new Error('Error al consultar historial de emails');
        cachedNotificaciones = await res.json();

        const tbody = document.getElementById('historialTableBody');
        if (cachedNotificaciones.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="loading">No hay notificaciones emitidas aún. Dispara una tarea automática o espera al cron.</td></tr>';
            return;
        }

        tbody.innerHTML = cachedNotificaciones.map(log => {
            const isCumple = log.tipo === 'SALUTACION_CUMPLEANIOS';
            const tipoBadge = isCumple
                ? '<span class="badge-email-type badge-cumple">🎂 Cumpleaños</span>'
                : '<span class="badge-email-type badge-vencimiento">⏰ Vencimiento (24h)</span>';

            const statusClass = log.estado && log.estado.includes('SMTP') ? 'badge-status-smtp' : 'badge-status-sim';
            const estadoBadge = `<span class="badge-email-type ${statusClass}">${log.estado}</span>`;
            const fechaFormat = log.fechaEnvio ? log.fechaEnvio.replace('T', ' ').substring(0, 19) : '-';

            return `
                <tr>
                    <td><strong>#${log.id}</strong></td>
                    <td><small>${fechaFormat}</small></td>
                    <td>
                        <div><strong>${log.nombreDestinatario || 'Usuario'}</strong></div>
                        <small style="color:#38bdf8;">${log.destinatario}</small>
                    </td>
                    <td>${tipoBadge}</td>
                    <td>${log.asunto}</td>
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
        document.getElementById('historialTableBody').innerHTML = `<tr><td colspan="7" class="loading error-text">${e.message}</td></tr>`;
    }
}

async function dispararCumpleanios() {
    try {
        showAlert('⏳ Ejecutando verificación de cumpleaños...', 'loading');
        const res = await fetch(`${API_BASE}/notificaciones/ejecutar-cumpleanios`, { method: 'POST' });
        if (!res.ok) throw new Error('Error ejecutando tarea de cumpleaños');
        const resp = await res.json();
        showAlert(`🎉 Tarea ejecutada con éxito. Total correos emitidos: ${resp.totalEnviados}`);
        await loadNotificaciones();
    } catch (e) {
        showAlert(e.message, 'error');
    }
}

async function dispararVencimientos() {
    try {
        showAlert('⏳ Ejecutando verificación de préstamos por vencer...', 'loading');
        const res = await fetch(`${API_BASE}/notificaciones/ejecutar-vencimientos`, { method: 'POST' });
        if (!res.ok) throw new Error('Error ejecutando tarea de vencimientos');
        const resp = await res.json();
        showAlert(`⏰ Tarea ejecutada con éxito. Total recordatorios emitidos: ${resp.totalEnviados}`);
        await loadNotificaciones();
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
