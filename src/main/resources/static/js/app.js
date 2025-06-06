// Load configurations
async function loadConfigs() {
    const response = await fetch('/api/automation/configs');
    const configs = await response.json();
    
    const container = document.getElementById('configsList');
    container.innerHTML = '';
    
    configs.forEach(config => {
        const card = `
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${config.name}</h5>
                        <p class="card-text">${config.description || 'No description'}</p>
                        <p class="card-text">
                            <small class="text-muted">
                                Status: ${config.active ? 'Active' : 'Inactive'}
                            </small>
                        </p>
                        <button class="btn btn-sm btn-primary" onclick="runNow(${config.id})">Run Now</button>
                        <button class="btn btn-sm btn-secondary" onclick="toggleActive(${config.id})">
                            ${config.active ? 'Deactivate' : 'Activate'}
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteConfig(${config.id})">Delete</button>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Load history
async function loadHistory() {
    const response = await fetch('/api/history');
    const data = await response.json();
    
    const tbody = document.getElementById('historyTable');
    tbody.innerHTML = '';
    
    data.content.forEach(result => {
        const duration = result.endTime ? 
            Math.round((new Date(result.endTime) - new Date(result.startTime)) / 1000) + 's' : 
            'Running...';
        
        const row = `
            <tr>
                <td>${result.config.name}</td>
                <td>
                    <span class="badge bg-${getStatusColor(result.status)}">
                        ${result.status}
                    </span>
                </td>
                <td>${new Date(result.startTime).toLocaleString()}</td>
                <td>${duration}</td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="viewDetails(${result.id})">Details</button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
}

// Helper functions
function getStatusColor(status) {
    switch(status) {
        case 'SUCCESS': return 'success';
        case 'FAILED': return 'danger';
        case 'RUNNING': return 'primary';
        default: return 'secondary';
    }
}

async function runNow(configId) {
    const response = await fetch(`/api/automation/configs/${configId}/run`, { method: 'POST' });
    if (response.ok) {
        alert('Automation started!');
        loadHistory();
    }
}

async function toggleActive(configId) {
    const response = await fetch(`/api/automation/configs/${configId}/toggle`, { method: 'POST' });
    if (response.ok) {
        loadConfigs();
    }
}

async function deleteConfig(configId) {
    if (confirm('Are you sure you want to delete this configuration?')) {
        const response = await fetch(`/api/automation/configs/${configId}`, { method: 'DELETE' });
        if (response.ok) {
            loadConfigs();
        }
    }
}

// Step management
let stepCount = 0;

function addStep() {
    const container = document.getElementById('stepsContainer');
    const stepHtml = `
        <div class="card mb-2" id="step-${stepCount}">
            <div class="card-body">
                <div class="row">
                    <div class="col-md-3">
                        <select class="form-select step-type" onchange="updateStepFields(${stepCount})">
                            <option value="NAVIGATE">Navigate</option>
                            <option value="CLICK">Click</option>
                            <option value="INPUT">Input</option>
                            <option value="WAIT">Wait</option>
                            <option value="SCREENSHOT">Screenshot</option>
                            <option value="SCROLL">Scroll</option>
                            <option value="SELECT">Select</option>
                        </select>
                    </div>
                    <div class="col-md-8" id="stepFields-${stepCount}">
                        <input type="text" class="form-control" placeholder="URL" data-field="value">
                    </div>
                    <div class="col-md-1">
                        <button class="btn btn-sm btn-danger" onclick="removeStep(${stepCount})">X</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    container.innerHTML += stepHtml;
    stepCount++;
}

function updateStepFields(stepId) {
    const stepType = document.querySelector(`#step-${stepId} .step-type`).value;
    const fieldsContainer = document.getElementById(`stepFields-${stepId}`);
    
    let fields = '';
    switch(stepType) {
        case 'NAVIGATE':
            fields = '<input type="text" class="form-control" placeholder="URL" data-field="value">';
            break;
        case 'CLICK':
            fields = '<input type="text" class="form-control" placeholder="CSS Selector" data-field="selector">';
            break;
        case 'INPUT':
            fields = `
                <input type="text" class="form-control mb-2" placeholder="CSS Selector" data-field="selector">
                <input type="text" class="form-control" placeholder="Text to input" data-field="value">
            `;
            break;
        case 'WAIT':
            fields = '<input type="number" class="form-control" placeholder="Seconds" data-field="waitSeconds">';
            break;
        case 'SCREENSHOT':
            fields = '<input type="text" class="form-control" placeholder="CSS Selector (optional)" data-field="captureSelector">';
            break;
        case 'SCROLL':
            fields = '<input type="number" class="form-control" placeholder="Scroll position (pixels)" data-field="value">';
            break;
        case 'SELECT':
            fields = `
                <input type="text" class="form-control mb-2" placeholder="CSS Selector" data-field="selector">
                <input type="text" class="form-control" placeholder="Option value" data-field="value">
            `;
            break;
    }
    fieldsContainer.innerHTML = fields;
}

function removeStep(stepId) {
    document.getElementById(`step-${stepId}`).remove();
}

// Schedule fields
function updateScheduleFields() {
    const scheduleType = document.getElementById('scheduleType').value;
    const container = document.getElementById('scheduleFields');
    
    let fields = '';
    switch(scheduleType) {
        case 'ONCE':
            fields = '<input type="datetime-local" class="form-control" id="runOnceAt">';
            break;
        case 'INTERVAL':
            fields = '<input type="number" class="form-control" id="intervalMinutes" placeholder="Minutes">';
            break;
        case 'CRON':
            fields = '<input type="text" class="form-control" id="cronExpression" placeholder="0 0 * * * ?">';
            break;
    }
    container.innerHTML = fields;
}

// Form submission
document.getElementById('configForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const steps = [];
    document.querySelectorAll('[id^="step-"]').forEach((stepDiv, index) => {
        const type = stepDiv.querySelector('.step-type').value;
        const step = {
            order: index,
            type: type
        };
        
        stepDiv.querySelectorAll('[data-field]').forEach(input => {
            step[input.dataset.field] = input.value;
        });
        
        steps.push(step);
    });
    
    const scheduleType = document.getElementById('scheduleType').value;
    let schedule = null;
    if (scheduleType) {
        schedule = { type: scheduleType };
        switch(scheduleType) {
            case 'ONCE':
                schedule.runOnceAt = document.getElementById('runOnceAt').value;
                break;
            case 'INTERVAL':
                schedule.intervalMinutes = parseInt(document.getElementById('intervalMinutes').value);
                break;
            case 'CRON':
                schedule.cronExpression = document.getElementById('cronExpression').value;
                break;
        }
    }
    
    const config = {
        name: document.getElementById('configName').value,
        description: document.getElementById('configDescription').value,
        steps: steps,
        schedule: schedule,
        active: true
    };
    
    const response = await fetch('/api/automation/configs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(config)
    });
    
    if (response.ok) {
        alert('Configuration created successfully!');
        document.getElementById('configForm').reset();
        document.getElementById('stepsContainer').innerHTML = '';
        document.getElementById('scheduleFields').innerHTML = '';
        loadConfigs();
    }
});

// Initial load
loadConfigs();
loadHistory();

// Auto-refresh history every 10 seconds
setInterval(loadHistory, 10000); 