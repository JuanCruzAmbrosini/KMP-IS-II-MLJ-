submitButton = document.querySelector(".formButton")
list = document.querySelector(".list")

speedGifs = [
    "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcnhrcXN5bW5yd2lmdXg4MWxnMGg4cHVrcDFmMHA2MDRpMDl3dTI2aiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/lxxOGaDRk4f7R5TkBd/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaHRudnZ6ZTFnOXliejg2NTljdnIxaGtpcW1xNGwxNmI3dnUwc2d3NiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/s5wFafpHxqKbIEERl9/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaHRudnZ6ZTFnOXliejg2NTljdnIxaGtpcW1xNGwxNmI3dnUwc2d3NiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/D63HGAzG15LQrjBPRE/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExaHRudnZ6ZTFnOXliejg2NTljdnIxaGtpcW1xNGwxNmI3dnUwc2d3NiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/fkAj7cN0OEuHXOXFoQ/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3Y3lwaDF6a3hjZ3psbjRkbHEzMm04Z2ZoYWFvemE2eHYxc2NqZmd6diZlcD12MV9naWZzX3NlYXJjaCZjdD1n/G3uXJGdnIEQ9sFcxTR/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3Y3lwaDF6a3hjZ3psbjRkbHEzMm04Z2ZoYWFvemE2eHYxc2NqZmd6diZlcD12MV9naWZzX3NlYXJjaCZjdD1n/Ld6YQCrqoWzk70ykXH/giphy.gif",
    "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3ZGdxb3AzOTB1b2VvcnF1MHFxZG5mYTI3YWR3M3pscmlsOTFvbmdlbCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/oPvWTJmebjD0KqjUnT/giphy.gif"
    ]

function getRandomGif() {
    const randomIndex = Math.floor(Math.random() * speedGifs.length)
    return speedGifs[randomIndex]
}

function createListItem(name, course) {
    const listItem = document.createElement("li")
    const nameSpan = document.createElement("span")
    const courseSpan = document.createElement("span")
    const listItemTextSection = document.createElement("div")
    const listItemButtonSection = document.createElement("div")
    nameSpan.textContent = "Nombre: " + name
    courseSpan.textContent = "Carrera: " + course
    const listItemEditButton = document.createElement("button")
    const listItemDeleteButton = document.createElement("button")
    const randomGif = getRandomGif()
    listItemEditButton.textContent = "Editar"
    listItemDeleteButton.textContent = "Eliminar"
    listItem.className = "list-item"
    listItemButtonSection.className = "list-item-button-section"
    listItemEditButton.className = "list-item-edit-button"
    listItemDeleteButton.className = "list-item-delete-button"
    listItemTextSection.className = "list-item-text-section"
    listItem.appendChild(listItemTextSection)
    listItem.appendChild(listItemButtonSection)
    listItemTextSection.appendChild(nameSpan)
    listItemTextSection.appendChild(courseSpan)
    listItemButtonSection.appendChild(document.createElement("img")).src = randomGif
    listItemButtonSection.appendChild(listItemEditButton)
    listItemButtonSection.appendChild(listItemDeleteButton)
    return listItem
}

// Cargar un alumno
submitButton.addEventListener("click", function(event) {
    event.preventDefault()
    const nameInput = document.querySelector(".formInput.name")
    const courseInput = document.querySelector(".formInput.course")
    const name = nameInput.value.trim()
    const course = courseInput.value.trim()
    if (name && course) {
        const listItem = createListItem(name, course)
        list.appendChild(listItem)
        nameInput.value = ""
        courseInput.value = ""
    }
})

// Editar o eliminar un alumno
list.addEventListener("click", function(event) {
    const button = event.target
    const listItem = button.closest(".list-item")

    if (!listItem) {
        return
    }

    // Eliminar un alumno
    if (button.classList.contains("list-item-delete-button")) {
        list.removeChild(listItem)
        return
    }

    // Editar un alumno
    if (button.classList.contains("list-item-edit-button")) {
        const textSection = listItem.querySelector(".list-item-text-section")
        const nameSpan = textSection.children[0]
        const courseSpan = textSection.children[1]
        const nameInput = document.createElement("input")
        const courseInput = document.createElement("input")

        nameInput.type = "text"
        nameInput.value = nameSpan.textContent.replace("Nombre: ", "")
        courseInput.type = "text"
        courseInput.value = courseSpan.textContent.replace("Carrera: ", "")

        textSection.replaceChild(nameInput, nameSpan)
        textSection.replaceChild(courseInput, courseSpan)

        const deleteButton = listItem.querySelector(".list-item-delete-button")
        if (deleteButton) {
            deleteButton.remove()
        }

        button.textContent = "Guardar"
        button.classList.remove("list-item-edit-button")
        button.classList.add("list-item-save-button")
        return
    }

    // Confirmar la edición de un alumno
    if (button.classList.contains("list-item-save-button")) {
        const inputs = listItem.querySelectorAll(".list-item-text-section input")
        const nameInput = inputs[0]
        const courseInput = inputs[1]
        const name = nameInput.value.trim()
        const course = courseInput.value.trim()

        if (name && course) {
            const textSection = listItem.querySelector(".list-item-text-section")
            const nameSpan = document.createElement("span")
            const courseSpan = document.createElement("span")
            nameSpan.textContent = "Nombre: " + name
            courseSpan.textContent = "Carrera: " + course
            textSection.replaceChild(nameSpan, nameInput)
            textSection.replaceChild(courseSpan, courseInput)

            button.textContent = "Editar"
            button.classList.remove("list-item-save-button")
            button.classList.add("list-item-edit-button")

            const deleteButton = document.createElement("button")
            deleteButton.textContent = "Eliminar"
            deleteButton.classList.add("list-item-delete-button")
            listItem.querySelector(".list-item-button-section").appendChild(deleteButton)
        }
    }
})
