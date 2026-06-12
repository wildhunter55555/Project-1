let currentUser = null;

document.addEventListener('DOMContentLoaded', () => {
    // 1. Check who is logged in before doing anything else
    checkAuthentication();

    // 2. Handle the form submission
    document.getElementById('assignTaskForm').addEventListener('submit', function(event) {
        event.preventDefault();
        assignTask();
    });
});

function checkAuthentication() {
    fetch('/api/users/me')
        .then(response => {
            if (!response.ok) {
                // If not logged in, redirect to login page
                window.location.href = '/login.html';
                throw new Error('Not authenticated');
            }
            return response.json();
        })
        .then(user => {
            currentUser = user;

            // Security check: Route staff members away from the admin page
            if (user.role !== 'ROLE_ADMIN') {
                alert('Access Denied: You do not have admin privileges.');
                window.location.href = '/staff.html';
                return;
            }

            // If they are an admin, proceed with loading the page data
            loadAvailableStaff();
        })
        .catch(error => console.error('Authentication error:', error));
}

// ... Keep your existing loadAvailableStaff() and assignTask() functions exactly as they are ...

// 1. Fetch available staff from the database
function loadAvailableStaff() {
    fetch('http://localhost:8080/api/users/available')
        .then(response => response.json())
        .then(staffList => {
            const staffSelect = document.getElementById('staffSelect');
            staffList.forEach(staff => {
                const option = document.createElement('option');
                option.value = staff.id; // We need the ID to send to the backend
                option.textContent = staff.fullName;
                staffSelect.appendChild(option);
            });
        })
        .catch(error => console.error('Error fetching staff:', error));
}

// 2. Send the new task data to the backend
function assignTask() {
    // Gather data from the form
    const staffId = document.getElementById('staffSelect').value;
    const adminNotes = document.getElementById('adminNotes').value;

    // Create the Task object
    const taskData = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        location: document.getElementById('location').value,
        deadline: document.getElementById('deadline').value
    };

    // Construct the URL with query parameters for staffId and adminNotes
    const url = `http://localhost:8080/api/tasks/assign?staffId=${staffId}&adminNotes=${encodeURIComponent(adminNotes)}`;

    // Send the POST request
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(taskData) // Convert Java object structure to JSON
    })
    .then(response => {
        if (response.ok) {
            alert('Task successfully assigned!');
            document.getElementById('assignTaskForm').reset(); // Clear the form
        } else {
            alert('Failed to assign task. Please check the console.');
        }
    })
    .catch(error => console.error('Error assigning task:', error));
}