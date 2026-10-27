// Cliente REST API base URL
const API_BASE = '/cliente/api';

// Estado de autenticación JWT
let currentToken = localStorage.getItem('jwt_token') || '';
let currentUser = JSON.parse(localStorage.getItem('jwt_user') || 'null');

// Cache de datos para selección en relaciones
let cachedLocalidades = [];
let cachedDomicilios = [];
let cachedAutores = [];
let cachedLibros = [];

// ==========================================
// INICIALIZACIÓN
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    initAuth();
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
    }, 4500);
}

function hasValidCoords(lat, lon) {
    return lat != null && lon != null && String(lat).trim() !== '' && String(lon).trim() !== '';
}

// ==========================================
// GESTIÓN DE AUTENTICACIÓN JWT
// ==========================================
function initAuth() {
    if (!currentToken) {
        // Por defecto loguear como admin para facilitar la primera experiencia de uso
        quickLogin('admin', 'admin123', false);
    } else {
        updateAuthUI();
    }
}

function updateAuthUI() {
    const pill = document.getElementById('authStatusPill');
    const text = document.getElementById('authStatusText');
    const btnLogin = document.getElementById('btnShowLogin');
    const btnRegister = document.getElementById('btnShowRegister');
    const btnToken = document.getElementById('btnViewToken');
    const btnLogout = document.getElementById('btnLogout');
    const toolbarBtnToken = document.getElementById('toolbarBtnToken');

    const chipAdmin = document.getElementById('chipAdmin');
    const chipUser = document.getElementById('chipUser');
    const chipGuest = document.getElementById('chipGuest');

    if (chipAdmin) chipAdmin.classList.remove('active-chip');
    if (chipUser) chipUser.classList.remove('active-chip');
    if (chipGuest) chipGuest.classList.remove('active-chip');

    if (currentToken && currentUser) {
        pill.className = 'auth-pill authenticated';
        text.textContent = `${currentUser.username} (${currentUser.rol || 'ROLE_USER'})`;
        if (btnLogin) btnLogin.classList.add('hidden');
        if (btnRegister) btnRegister.classList.add('hidden');
        if (btnToken) btnToken.classList.remove('hidden');
        if (btnLogout) btnLogout.classList.remove('hidden');
        if (toolbarBtnToken) toolbarBtnToken.classList.remove('hidden');

        if (currentUser.username === 'admin' && chipAdmin) {
            chipAdmin.classList.add('active-chip');
        } else if (currentUser.username === 'user' && chipUser) {
            chipUser.classList.add('active-chip');
        }
    } else {
        pill.className = 'auth-pill unauthenticated';
        text.textContent = 'No autenticado';
        if (btnLogin) btnLogin.classList.remove('hidden');
        if (btnRegister) btnRegister.classList.remove('hidden');
        if (btnToken) btnToken.classList.add('hidden');
        if (btnLogout) btnLogout.classList.add('hidden');
        if (toolbarBtnToken) toolbarBtnToken.classList.add('hidden');

        if (chipGuest) chipGuest.classList.add('active-chip');
    }
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

async function authFetch(url, options = {}) {
    options.headers = options.headers || {};
    if (currentToken) {
        options.headers['Authorization'] = 'Bearer ' + currentToken;
    }

    const res = await fetch(url, options);

    if (res.status === 401) {
        let errorData = null;
        try { errorData = await res.json(); } catch(e){}
        const msg = (errorData && errorData.message)
            ? errorData.message
            : 'Acceso Denegado (401 Unauthorized): Se requiere Token JWT Bearer válido.';
        showAlert(`[401 No Autorizado] ${msg}`, 'error');
        throw new Error(msg);
    }

    if (res.status === 403) {
        let errorData = null;
        try { errorData = await res.json(); } catch(e){}
        const msg = (errorData && errorData.message)
            ? errorData.message
            : 'Acción Prohibida (403 Forbidden): Se requieren permisos de Administrador (ROLE_ADMIN).';
        showAlert(`[403 Prohibido] ${msg}`, 'error');
        throw new Error(msg);
    }

    return res;
}

async function quickLogin(username, password, triggerLoad = true) {
    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || 'Error en inicio de sesión');
        }

        const data = await res.json();
        currentToken = data.token;
        currentUser = {
            username: data.username,
            rol: data.rol,
            nombreCompleto: data.nombreCompleto
        };

        localStorage.setItem('jwt_token', currentToken);
        localStorage.setItem('jwt_user', JSON.stringify(currentUser));

        updateAuthUI();
        showAlert(`Sesión iniciada como ${data.username} (${data.rol})`, 'success');
        if (triggerLoad) await loadAll();
    } catch (e) {
        showAlert(`Error al autenticar: ${e.message}`, 'error');
    }
}

function simulateUnauthenticated() {
    currentToken = '';
    currentUser = null;
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
    updateAuthUI();
    showAlert('Cliente sin token: las operaciones protegidas responderán 401 Unauthorized', 'error');
    loadAll();
}

function logout() {
    currentToken = '';
    currentUser = null;
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('jwt_user');
    updateAuthUI();
    showAlert('Sesión finalizada', 'success');
    loadAll();
}

function openLoginModal() {
    document.getElementById('loginError').classList.add('hidden');
    document.getElementById('loginUsername').value = '';
    document.getElementById('loginPassword').value = '';
    document.getElementById('loginModal').classList.remove('hidden');
}

function openRegisterModal() {
    document.getElementById('registerError').classList.add('hidden');
    document.getElementById('regUsername').value = '';
    document.getElementById('regPassword').value = '';
    document.getElementById('regNombre').value = '';
    document.getElementById('regEmail').value = '';
    document.getElementById('regRol').value = 'ROLE_USER';
    document.getElementById('registerModal').classList.remove('hidden');
}

function openTokenModal() {
    if (!currentToken) {
        showAlert('No hay un token activo para visualizar. Inicie sesión primero.', 'error');
        return;
    }
    document.getElementById('rawTokenText').value = currentToken;
    const decoded = parseJwt(currentToken);
    document.getElementById('decodedTokenPayload').textContent = JSON.stringify(decoded, null, 2);
    document.getElementById('tokenModal').classList.remove('hidden');
}

function copyToken() {
    const raw = document.getElementById('rawTokenText');
    raw.select();
    navigator.clipboard.writeText(raw.value);
    showAlert('Token copiado al portapapeles', 'success');
}

async function handleLoginSubmit(e) {
    e.preventDefault();
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errBox = document.getElementById('loginError');

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            let msg = 'Credenciales inválidas';
            try {
                const err = await res.json();
                if (err.message) msg = err.message;
            } catch(ex){}
            throw new Error(msg);
        }

        const data = await res.json();
        currentToken = data.token;
        currentUser = {
            username: data.username,
            rol: data.rol,
            nombreCompleto: data.nombreCompleto
        };

        localStorage.setItem('jwt_token', currentToken);
        localStorage.setItem('jwt_user', JSON.stringify(currentUser));

        closeModal('loginModal');
        updateAuthUI();
        showAlert(`Sesión iniciada como ${data.username} (${data.rol})`, 'success');
        await loadAll();
    } catch (err) {
        errBox.textContent = err.message;
        errBox.classList.remove('hidden');
    }
}

async function handleRegisterSubmit(e) {
    e.preventDefault();
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const nombreCompleto = document.getElementById('regNombre').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const rol = document.getElementById('regRol').value;
    const errBox = document.getElementById('registerError');

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, nombreCompleto, email, rol })
        });

        if (!res.ok) {
            let msg = 'Error en el registro';
            try {
                const err = await res.json();
                if (err.message) msg = err.message;
            } catch(ex){}
            throw new Error(msg);
        }

        const data = await res.json();
        currentToken = data.token;
        currentUser = {
            username: data.username,
            rol: data.rol,
            nombreCompleto: data.nombreCompleto
        };

        localStorage.setItem('jwt_token', currentToken);
        localStorage.setItem('jwt_user', JSON.stringify(currentUser));

        closeModal('registerModal');
        updateAuthUI();
        showAlert(`Usuario ${data.username} registrado y autenticado`, 'success');
        await loadAll();
    } catch (err) {
        errBox.textContent = err.message;
        errBox.classList.remove('hidden');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) modal.classList.add('hidden');
}

// ==========================================
// CARGA GLOBAL
// ==========================================
async function loadAll() {
    try {
        await Promise.all([
            loadLocalidades(),
            loadDomicilios(),
            loadAutores(),
            loadLibros()
        ]);
        await loadPersonas();
    } catch (e) {
        console.error('Error cargando datos:', e);
    }
}

// ==========================================
// 1. LOCALIDADES
// ==========================================
async function loadLocalidades() {
    try {
        const res = await authFetch(`${API_BASE}/localidades`);
        if (!res.ok) throw new Error('Error al consultar localidades');
        cachedLocalidades = await res.json();
        
        const tbody = document.getElementById('localidadesTableBody');
        if (cachedLocalidades.length === 0) {
            tbody.innerHTML = '<tr><td colspan="3" class="loading">No hay localidades registradas</td></tr>';
            return;
        }

        tbody.innerHTML = cachedLocalidades.map(loc => `
            <tr>
                <td><code>#${loc.id}</code></td>
                <td><strong>${loc.denominacion}</strong></td>
                <td style="text-align: right;">
                    <button class="btn btn-sm btn-outline" onclick="editLocalidad(${loc.id})">Editar</button>
                    <button class="btn btn-sm btn-subtle-danger" onclick="deleteLocalidad(${loc.id})">Eliminar</button>
                </td>
            </tr>
        `).join('');

        populateLocalidadesSelect();
    } catch (e) {
        console.error(e);
        document.getElementById('localidadesTableBody').innerHTML = 
            `<tr><td colspan="3" class="loading">Error al cargar: ${e.message}</td></tr>`;
    }
}

function populateLocalidadesSelect() {
    const select = document.getElementById('domicilioLocalidad');
    if (!select) return;
    select.innerHTML = '<option value="">Seleccione Localidad</option>' +
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
        const res = await authFetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar localidad');
        
        closeModal('localidadModal');
        showAlert(`Localidad ${id ? 'actualizada' : 'creada'} correctamente`);
        await loadLocalidades();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteLocalidad(id) {
    if (!confirm('¿Desea eliminar esta localidad?')) return;
    try {
        const res = await authFetch(`${API_BASE}/localidades/${id}`, { method: 'DELETE' });
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
        const res = await authFetch(`${API_BASE}/domicilios`);
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
                ? `<span class="coords-text">${d.latitud}, ${d.longitud}</span>`
                : '<span class="muted-text">Sin datos</span>';

            const mapsBtn = hasCoords
                ? `<a href="${mapsUrl}" target="_blank" rel="noopener noreferrer" class="link-maps">Google Maps &nearr;</a>`
                : '<span class="muted-text">-</span>';

            return `
                <tr>
                    <td><code>#${d.id}</code></td>
                    <td><strong>${d.calle} ${d.numero}</strong></td>
                    <td>${d.localidad ? d.localidad.denominacion : '<span class="muted-text">Sin asignar</span>'}</td>
                    <td>${coordsContent}</td>
                    <td>${mapsBtn}</td>
                    <td style="text-align: right;">
                        <button class="btn btn-sm btn-outline" onclick="editDomicilio(${d.id})">Editar</button>
                        <button class="btn btn-sm btn-subtle-danger" onclick="deleteDomicilio(${d.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');

        populateDomiciliosSelect();
    } catch (e) {
        console.error(e);
        document.getElementById('domiciliosTableBody').innerHTML = 
            `<tr><td colspan="6" class="loading">Error al cargar: ${e.message}</td></tr>`;
    }
}

function populateDomiciliosSelect() {
    const select = document.getElementById('personaDomicilio');
    if (!select) return;
    select.innerHTML = '<option value="">Sin Domicilio</option>' +
        cachedDomicilios.map(d => {
            const locName = d.localidad ? ` (${d.localidad.denominacion})` : '';
            const coordsInfo = hasValidCoords(d.latitud, d.longitud) ? ' [Maps]' : '';
            return `<option value="${d.id}">${d.calle} ${d.numero}${locName}${coordsInfo}</option>`;
        }).join('');
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
    const dom = cachedDomicilios.find(d => d.id === id);
    if (dom) openDomicilioModal(dom);
}

async function saveDomicilio(e) {
    e.preventDefault();
    const id = document.getElementById('domicilioId').value;
    const calle = document.getElementById('domicilioCalle').value.trim();
    const numero = parseInt(document.getElementById('domicilioNumero').value, 10);
    const locId = document.getElementById('domicilioLocalidad').value;
    const latitud = document.getElementById('domicilioLatitud').value.trim();
    const longitud = document.getElementById('domicilioLongitud').value.trim();

    let localidad = null;
    if (locId) {
        localidad = cachedLocalidades.find(l => l.id === parseInt(locId, 10));
    }

    const payload = {
        calle,
        numero,
        localidad,
        latitud: latitud || null,
        longitud: longitud || null
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/domicilios/${id}` : `${API_BASE}/domicilios`;
        const res = await authFetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar domicilio');

        closeModal('domicilioModal');
        showAlert(`Domicilio ${id ? 'actualizado' : 'creado'} correctamente`);
        await loadDomicilios();
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteDomicilio(id) {
    if (!confirm('¿Desea eliminar este domicilio?')) return;
    try {
        const res = await authFetch(`${API_BASE}/domicilios/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar domicilio');
        showAlert('Domicilio eliminado');
        await loadDomicilios();
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 3. AUTORES
// ==========================================
async function loadAutores() {
    try {
        const res = await authFetch(`${API_BASE}/autores`);
        if (!res.ok) throw new Error('Error al consultar autores');
        cachedAutores = await res.json();

        const tbody = document.getElementById('autoresTableBody');
        if (cachedAutores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="loading">No hay autores registrados</td></tr>';
            return;
        }

        tbody.innerHTML = cachedAutores.map(a => `
            <tr>
                <td><code>#${a.id}</code></td>
                <td><strong>${a.nombre} ${a.apellido}</strong></td>
                <td><span class="muted-text">${a.biografia || 'Sin reseña'}</span></td>
                <td style="text-align: right;">
                    <button class="btn btn-sm btn-outline" onclick="editAutor(${a.id})">Editar</button>
                    <button class="btn btn-sm btn-subtle-danger" onclick="deleteAutor(${a.id})">Eliminar</button>
                </td>
            </tr>
        `).join('');

        renderAutoresCheckboxList();
    } catch (e) {
        console.error(e);
        document.getElementById('autoresTableBody').innerHTML = 
            `<tr><td colspan="4" class="loading">Error al cargar: ${e.message}</td></tr>`;
    }
}

function renderAutoresCheckboxList(selectedIds = []) {
    const container = document.getElementById('libroAutoresList');
    if (!container) return;
    if (cachedAutores.length === 0) {
        container.innerHTML = '<span class="muted-text">No hay autores registrados</span>';
        return;
    }
    container.innerHTML = cachedAutores.map(a => {
        const isChecked = selectedIds.includes(a.id) ? 'checked' : '';
        return `
            <label class="check-item">
                <input type="checkbox" value="${a.id}" ${isChecked}>
                <span>${a.nombre} ${a.apellido}</span>
            </label>
        `;
    }).join('');
}

function openAutorModal(autor = null) {
    document.getElementById('autorModalTitle').textContent = autor ? 'Editar Autor' : 'Nuevo Autor';
    document.getElementById('autorId').value = autor ? autor.id : '';
    document.getElementById('autorNombre').value = autor ? autor.nombre : '';
    document.getElementById('autorApellido').value = autor ? autor.apellido : '';
    document.getElementById('autorBiografia').value = autor && autor.biografia ? autor.biografia : '';
    document.getElementById('autorModal').classList.remove('hidden');
}

function editAutor(id) {
    const autor = cachedAutores.find(a => a.id === id);
    if (autor) openAutorModal(autor);
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
        const res = await authFetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar autor');

        closeModal('autorModal');
        showAlert(`Autor ${id ? 'actualizado' : 'creado'} correctamente`);
        await loadAutores();
        await loadLibros();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteAutor(id) {
    if (!confirm('¿Desea eliminar este autor?')) return;
    try {
        const res = await authFetch(`${API_BASE}/autores/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar autor');
        showAlert('Autor eliminado');
        await loadAutores();
        await loadLibros();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 4. LIBROS
// ==========================================
async function loadLibros() {
    try {
        const res = await authFetch(`${API_BASE}/libros`);
        if (!res.ok) throw new Error('Error al consultar libros');
        cachedLibros = await res.json();

        const tbody = document.getElementById('librosTableBody');
        if (cachedLibros.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="loading">No hay libros registrados</td></tr>';
            return;
        }

        tbody.innerHTML = cachedLibros.map(lib => {
            const autoresList = (lib.autores && lib.autores.length > 0)
                ? lib.autores.map(a => `<span class="tag">${a.nombre} ${a.apellido}</span>`).join(' ')
                : '<span class="muted-text">Sin vincular</span>';

            return `
                <tr>
                    <td><code>#${lib.id}</code></td>
                    <td><strong>${lib.titulo}</strong></td>
                    <td><span class="tag">${lib.genero}</span></td>
                    <td>${lib.fecha}</td>
                    <td>${lib.paginas}</td>
                    <td>${lib.autor || '-'}</td>
                    <td>${autoresList}</td>
                    <td style="text-align: right;">
                        <button class="btn btn-sm btn-outline" onclick="editLibro(${lib.id})">Editar</button>
                        <button class="btn btn-sm btn-subtle-danger" onclick="deleteLibro(${lib.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');

        renderPersonasLibrosCheckboxList();
    } catch (e) {
        console.error(e);
        document.getElementById('librosTableBody').innerHTML = 
            `<tr><td colspan="8" class="loading">Error al cargar: ${e.message}</td></tr>`;
    }
}

function renderPersonasLibrosCheckboxList(selectedIds = []) {
    const container = document.getElementById('personaLibrosList');
    if (!container) return;
    if (cachedLibros.length === 0) {
        container.innerHTML = '<span class="muted-text">No hay libros registrados</span>';
        return;
    }
    container.innerHTML = cachedLibros.map(l => {
        const isChecked = selectedIds.includes(l.id) ? 'checked' : '';
        return `
            <label class="check-item">
                <input type="checkbox" value="${l.id}" ${isChecked}>
                <span>${l.titulo} (${l.fecha})</span>
            </label>
        `;
    }).join('');
}

function openLibroModal(libro = null) {
    document.getElementById('libroModalTitle').textContent = libro ? 'Editar Libro' : 'Nuevo Libro';
    document.getElementById('libroId').value = libro ? libro.id : '';
    document.getElementById('libroTitulo').value = libro ? libro.titulo : '';
    document.getElementById('libroGenero').value = libro ? libro.genero : '';
    document.getElementById('libroFecha').value = libro ? libro.fecha : '';
    document.getElementById('libroPaginas').value = libro ? libro.paginas : '';
    document.getElementById('libroAutorTexto').value = libro ? (libro.autor || '') : '';

    const selectedAutorIds = (libro && libro.autores) ? libro.autores.map(a => a.id) : [];
    renderAutoresCheckboxList(selectedAutorIds);

    document.getElementById('libroModal').classList.remove('hidden');
}

function editLibro(id) {
    const libro = cachedLibros.find(l => l.id === id);
    if (libro) openLibroModal(libro);
}

async function saveLibro(e) {
    e.preventDefault();
    const id = document.getElementById('libroId').value;
    const titulo = document.getElementById('libroTitulo').value.trim();
    const genero = document.getElementById('libroGenero').value.trim();
    const fecha = parseInt(document.getElementById('libroFecha').value, 10);
    const paginas = parseInt(document.getElementById('libroPaginas').value, 10);
    const autorTexto = document.getElementById('libroAutorTexto').value.trim();

    const checkedInputs = document.querySelectorAll('#libroAutoresList input[type="checkbox"]:checked');
    const selectedAutores = Array.from(checkedInputs).map(cb => {
        const aId = parseInt(cb.value, 10);
        return cachedAutores.find(a => a.id === aId);
    }).filter(Boolean);

    const payload = {
        titulo,
        genero,
        fecha,
        paginas,
        autor: autorTexto,
        autores: selectedAutores
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/libros/${id}` : `${API_BASE}/libros`;
        const res = await authFetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar libro');

        closeModal('libroModal');
        showAlert(`Libro ${id ? 'actualizado' : 'creado'} correctamente`);
        await loadLibros();
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deleteLibro(id) {
    if (!confirm('¿Desea eliminar este libro?')) return;
    try {
        const res = await authFetch(`${API_BASE}/libros/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar libro');
        showAlert('Libro eliminado');
        await loadLibros();
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

// ==========================================
// 5. PERSONAS
// ==========================================
async function loadPersonas() {
    try {
        const res = await authFetch(`${API_BASE}/personas`);
        if (!res.ok) throw new Error('Error al consultar personas');
        const personas = await res.json();

        const tbody = document.getElementById('personasTableBody');
        if (personas.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="loading">No hay personas registradas</td></tr>';
            return;
        }

        tbody.innerHTML = personas.map(p => {
            let domicilioText = '<span class="muted-text">Sin domicilio</span>';
            if (p.domicilio) {
                const locInfo = p.domicilio.localidad ? `, ${p.domicilio.localidad.denominacion}` : '';
                const hasCoords = hasValidCoords(p.domicilio.latitud, p.domicilio.longitud);
                const mapsUrl = hasCoords 
                    ? `https://www.google.com/maps?q=${encodeURIComponent(String(p.domicilio.latitud).trim())},${encodeURIComponent(String(p.domicilio.longitud).trim())}`
                    : '#';

                const mapsBtn = hasCoords
                    ? `<a href="${mapsUrl}" target="_blank" rel="noopener noreferrer" class="link-maps">Google Maps &nearr;</a>`
                    : '';

                domicilioText = `
                    <div>
                        <strong>${p.domicilio.calle} ${p.domicilio.numero}</strong>${locInfo}
                        ${mapsBtn ? `<span style="margin-left: 8px;">${mapsBtn}</span>` : ''}
                    </div>
                `;
            }

            const librosList = (p.libros && p.libros.length > 0)
                ? p.libros.map(lib => `<span class="tag">${lib.titulo}</span>`).join(' ')
                : '<span class="muted-text">Sin libros</span>';

            return `
                <tr>
                    <td><code>#${p.id}</code></td>
                    <td><strong>${p.nombre} ${p.apellido}</strong></td>
                    <td>${p.dni}</td>
                    <td>${domicilioText}</td>
                    <td>${librosList}</td>
                    <td style="text-align: right;">
                        <button class="btn btn-sm btn-outline" onclick="editPersona(${p.id})">Editar</button>
                        <button class="btn btn-sm btn-subtle-danger" onclick="deletePersona(${p.id})">Eliminar</button>
                    </td>
                </tr>
            `;
        }).join('');
    } catch (e) {
        console.error(e);
        document.getElementById('personasTableBody').innerHTML = 
            `<tr><td colspan="6" class="loading">Error al cargar: ${e.message}</td></tr>`;
    }
}

function openPersonaModal(persona = null) {
    document.getElementById('personaModalTitle').textContent = persona ? 'Editar Persona' : 'Nueva Persona';
    document.getElementById('personaId').value = persona ? persona.id : '';
    document.getElementById('personaNombre').value = persona ? persona.nombre : '';
    document.getElementById('personaApellido').value = persona ? persona.apellido : '';
    document.getElementById('personaDni').value = persona ? persona.dni : '';
    document.getElementById('personaDomicilio').value = (persona && persona.domicilio) ? persona.domicilio.id : '';

    const selectedLibroIds = (persona && persona.libros) ? persona.libros.map(l => l.id) : [];
    renderPersonasLibrosCheckboxList(selectedLibroIds);

    document.getElementById('personaModal').classList.remove('hidden');
}

async function editPersona(id) {
    try {
        const res = await authFetch(`${API_BASE}/personas/${id}`);
        if (!res.ok) throw new Error('Error al obtener persona');
        const persona = await res.json();
        openPersonaModal(persona);
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function savePersona(e) {
    e.preventDefault();
    const id = document.getElementById('personaId').value;
    const nombre = document.getElementById('personaNombre').value.trim();
    const apellido = document.getElementById('personaApellido').value.trim();
    const dni = parseInt(document.getElementById('personaDni').value, 10);
    const domId = document.getElementById('personaDomicilio').value;

    let domicilio = null;
    if (domId) {
        domicilio = cachedDomicilios.find(d => d.id === parseInt(domId, 10));
    }

    const checkedInputs = document.querySelectorAll('#personaLibrosList input[type="checkbox"]:checked');
    const selectedLibros = Array.from(checkedInputs).map(cb => {
        const lId = parseInt(cb.value, 10);
        return cachedLibros.find(l => l.id === lId);
    }).filter(Boolean);

    const payload = {
        nombre,
        apellido,
        dni,
        domicilio,
        libros: selectedLibros
    };

    try {
        const method = id ? 'PUT' : 'POST';
        const url = id ? `${API_BASE}/personas/${id}` : `${API_BASE}/personas`;
        const res = await authFetch(url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        if (!res.ok) throw new Error('Error al guardar persona');

        closeModal('personaModal');
        showAlert(`Persona ${id ? 'actualizada' : 'creada'} correctamente`);
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}

async function deletePersona(id) {
    if (!confirm('¿Desea eliminar este registro?')) return;
    try {
        const res = await authFetch(`${API_BASE}/personas/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error('Error al eliminar persona');
        showAlert('Persona eliminada');
        await loadPersonas();
    } catch (err) {
        showAlert(err.message, 'error');
    }
}
