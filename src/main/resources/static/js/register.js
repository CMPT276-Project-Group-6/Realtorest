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

function checkPasswordStrength() {
    var password = document.getElementById("password").value
    var strengthBar = document.getElementById("password-strength")
    var strengthText = document.getElementById("password-strength-text")

    var passwordInput = document.getElementById("password")
    passwordInput.addEventListener("input", checkPasswordStrength)

    passwordInput.addEventListener("focus", function () {
        strengthBar.style.display = "block"
        strengthText.style.display = "block"
    })

    passwordInput.addEventListener("blur", function () {
        strengthBar.style.display = "none"
        strengthText.style.display = "none"
    })

    var strength = validatePasswordStrength(password)
    strengthBar.value = strength * 25 // Multiply by 25 to get a value out of 100

    switch (strength) {
        case 0:
            strengthText.innerHTML = "Password must be at least 8 characters long and<br>contains uppercase, lowercase, number, and special character."
            strengthText.style.color = "#a5b5bc"
            break
        case 1:
            strengthText.innerHTML = "Weak"
            strengthText.style.color = "#a5b5bc"
            break
        case 2:
            strengthText.innerHTML = "Medium"
            strengthText.style.color = "#a5b5bc"
            break
        case 3:
            strengthText.innerHTML = "Strong"
            strengthText.style.color = "#a5b5bc"
            break
        case 4:
            strengthText.innerHTML = "Very Strong"
            strengthText.style.color = "#a5b5bc"
            break
        default:
            break
    }
}

function validateRegistration() {
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
