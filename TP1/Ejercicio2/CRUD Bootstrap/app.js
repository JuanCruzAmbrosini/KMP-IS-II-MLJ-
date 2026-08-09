// Selecciona el formulario y los campos del modal
var form = document.getElementById("myForm"),
    imgInput = document.querySelector(".img"),
    file = document.getElementById("imgInput"),
    userName = document.getElementById("name"),
    age = document.getElementById("age"),
    city = document.getElementById("city"),
    email = document.getElementById("email"),
    phone = document.getElementById("phone"),
    post = document.getElementById("post"),
    sDate = document.getElementById("sDate"),
    submitBtn = document.querySelector(".submit"),
    userInfo = document.getElementById("data"),
    modal = document.getElementById("userForm"),
    modalTitle = document.querySelector("#userForm .modal-title"),
    newUserBtn = document.querySelector(".newUser")

// Carga usuarios desde localStorage o crea un array vacío
let getData = localStorage.getItem('userProfile') ? JSON.parse(localStorage.getItem('userProfile')) : [];

let isEdit = false, editId

// Al hacer clic en "New User" abre el modal y resetea el formulario
newUserBtn.addEventListener('click', ()=> {
    // prepara el modal para crear un usuario nuevo
    submitBtn.innerText = 'Submit';
    modalTitle.innerText = "Fill the Form";
    isEdit = false;
    imgInput.src = "ProfileIcon.webp";
    form.reset();
})

// Cuando se selecciona un archivo, se previsualiza en el modal (FileReader)
file.onchange = function(){
    // comprueba que haya archivos antes de acceder
    if(file.files && file.files[0]){
        if(file.files[0].size < 1000000){  // 1MB = 1000000, si es mayor, muestra alerta
            var filereader = new FileReader();

            filereader.onload = function(e){ // cuando se carga el archivo, se asigna la URL al src de la imagen
                imgUrl = e.target.result;
                imgInput.src = imgUrl;
            }
            //filereader lo que hace es leer el archivo y convertirlo en una URL de datos (Data URL) para poder mostrarlo en la imagen  
            filereader.readAsDataURL(file.files[0]); // lee el archivo como Data URL para poder mostrarlo en la imagen
        } else {
            alert("El archivo es demasiado grande");
        }
    }
}

//  showInfo() se llama al final del script para renderizar la tabla con los datos de `getData` al cargar la página.
//  Esto asegura que cualquier usuario previamente guardado en localStorage se muestre inmediatamente en la tabla.

// Renderiza la tabla con los datos de `getData`
function showInfo(){
    document.querySelectorAll('.employeeDetails').forEach(info => info.remove())
    getData.forEach((element, index) => {
        let createElement = `<tr class="employeeDetails">
            <td>${index+1}</td>
            <td><img src="${element.picture}" alt="" width="50" height="50"></td>
            <td>${element.employeeName}</td>
            <td>${element.employeeAge}</td>
            <td>${element.employeeCity}</td>
            <td>${element.employeeEmail}</td>
            <td>${element.employeePhone}</td>
            <td>${element.employeePost}</td>
            <td>${element.startDate}</td>


            <td>
                <button class="btn btn-success" onclick="readInfo('${element.picture}', '${element.employeeName}', '${element.employeeAge}', '${element.employeeCity}', '${element.employeeEmail}', '${element.employeePhone}', '${element.employeePost}', '${element.startDate}')" data-bs-toggle="modal" data-bs-target="#readData"><i class="bi bi-eye"></i></button>

                <button class="btn btn-primary" onclick="editInfo(${index}, '${element.picture}', '${element.employeeName}', '${element.employeeAge}', '${element.employeeCity}', '${element.employeeEmail}', '${element.employeePhone}', '${element.employeePost}', '${element.startDate}')" data-bs-toggle="modal" data-bs-target="#userForm"><i class="bi bi-pencil-square"></i></button>

                <button class="btn btn-danger" onclick="deleteInfo(${index})"><i class="bi bi-trash"></i></button>
            </td>
        </tr>`;

        userInfo.innerHTML += createElement;
    })
}

showInfo()

function readInfo(pic,name,age,city,email,phone,post,sDate){
    // muestra datos en el modal de solo lectura
    document.querySelector(".showImg").src = pic;
    document.getElementById("showName").value = name;
    document.getElementById("showAge").value = age;
    document.getElementById("showCity").value = city;
    document.getElementById("showEmail").value = email;
    document.getElementById("showPhone").value = phone;
    document.getElementById("showPost").value = post;
    document.getElementById("showsDate").value = sDate;
}

function editInfo(index, pic, name, Age, City, Email, Phone, Post, Sdate){
    isEdit = true
    editId = index
    imgInput.src = pic
    userName.value = name
    age.value = Age
    city.value =City
    email.value = Email,
    phone.value = Phone,
    post.value = Post,
    sDate.value = Sdate
    submitBtn.innerText = "Update"
    modalTitle.innerText = "Update The Form"
}

function deleteInfo(index){
    if(confirm("¿Seguro que quieres eliminar este registro?")){
        getData.splice(index, 1);
        localStorage.setItem("userProfile", JSON.stringify(getData));
        showInfo();
    }
}

// Evento submit: crea o edita un usuario y guarda en localStorage
form.addEventListener('submit', (e)=>{
    e.preventDefault()

    const information = {
        picture : imgInput.src == undefined ? "ProfileIcon.webp" : imgInput.src,
        employeeName : userName.value,
        employeeAge : age.value,
        employeeCity : city.value,
        employeeEmail : email.value,
        employeePhone : phone.value,
        employeePost : post.value,
        // usa la clave `startDate` que espera `showInfo()`
        startDate : sDate.value
    }

    if(!isEdit){
        // añadir nuevo
        getData.push(information);
    }else{
        // reemplazar existente
        isEdit = false;
        getData[editId] = information;
    }

    // guarda y actualiza la tabla
    localStorage.setItem('userProfile', JSON.stringify(getData));
    submitBtn.value = "Submit";
    modalTitle.innerHTML = "Fill The Form";

    showInfo();
    form.reset();

    imgInput.src = "ProfileIcon.webp";
})

// Inicializa la UI llamando a showInfo una vez
showInfo();
