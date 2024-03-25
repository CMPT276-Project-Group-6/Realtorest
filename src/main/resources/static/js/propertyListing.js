// Get all property elements
const properties = document.querySelectorAll('.property-container');
const itemsPerPage = 9;
let currentPage = 1;

// Function to show properties for the current page
function displayProperties() {
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;

    properties.forEach((property, index) => {
        if (index >= startIndex && index < endIndex) {
            property.style.display = 'block';
        } else {
            property.style.display = 'none';
        }
    });
}

// Function to update pagination links
function updatePagination() {
    const totalPages = Math.ceil(properties.length / itemsPerPage);
    const paginationElement = document.getElementById('pagination');
    paginationElement.innerHTML = '';

    // Previous page link
    const prevPageElement = document.createElement('li');
    prevPageElement.classList.add('page-item');
    if (currentPage === 1) {
        prevPageElement.classList.add('disabled');
    }
    prevPageElement.innerHTML = `
        <a class="page-link" aria-label="Previous" onclick="prevPage()">
            <span aria-hidden="true">&laquo;</span>
        </a>
    `;
    paginationElement.appendChild(prevPageElement);

    // Sequential page links
    for (let i = currentPage - 2; i <= currentPage + 2; i++) {
        if (i >= 1 && i <= totalPages) {
            const pageElement = document.createElement('li');
            pageElement.classList.add('page-item');
            if (i === currentPage) {
                pageElement.classList.add('active');
            }
            pageElement.innerHTML = `
                <a class="page-link" onclick="goToPage(${i})">${i}</a>
            `;
            paginationElement.appendChild(pageElement);
        }
    }

    // Next page link
    const nextPageElement = document.createElement('li');
    nextPageElement.classList.add('page-item');
    if (currentPage === totalPages) {
        nextPageElement.classList.add('disabled');
    }
    nextPageElement.innerHTML = `
        <a class="page-link" aria-label="Next" onclick="nextPage()">
            <span aria-hidden="true">&raquo;</span>
        </a>
    `;
    paginationElement.appendChild(nextPageElement);
}

// Function to go to the previous page
function prevPage() {
    if (currentPage > 1) {
        currentPage--;
        displayProperties();
        updatePagination();
    }
}

// Function to go to the next page
function nextPage() {
    const totalPages = Math.ceil(properties.length / itemsPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        displayProperties();
        updatePagination();
    }
}

// Function to go to a specific page
function goToPage(page) {
    currentPage = page;
    displayProperties();
    updatePagination();
}

// Initial display and pagination setup
displayProperties();
updatePagination();