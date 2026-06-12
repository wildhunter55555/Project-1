let currentUser = null;

document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication();
});

function checkAuthentication() {
    fetch('/api/users/me')
        .then(response => {
            if (!response.ok) {
                window.location.href = '/login.html';
                throw new Error('Not authenticated');
            }
            return response.json();
        })
        .then(user => {
            currentUser = user;
            updateAttendanceUI(user.available);
            loadMyTasks(user.id); // Securely loads tasks for this exact user
        })
        .catch(error => console.error('Authentication error:', error));
}

function loadMyTasks(staffId) {
    fetch(`/api/tasks/my-tasks?staffId=${staffId}`)
        .then(response => response.json())
        .then(assignments => {
            const tbody = document.getElementById('taskTableBody');
            tbody.innerHTML = '';

            if (assignments.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No tasks assigned to you right now.</td></tr>';
                return;
            }

            assignments.forEach(assignment => {
                const task = assignment.task;
                const tr = document.createElement('tr');

                tr.innerHTML = `
                    <td><strong>${task.title}</strong><br><small>${task.description}</small></td>
                    <td>${task.location}</td>
                    <td>${task.deadline || 'No Deadline'}</td>
                    <td>${assignment.adminNotes || ''}</td>
                    <td><strong>${task.status}</strong></td>
                    <td>
                        <select id="statusSelect-${task.id}">
                            <option value="PENDING" ${task.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="IN_PROGRESS" ${task.status === 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                            <option value="COMPLETED" ${task.status === 'COMPLETED' ? 'selected' : ''}>Completed</option>
                        </select>
                        <button onclick="updateTaskStatus(${task.id})">Update</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => console.error('Error fetching tasks:', error));
}

function updateTaskStatus(taskId) {
    const newStatus = document.getElementById(`statusSelect-${taskId}`).value;

    fetch(`/api/tasks/${taskId}/status?newStatus=${newStatus}`, { method: 'PUT' })
    .then(response => {
        if (response.ok) {
            alert('Task status updated successfully!');
            loadMyTasks(currentUser.id); // Refresh the table securely
        } else {
            alert('Failed to update status.');
        }
    })
    .catch(error => console.error('Error updating status:', error));
}

function updateAttendanceUI(isAvailable) {
    const statusText = document.getElementById('availabilityStatus');
    const clockInBtn = document.getElementById('clockInBtn');
    const clockOutBtn = document.getElementById('clockOutBtn');

    if (isAvailable) {
        statusText.textContent = "Clocked In (Available for tasks)";
        statusText.style.color = "green";
        clockInBtn.disabled = true;
        clockInBtn.style.opacity = "0.5";
        clockOutBtn.disabled = false;
        clockOutBtn.style.opacity = "1";
    } else {
        statusText.textContent = "Clocked Out (Unavailable)";
        statusText.style.color = "red";
        clockInBtn.disabled = false;
        clockInBtn.style.opacity = "1";
        clockOutBtn.disabled = true;
        clockOutBtn.style.opacity = "0.5";
    }
}

function clockIn() {
    fetch('/api/attendance/clock-in', { method: 'POST' })
    .then(response => {
        if (response.ok) {
            alert('You have successfully clocked in!');
            updateAttendanceUI(true);
        } else {
            alert('Failed to clock in. Please try again.');
        }
    })
    .catch(error => console.error('Error clocking in:', error));
}

function clockOut() {
    fetch('/api/attendance/clock-out', { method: 'PUT' })
    .then(response => {
        if (response.ok) {
            alert('You have successfully clocked out!');
            updateAttendanceUI(false);
        } else {
            alert('Failed to clock out. Please try again.');
        }
    })
    .catch(error => console.error('Error clocking out:', error));
}