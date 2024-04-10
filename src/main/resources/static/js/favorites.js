function toggleFavorite(button) {
    var propertyId = button.getAttribute("data-pid")
    var iconElement = button.querySelector("i")

    if (iconElement.classList.contains("highlighted")) {
        // Remove from favorites
        iconElement.classList.remove("highlighted")
        removeFavorite(propertyId)
        localStorage.removeItem(propertyId) // Remove from local storage
    } else {
        // Add to favorites
        iconElement.classList.add("highlighted")
        addFavorite(propertyId)
        localStorage.setItem(propertyId, true) // Add to local storage
    }
}

window.onload = function () {
    var favoriteButtons = document.querySelectorAll(".favorite-button")
    favoriteButtons.forEach(function (button) {
        var propertyId = button.getAttribute("data-pid")
        if (favoritePropertyIds.includes(parseInt(propertyId))) { // // Using the favoritePropertyIds from the server
            button.querySelector("i").classList.add("highlighted")
        }
    })
}

function viewFavorite() {
    fetch("/favorites", {
        method: "GET",
        credentials: "include", // This is required to include the session cookie in the request
    })
        .then((response) => response.json())
        .then((favoriteProperties) => {
            console.log(favoriteProperties)
        })
        .catch((error) => console.error("Error:", error))
}

function addFavorite(propertyId) {
    // Make AJAX POST request to add property to favorites
    fetch(`/add-favorite/${propertyId}`, {
        method: "POST",
        credentials: "include", // This is required to include the session cookie in the request
    })
        .then((response) => response.text())
        .then((message) => {
            console.log(message)
        })
        .catch((error) => console.error("Error:", error))
}

function removeFavorite(propertyId) {
    // Make AJAX DELETE request to remove property from favorites
    fetch(`/remove-favorite/${propertyId}`, {
        method: "DELETE",
        credentials: "include", // This is required to include the session cookie in the request
    })
        .then((response) => response.text())
        .then((message) => {
            console.log(message)
        })
        .catch((error) => console.error("Error:", error))
}
