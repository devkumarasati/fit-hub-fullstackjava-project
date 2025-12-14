


function updateNavigation() {
    const loginLink = document.getElementById('loginLink');
    const signupLink = document.getElementById('signupLink');
    const dashboardLink = document.getElementById('dashboardLink');
    const feedLink = document.getElementById('feedLink');
    const logoutBtn = document.getElementById('logoutBtn');
    const userGreeting = document.getElementById('userGreeting');

    if (isLoggedIn()) {
        const user = getCurrentUser();


        if (loginLink) loginLink.style.display = 'none';
        if (signupLink) signupLink.style.display = 'none';


        if (dashboardLink) {
            dashboardLink.style.display = 'block';
            if (user.userType === 'TRAINER') {
                dashboardLink.href = 'pages/trainer-dashboard.html';
            } else {
                dashboardLink.href = 'pages/user-dashboard.html';
            }
        }

        if (feedLink) feedLink.style.display = 'block';
        if (logoutBtn) logoutBtn.style.display = 'block';

        if (userGreeting) {
            userGreeting.style.display = 'block';
            userGreeting.textContent = `Hello, ${user.fullName}`;
        }
    } else {

        if (loginLink) loginLink.style.display = 'block';
        if (signupLink) signupLink.style.display = 'block';


        if (dashboardLink) dashboardLink.style.display = 'none';
        if (feedLink) feedLink.style.display = 'none';
        if (logoutBtn) logoutBtn.style.display = 'none';
        if (userGreeting) userGreeting.style.display = 'none';
    }


    if (logoutBtn) {
        logoutBtn.onclick = function () {
            logout();
        };
    }
}


async function makeAuthenticatedRequest(url, options = {}) {
    const token = getAuthToken();

    if (!options.headers) {
        options.headers = {};
    }

    if (token) {
        options.headers['Authorization'] = `Bearer ${token}`;
    }

    options.headers['Content-Type'] = 'application/json';

    try {
        const response = await fetch(url, options);


        if (response.status === 401) {
            alert('Session expired. Please login again.');
            logout();
            return null;
        }

        return response;
    } catch (error) {
        console.error('Request failed:', error);
        throw error;
    }
}


function requireRole(role) {
    const user = getCurrentUser();

    if (!user || user.userType !== role) {
        alert(`This page is only accessible to ${role}s`);
        window.location.href = '../index.html';
        return false;
    }

    return true;
}


function requireLogin() {
    if (!isLoggedIn()) {
        alert('Please login to access this page');
        window.location.href = 'login.html';
        return false;
    }
    return true;
}


document.addEventListener('DOMContentLoaded', function () {
    updateNavigation();
});