let token = null;

function showSection(sectionId) {
    document.querySelectorAll('.section').forEach(section => section.classList.add('hidden'));
    document.getElementById(`${sectionId}-section`).classList.remove('hidden');
}

async function register() {
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;
    const response = await fetch('/api/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    const result = await response.json();
    document.getElementById('result').innerText = result.message;
    document.getElementById('result').classList.remove('hidden');
}

async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;
    const response = await fetch('/api/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });
    const result = await response.json();
    if (response.ok) {
        token = result.token;
        document.getElementById('custom-url').disabled = false;
        document.getElementById('auth-section').classList.add('hidden');
        document.getElementById('result').innerText = 'Logged in successfully';
    } else {
        document.getElementById('result').innerText = result.message;
    }
    document.getElementById('result').classList.remove('hidden');
}

async function shortenUrl() {
    const longUrl = document.getElementById('long-url').value;
    const customUrl = document.getElementById('custom-url').value;
    const endpoint = customUrl && token ? '/api/custom' : '/api/shorten';
    const headers = { 'Content-Type': 'application/json' };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    const response = await fetch(endpoint, {
        method: 'POST',
        headers,
        body: JSON.stringify({ longUrl, customUrl })
    });
    const result = await response.json();
    document.getElementById('result').innerText = response.ok
        ? `Short URL: http://localhost:8000/${result.shortUrl}`
        : result.message;
    document.getElementById('result').classList.remove('hidden');
}