const maxLength = 3000
const textInput = document.getElementById("textInput")
const charCount = document.getElementById("charCount")

textInput.addEventListener("input", function () {
    const remainingChars = maxLength - textInput.value.length
    charCount.textContent = `${remainingChars} / 3000`

    if (remainingChars < 0) {
        textInput.value = textInput.value.slice(0, maxLength)
        charCount.textContent = "Character limit reached"
    }
})
