import React from 'react'

const ButtonConsulta = ({ obtenerClima, cargando }) => {
  return (
    <button 
      className="btn btn-consultar d-flex align-items-center justify-content-center gap-2" 
      onClick={obtenerClima}
      disabled={cargando}
    >
      {cargando ? (
        <>
          <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
          <span>Buscando...</span>
        </>
      ) : (
        <>
          <i className="bi bi-search"></i>
          <span>Consultar Clima</span>
        </>
      )}
    </button>
  )
}

export default ButtonConsulta
