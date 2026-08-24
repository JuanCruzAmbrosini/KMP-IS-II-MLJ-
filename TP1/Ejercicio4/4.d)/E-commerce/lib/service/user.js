export function login ({username, password}){
    return fetch('https://fakestoreapi.com/auth/login',{
        method:'POST',
        headers:{
            "Content-Type": "application/json"
        },
        body:JSON.stringify({
            username,
            password
        })
    })
        .then(res=>{

            if(!res.ok){
                console.log(res)
                throw new Error("usuario o contraseña incorrecta")
            }
            return res.json()
    })
    .catch(e=> e)
    
}   

export function register({ username, email, firstname, lastname, password }) {
  return fetch('https://fakestoreapi.com/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      email,
      username,
      password,
      name: {
        firstname,
        lastname,
      },
    }),
  })
    .then((res) => res.json())
    .then((json) => json)
    .catch(e=>e)
}