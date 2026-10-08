import axios from 'axios'

export const ObtenerClima = async (ciudad, setClima, setError, setCargando) => {
    if (!ciudad || ciudad.trim() === '') {
        if (setError) setError('Por favor, ingresa el nombre de una ciudad.')
        return
    }

    if (setCargando) setCargando(true)
    if (setError) setError(null)

    const ciudadLimpia = encodeURIComponent(ciudad.trim())
    let data = null
    let errorCapturado = null

    // 1. Probar mediante el proxy de Vite (/api)
    try {
        const response = await axios.get(`/api/ciudad/clima/${ciudadLimpia}`)
        // Si el proxy no está activo y Vite devuelve el index.html (SPA fallback)
        if (typeof response.data === 'string' && response.data.trim().startsWith('<!doctype')) {
            throw new Error('Vite proxy devolvió HTML.')
        }
        data = response.data
    } catch (errProxy) {
        console.warn("Fallo vía /api, intentando directamente a http://localhost:8085...", errProxy)
        // 2. Si falla el proxy, probar conexión directa a localhost:8085
        try {
            const responseDirect = await axios.get(`http://localhost:8085/ciudad/clima/${ciudadLimpia}`)
            data = responseDirect.data
        } catch (errDirect) {
            errorCapturado = errDirect
            console.error("Error al obtener el clima:", errDirect)
        }
    }

    if (data !== null) {
        console.log("Datos de clima recibidos:", data)
        setClima(data)
    } else {
        if (setError) {
            if (errorCapturado?.code === 'ERR_NETWORK' || !errorCapturado?.response) {
                setError('No se pudo conectar con el servidor backend (puerto 8085). Asegúrate de que tu backend esté ejecutándose.')
            } else if (errorCapturado?.response?.status === 404) {
                setError(`No se encontró el clima para la ciudad "${ciudad.trim()}".`)
            } else {
                setError(`Error en la consulta: ${errorCapturado?.message || 'Error desconocido'}`)
            }
        }
    }

    if (setCargando) setCargando(false)
}


