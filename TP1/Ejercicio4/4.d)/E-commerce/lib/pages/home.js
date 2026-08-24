//Imports
import { getAllProducts } from "../service/product.js";

//Instancias de elementos
const contenedor_panes  = document.getElementById("contenedor-panes")
const contenedor_centeno  = document.getElementById("contenedor-centeno")
const contenedor_integrales  = document.getElementById("contenedor-integrales")
const contenedor_focaccias  = document.getElementById("contenedor-focaccias")

const fillProducts = async ()=>{
    const products = await getAllProducts()

    products.forEach(product => {
        const category = product.category

        let container;

        if(category === "men's clothing"){
            container = contenedor_panes
        }else if (category === "jewelery"){
            container = contenedor_centeno
        }else if (category === "electronics"){
            container = contenedor_integrales 
        }else if (category === "women's clothing"){
            container = contenedor_focaccias
        }

        //crear elemento en la categori

        container.innerHTML += `
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
        `
    })

    console.log(products)

}
fillProducts()