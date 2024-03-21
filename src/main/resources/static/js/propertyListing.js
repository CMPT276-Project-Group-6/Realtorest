// Getting data from database
// For now, just make up some data
const propertyData = [
    {
        id: 1,
        price: 100,
        bed: 4,
        bath: 2,
        area: 100,
        address: "8888 University Dr, Burnaby, BC V5A 1S6",
        images: ["Front.jpg", "Livingroom.jpeg", "Washroom.jpg"],
    },
    {
        id: 2,
        price: 150,
        bed: 3,
        bath: 2,
        area: 102,
        address: "13450 102 Ave #250, Surrey, BC V3T 0A3",
        images: ["Front.jpg", "Livingroom.jpg", "Washroom.jpg"],
    },
    {
        id: 3,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        address: "515 W Hastings St, Vancouver, BC V6B 5K3",
        images: ["Front.jpg", "Livingroom.jpeg", "Washroom.jpg"],
    },
    {
        id: 4,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        address: "800 Robson St, Vancouver, BC V6Z 2E7",
        images: ["Front.jpg", "Livingroom.jpg", "Washroom.jpg"],
    },
    {
        id: 5,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        address: "2329 West Mall, Vancouver, BC Canada V6T 1Z4",
        images: ["Front.jpg", "Livingroom.jpeg", "Washroom.jpg"],
    },
    {
        id: 6,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        address: "3333 University Way, Kelowna, BC Canada V1V 1V7",
        images: ["Front.jpg", "Livingroom.jpg", "Washroom.jpg"],
    },
]

// Load the property cards
document.addEventListener("DOMContentLoaded", function () {
    // Get the container element in HTML
    const propertyContainer = document.getElementById("property-container")

    // Add property cards to the HTML
    propertyData.forEach((property) => {
        const card = createPropertyCard(property)
        propertyContainer.appendChild(card)
    })
})

// Create the property card
function createPropertyCard(property) {
    // Create the figure element in HTML
    const figure = document.createElement("figure")
    figure.className = "figure"
    figure.innerHTML = `
        <!-- MODAL INFORMATION -->
        <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
            <div class="modal-header">
                <h1 class="modal-title fs-5" id="exampleModalLabel">Modal title</h1>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                ...
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-primary">Save changes</button>
            </div>
            </div>
        </div>
        </div>

        <!-- PROPERTY CARD -->
        <section id="carousel${property.id}" class="carousel slide">
            <section class="carousel-inner">
                <section class="carousel-item active">
                    <button class="favorite-button" onclick=""><i class="fas fa-star"></i></button>
                <img src="/css/Property${property.id}/${property.images[0]}" class="d-block w-100" alt="Front House">
                </section>
                <section class="carousel-item">
                <button class="favorite-button" onclick=""><i class="fas fa-star"></i></button>
                <img src="/css/Property${property.id}/${property.images[1]}" class="d-block w-100" alt="Living Room">
                </section>
                <section class="carousel-item">
                <button class="favorite-button" onclick=""><i class="fas fa-star"></i></button>
                <img src="/css/Property${property.id}/${property.images[2]}" class="d-block w-100" alt="Washroom">
                </section>
            </section>
            <button class="carousel-control-prev" type="button" data-bs-target="#carousel${property.id}" data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carousel${property.id}" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Next</span>
            </button>
        </section>

        <!-- PROPERTY INFO -->
        <figcaption class="figure-caption">
            <a href="">
                <section class="icon-container">
                    <p class="property-price">$${property.price}K</p>
                    <section class="icon-subcontainer">
                        <i class="fa-solid fa-bed"></i>
                        <p>${property.bed}</p>
                    </section>
                    <section class="icon-subcontainer">
                        <i class="fa-solid fa-bath"></i>
                        <p>${property.bath}</p>
                    </section>
                    <section class="icon-subcontainer">
                        <i class="fa-solid fa-house"></i>
                        <p>1${property.area} ft&sup2;</p>
                    </section>
                </section>
                <section class="address-container">
                    <i class="fa-solid fa-location-dot"></i>
                    <p class="property-address">${property.address}</p>
                </section>
            </a>
        </figcaption>
    `
    return figure
}