// Getting data from database
// For now, just make up some data
const propertyData = [
    {
        id: 1,
        price: 100,
        bed: 4,
        bath: 2,
        area: 100,
        street: "8888 University Dr",
        city_province: "Burnaby, BC",
        zipcode: "V5A 1S6",
        images: ["Front.jpg", "Livingroom.jpeg", "Washroom.jpg"],
    },
    {
        id: 2,
        price: 150,
        bed: 3,
        bath: 2,
        area: 102,
        street: "13450 102 Ave #250",
        city_province: "Surrey, BC",
        zipcode: "V3T 0A3",
        images: ["Front.jpg", "Livingroom.jpg", "Washroom.jpg"],
    },
    {
        id: 3,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        street: "515 W Hastings St",
        city_province: "Vancouver, BC",
        zipcode: "V6B 5K3",
        images: ["Front.jpg", "Livingroom.jpeg", "Washroom.jpg"],
    },
    {
        id: 4,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        street: "800 Robson St",
        city_province: "Vancouver, BC",
        zipcode: "V6Z 2E7",
        images: ["Front.jpg", "Livingroom.jpg", "Washroom.jpg"],
    },
    {
        id: 5,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        street: "2329 West Mall",
        city_province: "Vancouver, BC Canada",
        zipcode: "V6T 1Z4",
        images: ["Front.jpg", "Livingroom.jpeg", "Washroom.jpg"],
    },
    {
        id: 6,
        price: 650,
        bed: 5,
        bath: 4,
        area: 259,
        street: "3333 University Way",
        city_province: "Kelowna, BC Canada",
        zipcode: "V1V 1V7",
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
        <section class="modal fade" id="modal${property.id}" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
            <section class="modal-dialog modal-xl modal-dialog-scrollable">
                <section class="modal-content">
                    <section class="modal-body">
                        <!-- CAROUSEL -->
                        <section id="modal-carousel${property.id}" class="carousel slide carousel-fade" data-bs-ride="carousel">
                            <section class="carousel-inner">
                                <section class="carousel-item active data-bs-interval="3000"">
                                    <img src="/img/Property${property.id}/${property.images[0]}" class="d-block w-100" alt="Front House">
                                </section>
                                <section class="carousel-item data-bs-interval="3000"">
                                    <img src="/img/Property${property.id}/${property.images[1]}" class="d-block w-100" alt="Living Room">
                                </section>
                                <section class="carousel-item data-bs-interval="3000"">
                                    <img src="/img/Property${property.id}/${property.images[2]}" class="d-block w-100" alt="Washroom">
                                </section>
                            </section>
                            <button class="carousel-control-prev" type="button" data-bs-target="#modal-carousel${property.id}" data-bs-slide="prev">
                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                <span class="visually-hidden">Previous</span>
                            </button>
                            <button class="carousel-control-next" type="button" data-bs-target="#modal-carousel${property.id}" data-bs-slide="next">
                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                <span class="visually-hidden">Next</span>
                            </button>
                        </section>

                        <!-- PROPERTY INFO -->
                        <section id="favourite-button" class="icon-subcontainer">
                            <button class="favorite-button" onclick=""><i class="fa-solid fa-bookmark"></i></button>
                            <p> Add to favourites</p>
                        </section>
                        <p class="location-address">${property.street} ${property.city_province} ${property.zipcode}</p>
                        <section class="location-icon-container">
                            <p class="property-price">$${property.price},000</p>
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
                        <section class="detailed-description">
                            <p>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sed eros fringilla, luctus quam ut, hendrerit mi. Donec hendrerit ligula cursus dui pretium mollis. Quisque commodo interdum sapien ut congue. Quisque egestas, libero sit amet ornare ornare, massa dolor sollicitudin elit, ut consequat ante urna sit amet nulla. Suspendisse eu mi sapien. Cras ullamcorper purus ipsum, sit amet eleifend ipsum imperdiet nec. Phasellus hendrerit, urna at feugiat dignissim, ante ligula convallis sapien, sit amet tristique enim turpis non nibh. Aenean non cursus sem. Quisque risus tellus, pellentesque sit amet ligula at, viverra finibus velit. Nunc non aliquet arcu, nec consectetur.<br><br>
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce sed eros fringilla, luctus quam ut, hendrerit mi. Donec hendrerit ligula cursus dui pretium mollis. Quisque commodo interdum sapien ut congue. Quisque egestas, libero sit amet ornare ornare, massa dolor sollicitudin elit, ut consequat ante urna sit amet nulla. Suspendisse eu mi sapien. Cras ullamcorper purus ipsum, sit amet eleifend ipsum imperdiet nec. Phasellus hendrerit, urna at feugiat dignissim, ante ligula convallis sapien, sit amet tristique enim turpis non nibh. Aenean non cursus sem. Quisque risus tellus, pellentesque sit amet ligula at, viverra finibus velit. Nunc non aliquet arcu, nec consectetur.
                            </p>                            
                        </section>
                        <section class="display-realtor">
                            <section class="profile-image">
                                <img src="/img/homepage/logo.png" alt="Profile Picture">
                            </section>
                            <section class="offcanvas-header">
                                <p class="playfair-regular client-name" id="offcanvasTopLabel">Aman Dhiman</p>
                                <p class="client-information" id="offcanvasTopLabel">Agent</p>  
                                <p class="client-information" id="offcanvasTopLabel">amandhiman@gmail.com</p>      
                                <p class="client-information" id="offcanvasTopLabel">604-123-4567</p>              
                                <section class="social-media">
                                    <a href="https://www.facebook.com"><i class="fa-brands fa-facebook fa-lg"></i></a>
                                    <a href="https://www.instagram.com/yyj.realtor?utm_source=ig_web_button_share_sheet&igsh=ZDNlZDc0MzIxNw=="><i class="fa-brands fa-instagram fa-lg"></i></a>
                                    <a href="https://twitter.com/?lang=en"><i class="fa-brands fa-x-twitter fa-lg"></i></a>
                                    <a href="https://www.youtube.com"><i class="fa-brands fa-youtube fa-lg"></i></a>
                                </section>
                            </section>
                        </section>
                    </section>
                </section>
            </section>
        </section>

        <!-- PROPERTY CARD -->
        <section id="carousel${property.id}" class="carousel slide carousel-fade">
            <section class="carousel-inner">
                <section class="carousel-item active">
                    <img src="/img/Property${property.id}/${property.images[0]}" class="d-block w-100" alt="Front House">
                </section>
                <section class="carousel-item">
                    <img src="/img/Property${property.id}/${property.images[1]}" class="d-block w-100" alt="Living Room">
                </section>
                <section class="carousel-item">
                    <img src="/img/Property${property.id}/${property.images[2]}" class="d-block w-100" alt="Washroom">
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
        <figcaption class="figure-caption" data-bs-toggle="modal" data-bs-target="#modal${property.id}">
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
                    <p class="property-address">${property.street}, ${property.city_province} ${property.zipcode}</p>
                </section>
        </figcaption>
    `
    return figure
}
