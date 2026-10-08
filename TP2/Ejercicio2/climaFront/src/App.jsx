import { useState } from 'react'
import './App.css'
import InputCiudad from './component/InputCiudad'
import ButtonConsulta from './component/ButtonConsulta'
import DisplayClima from './component/DisplayClima'
import { ObtenerClima } from './api/ObtenerClima'

function App() {
  const [ciudad, setCiudad] = useState('')
  const [clima, setClima] = useState('')
  const [cargando, setCargando] = useState(false)
  const [error, setError] = useState(null)

  const handlerObtenerClima = (ciudadParam) => {
    const ciudadABuscar = typeof ciudadParam === 'string' ? ciudadParam : ciudad
    if (!ciudadABuscar || ciudadABuscar.trim() === '') {
      setError('Por favor, ingresa el nombre de una ciudad.')
      return
    }
    ObtenerClima(ciudadABuscar, setClima, setError, setCargando)
  }

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      handlerObtenerClima()
    }
  }

  const handleQuickSearch = (nombre) => {
    setCiudad(nombre)
    handlerObtenerClima(nombre)
  }

  return (
    <div className='app-wrapper'>
      <div className='main-weather-card'>
        {/* Encabezado */}
        <header className='app-header text-center mb-4'>
          <div className='badge-app-pill mb-2'>
            <i className='bi bi-cloud-sun-fill me-2'></i>
            <span>Estación Meteorológica</span>
          </div>
          <h1 className='app-title'>Consulta el Clima Actual</h1>
          <p className='app-subtitle'>
            Ingresa una ciudad para obtener su temperatura y condiciones climáticas en tiempo real.
          </p>
        </header>

        {/* Barra de búsqueda */}
        <div className='search-section mb-3'>
          <div className='row g-2 align-items-center'>
            <div className='col-12 col-md-8'>
              <InputCiudad 
                ciudad={ciudad} 
                setCiudad={setCiudad} 
                onKeyDown={handleKeyDown} 
              />
            </div>
            <div className='col-12 col-md-4'>
              <ButtonConsulta 
                obtenerClima={() => handlerObtenerClima()} 
                cargando={cargando} 
              />
            </div>
          </div>

          {/* Ciudades rápidas para probar con 1 clic */}
          <div className='quick-cities mt-3 d-flex flex-wrap align-items-center gap-2'>
            <span className='quick-label text-white-50 small'>
              <i className='bi bi-lightning-charge-fill me-1 text-warning'></i>Sugerencias:
            </span>
            {['Buenos Aires', 'Córdoba', 'Madrid', 'Santiago', 'Miami'].map((c) => (
              <button
                key={c}
                type='button'
                className='btn btn-quick-chip'
                onClick={() => handleQuickSearch(c)}
                disabled={cargando}
              >
                {c}
              </button>
            ))}
          </div>
        </div>

        {/* Tabla y resultado del clima */}
        <DisplayClima clima={clima} cargando={cargando} error={error} />
      </div>
    </div>
  )
}

export default App


