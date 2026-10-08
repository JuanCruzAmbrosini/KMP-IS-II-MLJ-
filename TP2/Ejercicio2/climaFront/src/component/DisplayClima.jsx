import React from 'react'
import { traducirDescripcion } from './Traduccion'

const DisplayClima = ({ clima, cargando, error }) => {
    let climaData = null
    let errorParsing = false

    if (clima) {
        try {
            let parsed = typeof clima === 'string' ? JSON.parse(clima) : clima
            if (typeof parsed === 'string') {
                parsed = JSON.parse(parsed)
            }
            climaData = parsed
        } catch (e) {
            console.error("Error al procesar los datos del clima:", e)
            errorParsing = true
        }
    }

    // Extraer nombre de ciudad flexiblemente
    const nombreCiudad = climaData?.name || climaData?.ciudad || climaData?.city || climaData?.nombre || null

    // Extraer temperatura
    let tempRaw = climaData?.main?.temp ?? climaData?.temp ?? climaData?.temperatura ?? climaData?.temperature ?? null
    let tempCelsius = null
    if (tempRaw !== null && tempRaw !== undefined) {
        const numTemp = parseFloat(tempRaw)
        if (!isNaN(numTemp)) {
            tempCelsius = numTemp > 60 ? (numTemp - 273.15).toFixed(1) : numTemp.toFixed(1)
        }
    }

    // Extraer descripción
    const descRaw = climaData?.weather?.[0]?.description || 
                    climaData?.descripcion || 
                    climaData?.description || 
                    climaData?.weather?.[0]?.main || 
                    null

    const descripcion = descRaw ? traducirDescripcion(descRaw) : null

    // Detalles adicionales opcionales
    const humedad = climaData?.main?.humidity ?? climaData?.humedad ?? null
    const viento = climaData?.wind?.speed ?? climaData?.viento ?? null
    const sensacion = climaData?.main?.feels_like ?? climaData?.sensacion ?? null
    const sensacionCelsius = sensacion ? (sensacion > 60 ? (sensacion - 273.15).toFixed(1) : parseFloat(sensacion).toFixed(1)) : null

    const getClimaIcon = (desc) => {
        if (!desc) return 'bi-cloud-sun'
        const d = desc.toLowerCase()
        if (d.includes('despejado') || d.includes('clear')) return 'bi-sun-fill text-warning'
        if (d.includes('nublado') || d.includes('nubes') || d.includes('cloud')) return 'bi-clouds-fill text-info'
        if (d.includes('lluvia') || d.includes('rain') || d.includes('chubasco')) return 'bi-cloud-rain-heavy-fill text-primary'
        if (d.includes('tormenta') || d.includes('thunder')) return 'bi-cloud-lightning-rain-fill text-warning'
        if (d.includes('nieve') || d.includes('snow')) return 'bi-snow text-white'
        if (d.includes('niebla') || d.includes('neblina') || d.includes('mist') || d.includes('fog')) return 'bi-cloud-fog2-fill text-secondary'
        return 'bi-cloud-sun-fill text-warning'
    }

    const tieneDatos = Boolean(nombreCiudad || tempCelsius !== null || descripcion)

    return (
        <div className="display-clima-section mt-4">
            {/* Mensaje de error si la consulta falló */}
            {error && (
                <div className="alert alert-custom-error d-flex align-items-center gap-3 mb-4" role="alert">
                    <i className="bi bi-exclamation-triangle-fill fs-4 flex-shrink-0"></i>
                    <div>
                        <strong>Atención:</strong> {error}
                    </div>
                </div>
            )}

            {/* Spinner de carga */}
            {cargando && (
                <div className="text-center py-4">
                    <div className="spinner-border text-light" style={{ width: '3rem', height: '3rem' }} role="status">
                        <span className="visually-hidden">Cargando...</span>
                    </div>
                    <p className="mt-2 text-white-50">Consultando datos meteorológicos...</p>
                </div>
            )}

            {/* Tarjeta destacada de resumen cuando hay datos */}
            {!cargando && tieneDatos && (
                <div className="weather-highlight-card mb-4">
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div>
                            <span className="badge bg-light text-dark px-3 py-1 mb-2 rounded-pill">
                                <i className="bi bi-geo-alt-fill text-danger me-1"></i> Clima Actual
                            </span>
                            <h2 className="city-title mb-0">{nombreCiudad || 'Ciudad'}</h2>
                            <p className="text-capitalize weather-desc-badge mb-0 mt-1">
                                <i className={`bi ${getClimaIcon(descripcion)} me-2`}></i>
                                {descripcion || 'Información del tiempo'}
                            </p>
                        </div>
                        <div className="temp-display text-end">
                            <span className="temp-number">{tempCelsius ?? '--'}</span>
                            <span className="temp-unit">°C</span>
                        </div>
                    </div>

                    {/* Fila con métricas adicionales si existen */}
                    {(humedad !== null || viento !== null || sensacionCelsius !== null) && (
                        <div className="weather-details-row row g-2 mt-3 pt-3 border-top border-white border-opacity-10">
                            {humedad !== null && (
                                <div className="col">
                                    <div className="detail-pill">
                                        <i className="bi bi-droplet-fill text-info me-1"></i>
                                        <span>Humedad: <strong>{humedad}%</strong></span>
                                    </div>
                                </div>
                            )}
                            {viento !== null && (
                                <div className="col">
                                    <div className="detail-pill">
                                        <i className="bi bi-wind text-light me-1"></i>
                                        <span>Viento: <strong>{viento} m/s</strong></span>
                                    </div>
                                </div>
                            )}
                            {sensacionCelsius !== null && (
                                <div className="col">
                                    <div className="detail-pill">
                                        <i className="bi bi-thermometer-half text-warning me-1"></i>
                                        <span>Sensación: <strong>{sensacionCelsius}°C</strong></span>
                                    </div>
                                </div>
                            )}
                        </div>
                    )}
                </div>
            )}

            {/* Tabla de resultados */}
            <div className="weather-table-container">
                <div className="table-responsive">
                    <table className="table custom-weather-table mb-0">
                        <thead>
                            <tr>
                                <th><i className="bi bi-buildings me-2"></i>Ciudad</th>
                                <th><i className="bi bi-thermometer-half me-2"></i>Temperatura (°C)</th>
                                <th><i className="bi bi-cloud-sun me-2"></i>Descripción</th>
                            </tr>
                        </thead>
                        <tbody>
                            {tieneDatos ? (
                                <tr className="data-row">
                                    <td className="fw-semibold">
                                        <i className="bi bi-geo-alt text-danger me-1"></i>
                                        {nombreCiudad || '-'}
                                    </td>
                                    <td>
                                        <span className="badge temp-badge">
                                            {tempCelsius !== null ? `${tempCelsius} °C` : '-'}
                                        </span>
                                    </td>
                                    <td>
                                        <span className="badge desc-badge text-capitalize">
                                            <i className={`bi ${getClimaIcon(descripcion)} me-1`}></i>
                                            {descripcion || '-'}
                                        </span>
                                    </td>
                                </tr>
                            ) : errorParsing ? (
                                <tr>
                                    <td colSpan="3" className="empty-table-cell text-danger">
                                        <i className="bi bi-exclamation-circle me-2"></i>
                                        Error al procesar el formato de los datos devueltos por el servidor.
                                    </td>
                                </tr>
                            ) : (
                                <tr>
                                    <td colSpan="3" className="empty-table-cell">
                                        <i className="bi bi-search me-2 opacity-50"></i>
                                        Ingresa una ciudad arriba y presiona <strong>&quot;Consultar Clima&quot;</strong> para ver los datos.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    )
}

export default DisplayClima
