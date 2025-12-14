

document.addEventListener('DOMContentLoaded', function() {
    loadPlans();
});


async function loadPlans() {
    const plansContainer = document.getElementById('plansContainer');
    
    try {
        const response = await makeAuthenticatedRequest(API_ENDPOINTS.GET_ALL_PLANS);
        const data = await response.json();
        
        if (data.success && data.plans) {
            displayPlans(data.plans);
        } else {
            plansContainer.innerHTML = '<p>No plans available at the moment.</p>';
        }
    } catch (error) {
        console.error('Error loading plans:', error);
        plansContainer.innerHTML = '<p>Error loading plans. Please try again later.</p>';
    }
}


function displayPlans(plans) {
    const plansContainer = document.getElementById('plansContainer');
    plansContainer.innerHTML = '';
    
    if (plans.length === 0) {
        plansContainer.innerHTML = '<p>No plans available yet.</p>';
        return;
    }
    
    plans.forEach(plan => {
        const planCard = createPlanCard(plan);
        plansContainer.appendChild(planCard);
    });
}


function createPlanCard(plan) {
    const card = document.createElement('div');
    card.className = 'plan-card';
    
    let content = `
        <h3>${plan.title}</h3>
        <p class="plan-trainer">by ${plan.trainerName}</p>
        <p class="plan-price">₹${plan.price}</p>
        <p class="plan-duration">${plan.durationDays} days</p>
    `;
    
    if (plan.isSubscribed) {
        content += `
            <p class="plan-description">${plan.description}</p>
            <button class="btn btn-success" disabled>Subscribed ✓</button>
        `;
    } else {
        content += `
            <div class="locked-message">
                🔒 Subscribe to view full details
            </div>
            <button class="btn btn-primary" onclick="subscribeToPlan(${plan.planId})">
                Subscribe Now
            </button>
        `;
    }
    
    card.innerHTML = content;
    return card;
}


async function subscribeToPlan(planId) {
    if (!isLoggedIn()) {
        alert('Please login to subscribe to plans');
        window.location.href = 'pages/login.html';
        return;
    }
    
    const user = getCurrentUser();
    if (user.userType === 'TRAINER') {
        alert('Trainers cannot subscribe to plans');
        return;
    }
    
    try {
        const response = await makeAuthenticatedRequest(API_ENDPOINTS.SUBSCRIBE, {
            method: 'POST',
            body: JSON.stringify({ planId: planId })
        });
        
        const data = await response.json();
        
        if (data.success) {
            alert('Successfully subscribed to plan!');
            loadPlans();
        } else {
            alert(data.message || 'Subscription failed');
        }
    } catch (error) {
        console.error('Error subscribing:', error);
        alert('Error subscribing to plan');
    }
}