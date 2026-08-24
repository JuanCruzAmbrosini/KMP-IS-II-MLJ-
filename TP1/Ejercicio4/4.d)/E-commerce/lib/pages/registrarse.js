import { register } from "../service/user.js";

// Obtener referencia a los elementos del formulario
const form = document.getElementById("form");
const userInput = document.getElementById("user");
const nameInput = document.getElementById("name");
const lastnameInput = document.getElementById("lastname");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const _passwordInput = document.getElementById("_password");

// Función manejadora del submit del formulario
form.addEventListener("submit", async (e) => {
  // Prevenir submit por defecto
  e.preventDefault();

  // Obtener los valores ingresados
  const username = userInput.value;
  const firstname = nameInput.value;
  const lastname = lastnameInput.value;
  const email = emailInput.value;
  const password = passwordInput.value;
  const _password = _passwordInput.value;

  // Validaciones de campos vacíos
  if (
    username === "" ||
    firstname === "" ||
    lastname === "" ||
    email === "" ||
    password === "" ||
    _password === ""
  ) {
    alert("Rellena todos los campos");
    return;
  }

  // Validación de coincidencia de contraseñas
  if (password !== _password) {
    alert("Las contraseñas no son iguales");
    return;
  }

  // Si es válido, enviar petición al backend
  
    const res = await register({
      username,
      email,
      firstname,
      lastname,
      password,
    })
    if(res.id){
        alert("Registro exitoso")
    }else{
        alert("Error al registrar")
    }
    console.log("Respuesta del servidor:", res);

});