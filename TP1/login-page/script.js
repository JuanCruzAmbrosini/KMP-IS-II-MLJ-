const container = document.querySelector(".container");
const btnSingIn = document.getElementById("btn-sing-in");
const btnSingUp = document.getElementById("btn-sing-up");

btnSingIn.addEventListener("click", () => {
  container.classList.remove("toggle");
});

btnSingUp.addEventListener("click", () => {
  container.classList.add("toggle");
});
