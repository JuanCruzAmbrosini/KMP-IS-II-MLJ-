import React, { useState, useRef } from 'react';
import clinicaService from '../services/api';

/**
 * Componente Funcional: Formulario de Registro de Paciente
 * Implementa el manejo de formulario controlado y subida binaria de FotoPaciente mediante FormData
 */
export const PacienteForm = ({ onPacienteGuardado }) => {
    const [formulario, setFormulario] = useState({
        nombre: '',
        apellido: '',
        documento: ''
    });

    const [archivoFoto, setArchivoFoto] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [cargando, setCargando] = useState(false);
    const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

    const fileInputRef = useRef(null);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormulario(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleFotoChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            // Validar tipo MIME
            if (!file.type.startsWith('image/')) {
                setMensaje({ tipo: 'error', texto: 'El archivo seleccionado debe ser una imagen válida.' });
                return;
            }
            setArchivoFoto(file);
            setPreviewUrl(URL.createObjectURL(file));
            setMensaje({ tipo: '', texto: '' });
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validaciones básicas de front
        if (!formulario.nombre.trim() || !formulario.apellido.trim() || !formulario.documento.trim()) {
            setMensaje({ tipo: 'error', texto: 'Por favor complete todos los campos obligatorios.' });
            return;
        }

        try {
            setCargando(true);
            setMensaje({ tipo: '', texto: '' });

            // Llamada al servicio HTTP enviando FormData con archivo binario
            const nuevoPaciente = await clinicaService.registrarPacienteConFoto(formulario, archivoFoto);

            setMensaje({ 
                tipo: 'exito', 
                texto: `¡Paciente ${nuevoPaciente.apellido}, ${nuevoPaciente.nombre} registrado con éxito!` 
            });

            // Limpieza del formulario
            setFormulario({ nombre: '', apellido: '', documento: '' });
            setArchivoFoto(null);
            setPreviewUrl(null);
            if (fileInputRef.current) fileInputRef.current.value = '';

            if (onPacienteGuardado) {
                onPacienteGuardado(nuevoPaciente);
            }
        } catch (error) {
            const errorMsg = error.response?.data || error.message || 'Error al registrar paciente';
            setMensaje({ tipo: 'error', texto: typeof errorMsg === 'string' ? errorMsg : JSON.stringify(errorMsg) });
        } finally {
            setCargando(false);
        }
    };

    return (
        <div className="card shadow-sm p-4 border-0 rounded-4 bg-white">
            <h3 className="h5 fw-bold text-dark mb-1">Registrar Nuevo Paciente</h3>
            <p className="text-muted small mb-4">Ingrese la información del paciente y su fotografía identificatoria (FotoPaciente).</p>

            {mensaje.texto && (
                <div className={`alert ${mensaje.tipo === 'error' ? 'alert-danger' : 'alert-success'} py-2 mb-3 small`}>
                    {mensaje.texto}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div className="row g-3">
                    <div className="col-md-6">
                        <label className="form-label fw-semibold small">Nombre *</label>
                        <input
                            type="text"
                            name="nombre"
                            value={formulario.nombre}
                            onChange={handleInputChange}
                            className="form-control form-control-sm"
                            placeholder="Ej: Laura"
                            disabled={cargando}
                            required
                        />
                    </div>

                    <div className="col-md-6">
                        <label className="form-label fw-semibold small">Apellido *</label>
                        <input
                            type="text"
                            name="apellido"
                            value={formulario.apellido}
                            onChange={handleInputChange}
                            className="form-control form-control-sm"
                            placeholder="Ej: Fernández"
                            disabled={cargando}
                            required
                        />
                    </div>

                    <div className="col-12">
                        <label className="form-label fw-semibold small">Documento / DNI *</label>
                        <input
                            type="text"
                            name="documento"
                            value={formulario.documento}
                            onChange={handleInputChange}
                            className="form-control form-control-sm"
                            placeholder="Ej: 39874561"
                            disabled={cargando}
                            required
                        />
                    </div>

                    <div className="col-12">
                        <label className="form-label fw-semibold small">Fotografía del Paciente (FotoPaciente)</label>
                        <input
                            type="file"
                            ref={fileInputRef}
                            onChange={handleFotoChange}
                            accept="image/*"
                            className="form-control form-control-sm"
                            disabled={cargando}
                        />
                        <div className="form-text small">La imagen se enviará en formato binario mediante <code>FormData</code>.</div>
                    </div>

                    {previewUrl && (
                        <div className="col-12 text-center py-2">
                            <p className="small text-muted mb-1">Vista Previa:</p>
                            <img
                                src={previewUrl}
                                alt="Previsualización"
                                style={{ width: '90px', height: '90px', objectFit: 'cover' }}
                                className="rounded-circle border shadow-sm"
                            />
                        </div>
                    )}

                    <div className="col-12 text-end mt-4">
                        <button
                            type="submit"
                            className="btn btn-primary px-4 fw-semibold"
                            disabled={cargando}
                        >
                            {cargando ? 'Guardando...' : 'Guardar Paciente'}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    );
};

export default PacienteForm;
