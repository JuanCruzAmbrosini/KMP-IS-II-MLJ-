let currentToken = localStorage.getItem('jwt_token_' + window.location.port) || '';

function switchTab(tab) {
    document.querySelectorAll('.tab-btn').forEach((btn, idx) => {
        btn.classList.toggle('active', (tab === 'login' && idx === 0) || (tab === 'register' && idx === 1));
    });
    document.getElementById('loginForm').classList.toggle('active', tab === 'login');
    document.getElementById('registerForm').classList.toggle('active', tab === 'register');
}

function fillCredentials(user, pass) {
    document.getElementById('loginUsername').value = user;
    document.getElementById('loginPassword').value = pass;
}

function setToken(token) {
    currentToken = token || '';
    document.getElementById('jwtRawInput').value = currentToken;
    const pill = document.getElementById('authStatusPill');
    if (currentToken) {
        pill.textContent = 'Token JWT Activo';
        pill.classList.add('active');
    } else {
        pill.textContent = 'Sin Token Activo';
        pill.classList.remove('active');
    }
    decodeJwtDisplay();
}

function clearToken() {
    setToken('');
    showResponse(200, { info: 'Token eliminado de la sesión actual.' });
}

function decodeJwtDisplay() {
    const raw = document.getElementById('jwtRawInput').value.trim();
    currentToken = raw;
    const headerEl = document.getElementById('jwtHeaderOutput');
    const payloadEl = document.getElementById('jwtPayloadOutput');
    if (!raw || raw.split('.').length < 2) {
        headerEl.textContent = '{}';
        payloadEl.textContent = '{}';
        return;
    }
    try {
        const parts = raw.split('.');
        const decodeB64 = (str) => JSON.parse(atob(str.replace(/-/g, '+').replace(/_/g, '/')));
        headerEl.textContent = JSON.stringify(decodeB64(parts[0]), null, 2);
        payloadEl.textContent = JSON.stringify(decodeB64(parts[1]), null, 2);
    } catch (e) {
        headerEl.textContent = '// Token malformado';
        payloadEl.textContent = '// No se pudo decodificar Base64';
    }
}

function showResponse(status, body) {
    const badge = document.getElementById('httpStatusBadge');
    badge.textContent = 'HTTP ' + status;
    badge.className = 'http-badge ' + (status >= 200 && status < 300 ? 'ok' : 'err');
    const out = document.getElementById('apiResponseOutput');
    out.textContent = typeof body === 'string' ? body : JSON.stringify(body, null, 2);
}

async function handleLogin(e) {
    e.preventDefault();
    const payload = {
        username: document.getElementById('loginUsername').value.trim(),
        password: document.getElementById('loginPassword').value
    };
    try {
        const res = await fetch(window.APP_CONFIG.loginUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const text = await res.text();
        let data = text;
        try { data = JSON.parse(text); } catch (_) {}
        showResponse(res.status, data);
        if (res.ok && data && data.token) {
            setToken(data.token);
        }
    } catch (err) {
        showResponse(500, { error: err.message });
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const payload = {
        username: document.getElementById('regUsername').value.trim(),
        password: document.getElementById('regPassword').value,
        firstname: document.getElementById('regFirstname').value.trim(),
        lastname: document.getElementById('regLastname').value.trim(),
        country: document.getElementById('regCountry').value.trim()
    };
    try {
        const res = await fetch(window.APP_CONFIG.registerUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const text = await res.text();
        let data = text;
        try { data = JSON.parse(text); } catch (_) {}
        showResponse(res.status, data);
        if (res.ok && data && data.token) {
            setToken(data.token);
        }
    } catch (err) {
        showResponse(500, { error: err.message });
    }
}

async function callProtectedEndpoint(withToken) {
    const headers = {};
    if (withToken && currentToken) {
        headers['Authorization'] = 'Bearer ' + currentToken;
    }
    try {
        const res = await fetch(window.APP_CONFIG.protectedUrl, {
            method: window.APP_CONFIG.protectedMethod,
            headers
        });
        const text = await res.text();
        let data = text;
        try { data = JSON.parse(text); } catch (_) {}
        showResponse(res.status, data || (res.status === 403 ? '403 Forbidden (Acceso denegado por Spring Security)' : '401 Unauthorized'));
    } catch (err) {
        showResponse(500, { error: err.message });
    }
}

