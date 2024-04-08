function validateForm() {
    var email = document.getElementsByName("email")[0].value
    var confirm_email = document.getElementsByName("confirmEmail")[0].value

    if (email !== confirm_email) {
        alert("Emails do not match!")
        return false
    }

    return true
}
