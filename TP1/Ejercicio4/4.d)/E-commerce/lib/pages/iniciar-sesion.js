import { login } from "../service/user.js"

const form = document.getElementById("form")
const userInput = document.getElementById("user")
const passwordInput = document.getElementById("password")

form.addEventListener('submit', async(e)=>{
    e.preventDefault()
    const username = userInput.value
    const password = passwordInput.value

    if(username === '' || password===''){
        alert("Completa los campos")
        return
    }
    const res = await login({username, password})

    if (res.token) {
        alert("Inicio de sesion correcto.")
    }else{
        alert("Error al iniicar sesion")
    }

})