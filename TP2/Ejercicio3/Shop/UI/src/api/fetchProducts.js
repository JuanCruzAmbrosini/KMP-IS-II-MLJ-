import axios from "axios";
import { API_BASE_URL, API_URLS } from "./constant"


export const getAllProducts = async (id,typeId)=>{
    let url = API_BASE_URL + API_URLS.GET_PRODUCTS;
    const params = [];
    if(id){
        params.push(`categoryId=${encodeURIComponent(id)}`);
    }
    if(typeId){
        params.push(`typeId=${encodeURIComponent(typeId)}`);
    }
    if(params.length > 0){
        url = url + `?${params.join('&')}`;
    }

    try{
        const result = await axios(url,{
            method:"GET"
        });
        return result?.data || [];
    }
    catch(err){
        console.error(err);
        return [];
    }
}

export const getProductBySlug = async (slug)=>{
    const url = API_BASE_URL + API_URLS.GET_PRODUCTS + `?slug=${slug}`;
    try{
        const result = await axios(url,{
            method:"GET",
        });
        return result?.data?.[0];
    }
    catch(err){
        console.error(err);
    }
}