import React from 'react'

const InputCiudad = ({ ciudad, setCiudad, onKeyDown }) => {
    return (
        <div className="input-group-custom">
            <span className="input-icon">
                <i className="bi bi-geo-alt-fill"></i>
            </span>
            <input 
                className="form-control custom-input"
                type="text"
                placeholder="Ingresa una ciudad (ej: Buenos Aires, Madrid, Tokio...)"
                value={ciudad}
                onChange={e => setCiudad(e.target.value)}
                onKeyDown={onKeyDown}
            />
            {ciudad && (
                <button 
                    type="button" 
                    className="btn btn-clear-input"
                    onClick={() => setCiudad('')}
                    title="Limpiar"
                >
                    <i className="bi bi-x-circle-fill"></i>
                </button>
            )}
        </div>
    )
}

export default InputCiudad
