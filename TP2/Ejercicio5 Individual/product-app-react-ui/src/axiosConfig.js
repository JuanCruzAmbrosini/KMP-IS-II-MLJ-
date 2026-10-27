import axios from "axios";

const axiosInstance = axios.create({
    headers: {
        'Content-Type': 'application/json'
    }
});

axiosInstance.interceptors.request.use(
    (config) => {
        const username = localStorage.getItem('apiUsername');
        const password = localStorage.getItem('apiPassword');
        if(username && password){
            config.auth = {
                username: username,
                password: password
            };
        }
        return config;
    },
    (error) => {
        return Promise.reject(error)
    }
);

export default axiosInstance