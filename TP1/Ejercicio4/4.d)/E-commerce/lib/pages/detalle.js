import { getOneProduct, getProductInCategory } from "../service/product.js"

const id = new URLSearchParams(window.location.search).get('id')

//Inicializar 

    const product_image  = document.getElementById("product-image")
    const product_title  = document.getElementById("product-title")
    const product_price  = document.getElementById("product-price")
    const product_description  = document.getElementById("product-description")

    const productosRelacionadosContainer = document.getElementById('productos-relacionados')


const fillDetailProduct =  async ()=>{

    const product = await getOneProduct(id)
    
    if(product){
        if (product) {
            if (product_image) product_image.src = product.image
            if (product_title) product_title.textContent = product.title
            if (product_price) product_price.textContent = `$${product.price}`
            if (product_description) product_description.textContent = product.description
            } 
        fillProductosRelacionados(product.category)
    }

}

const fillProductosRelacionados = async (category)=>{
    const products = await getProductInCategory(category)

    products.forEach(product => {
        //crear elemento en la categori 
        productosRelacionadosContainer.innerHTML += `
        <div class="col">
                        <div class="card h-100">
                            <img 
                            class="card-img-top"
                            src="${product.image}" 
                            alt=""
                            >
                            <div class="card-body p-4">
                                <div class="text-center">
                                    <h5 class="fw-bolder">${product.title}</h5>
                                    <span>$${product.price}</span>
                                </div>
                            </div>
                            <div class="card-footer p-4 pt-0 border-top-0 bg-transparent">
                                <div class="text-center d-flex gap-2 justify-content-center">
                                    <a href="/detalle.html?id=${product.id}" class="btn btn-outline-secondary mt-auto">
                                        Ver más
                                    </a>
                                    <a href="" class="btn btn-outline-success mt-auto">
                                        Añadir al carrito
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
        `;
    });

}


document.addEventListener("DOMContentLoaded", fillDetailProduct)