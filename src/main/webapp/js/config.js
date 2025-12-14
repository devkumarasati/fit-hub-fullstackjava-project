


const API_BASE_URL = 'http://localhost:8080/FitPlanHub/api';

const API_ENDPOINTS = {

    SIGNUP: `${API_BASE_URL}/auth?action=signup`,
    LOGIN: `${API_BASE_URL}/auth?action=login`,
    LOGOUT: `${API_BASE_URL}/auth?action=logout`,


    GET_ALL_PLANS: `${API_BASE_URL}/plans?action=all`,
    GET_PLAN_BY_ID: `${API_BASE_URL}/plans?action=byId`,
    GET_MY_PLANS: `${API_BASE_URL}/plans?action=myPlans`,
    CREATE_PLAN: `${API_BASE_URL}/plans`,
    UPDATE_PLAN: `${API_BASE_URL}/plans`,
    DELETE_PLAN: `${API_BASE_URL}/plans`,


    SUBSCRIBE: `${API_BASE_URL}/subscriptions`,
    GET_SUBSCRIPTIONS: `${API_BASE_URL}/subscriptions`,


    FOLLOW: `${API_BASE_URL}/follows`,
    UNFOLLOW: `${API_BASE_URL}/follows`,
    GET_FOLLOWED: `${API_BASE_URL}/follows?action=trainers`,
    GET_FEED: `${API_BASE_URL}/follows?action=feed`,
    CHECK_FOLLOW: `${API_BASE_URL}/follows?action=checkFollow`
};


function getAuthToken() {
    return localStorage.getItem('authToken');
}


function getCurrentUser() {
    const userStr = localStorage.getItem('currentUser');
    return userStr ? JSON.parse(userStr) : null;
}


function isLoggedIn() {
    return getAuthToken() !== null;
}


function logout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    window.location.href = '../index.html';
}