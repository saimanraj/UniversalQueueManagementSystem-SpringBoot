/* ==========================================
   UQS - Online Queue Management System
   Main JavaScript
   ========================================== */

'use strict';

/* ── Toast Notifications ── */
function showToast(message, type = 'success') {
    // Remove existing toasts
    document.querySelectorAll('.uqs-toast').forEach(t => t.remove());

    const icons = {
        success: 'fa-check-circle',
        error:   'fa-exclamation-circle',
        info:    'fa-info-circle',
        warning: 'fa-exclamation-triangle'
    };
    const colors = {
        success: '#2ecc71',
        error:   '#e63946',
        info:    '#4cc9f0',
        warning: '#ff9f1c'
    };

    const toast = document.createElement('div');
    toast.className = 'uqs-toast';
    toast.innerHTML = `
        <div style="
            position:fixed; bottom:24px; right:24px; z-index:9999;
            background:${colors[type]}; color:#fff;
            padding:14px 20px; border-radius:12px;
            box-shadow:0 8px 24px rgba(0,0,0,.15);
            display:flex; align-items:center; gap:10px;
            font-weight:600; font-size:.9rem;
            animation: slideUp .3s ease-out;
            max-width: 360px;
        ">
            <i class="fas ${icons[type]}"></i>
            <span>${message}</span>
            <button onclick="this.closest('.uqs-toast').remove()"
                style="background:none;border:none;color:rgba(255,255,255,.8);
                       cursor:pointer;margin-left:8px;font-size:1rem;">✕</button>
        </div>`;

    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

/* ── Auto-dismiss alerts ── */
document.addEventListener('DOMContentLoaded', function () {
    // Auto-dismiss Bootstrap alerts after 5s
    document.querySelectorAll('.alert-dismissible').forEach(alert => {
        setTimeout(() => {
            const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
            if (bsAlert) bsAlert.close();
        }, 5000);
    });

    // Add CSS animation keyframes
    if (!document.getElementById('uqs-anim')) {
        const style = document.createElement('style');
        style.id = 'uqs-anim';
        style.textContent = `
            @keyframes slideUp {
                from { opacity:0; transform:translateY(20px); }
                to   { opacity:1; transform:translateY(0); }
            }
        `;
        document.head.appendChild(style);
    }

    // Highlight active nav link based on current path
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        if (link.getAttribute('href') === path) {
            link.classList.add('active');
        }
    });

    // Password strength indicator (on register page)
    const pwdField = document.querySelector('input[name="password"]');
    if (pwdField && document.querySelector('form[action*="register"]')) {
        pwdField.addEventListener('input', function() {
            const val = this.value;
            let strength = 0;
            if (val.length >= 6)  strength++;
            if (val.length >= 10) strength++;
            if (/[A-Z]/.test(val)) strength++;
            if (/[0-9]/.test(val)) strength++;
            if (/[^A-Za-z0-9]/.test(val)) strength++;

            let existing = document.getElementById('pwdStrength');
            if (!existing) {
                existing = document.createElement('div');
                existing.id = 'pwdStrength';
                existing.className = 'mt-1';
                this.closest('.mb-3, .col-md-6').appendChild(existing);
            }

            const labels = ['', 'Very Weak', 'Weak', 'Fair', 'Strong', 'Very Strong'];
            const colors = ['', '#e63946', '#ff9f1c', '#f4d03f', '#2ecc71', '#1abc9c'];
            existing.innerHTML = strength > 0
                ? `<small style="color:${colors[strength]};font-weight:600;">
                     ${'▮'.repeat(strength)}${'▯'.repeat(5-strength)} ${labels[strength]}
                   </small>`
                : '';
        });
    }

    // Confirm dialogs for dangerous actions
    document.querySelectorAll('[data-confirm]').forEach(el => {
        el.addEventListener('click', function(e) {
            if (!confirm(this.dataset.confirm)) e.preventDefault();
        });
    });

    // Initialize tooltips
    document.querySelectorAll('[data-bs-toggle="tooltip"]').forEach(el => {
        new bootstrap.Tooltip(el);
    });
});

/* ── Auto-refresh countdown ── */
function startRefreshCountdown(seconds) {
    const bar = document.getElementById('refreshBar');
    if (!bar) return;
    let remaining = seconds;
    const interval = setInterval(() => {
        remaining--;
        const pct = (remaining / seconds * 100);
        bar.style.width = pct + '%';
        if (remaining <= 0) {
            clearInterval(interval);
        }
    }, 1000);
}

/* ── Queue position live update ── */
async function refreshQueueStatus(vendorId) {
    try {
        const res = await fetch(`/api/queue/status/${vendorId}`);
        if (!res.ok) return;
        const data = await res.json();
        const aheadEl = document.getElementById('peopleAhead');
        const etaEl   = document.getElementById('etaMinutes');
        const calledEl = document.getElementById('currentToken');
        if (aheadEl)  aheadEl.textContent = data.peopleAhead;
        if (etaEl)    etaEl.textContent   = data.etaMinutes;
        if (calledEl) calledEl.textContent = data.currentToken;
    } catch (e) {
        // Silently fail if API not available
    }
}

/* ── On page load ── */
window.addEventListener('load', function() {
    startRefreshCountdown(10);
});
