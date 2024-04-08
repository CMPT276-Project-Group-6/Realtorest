let initialValue = {} // Store initial value of inputs

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("setting-form")
    const saveButton = document.getElementById("save-button")
    const passwordField = document.querySelector('input[name="password"]')
    const confirmPasswordField = document.querySelector('input[name="confirmPassword"]')
    const inputs = form.querySelectorAll("input")

    inputs.forEach((input) => {
        initialValue[input.name] = input.type === "checkbox" ? input.checked : input.value
    })

    // Function to check if the form is modified
    const isFormModified = () => {
        for (const input of inputs) {
            if (input.type === "checkbox" && input.checked !== initialValue[input.name]) {
                return true
            }
            if (input.type !== "checkbox" && input.value !== initialValue[input.name]) {
                return true
            }
        }
        return false
    }

    // Function to enable/disable save button based on form modification
    const updateSaveButtonState = () => {
        saveButton.disabled = !isFormModified()
    }

    // Function to hide confirm password field if passwords match
    const checkPasswordsMatch = () => {
        if (passwordField.value === initialValue["password"]) {
            confirmPasswordField.parentNode.hidden = true
        } else {
            confirmPasswordField.parentNode.hidden = false
        }
    }

    // Listen for input changes
    inputs.forEach((input) => {
        input.addEventListener("input", () => {
            updateSaveButtonState()
            if (input.name === "password") {
                checkPasswordsMatch()
            }
        })
    })

    // Initial state of the save button
    updateSaveButtonState()

    // Greeting message
    var today = new Date()
    var hour = today.getHours()

    var greeting
    if (hour >= 5 && hour < 12) {
        greeting = "Good morning, "
    } else if (hour >= 12 && hour < 18) {
        greeting = "Good afternoon,"
    } else {
        greeting = "Good evening,"
    }

    document.getElementById("greeting-user").innerHTML = greeting
})

// Password Strength Checker
function validateForm() {
    var password = document.getElementById("password").value
    var confirm_password = document.getElementById("confirm_password").value

    if (password !== confirm_password) {
        alert("Passwords do not match!")
        return false
    }

    return true
}

function validatePasswordStrength(password) {
    var minLength = password.length >= 8
    var upperCase = /[A-Z]/.test(password)
    var lowerCase = /[a-z]/.test(password)
    var number = /[0-9]/.test(password)
    var specialCharacter = /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]+/.test(password)

    // Calculate strength score
    var strength = 0
    if (minLength) strength++
    if (upperCase) strength++
    if (lowerCase) strength++
    if (number) strength++
    if (specialCharacter) strength++

    return strength
}

function validateRegistration() {
    // If password has not been changed, skip validation and allow form submission
    var password = document.getElementById("password").value
    // console.log("password" + password)
    // console.log("initialValue[password]" + initialValue["password"])
    if (password === initialValue["password"]) {
        return true
    }

    if (!validateForm()) {
        return false // Prevent form submission
    }

    var password = document.getElementById("password").value
    var strength = validatePasswordStrength(password)

    if (strength < 4) {
        alert("Please enter a stronger password. Password must be at least 8 characters long and contains uppercase, lowercase, number, and special character.")
        return false // Prevent form submission
    }

    // Proceed with form submission
    return true
}
