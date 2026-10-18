import React, { useState, useEffect } from 'react';
import clinicaService from '../services/api';

/**
 * Componente Funcional: Visualizador y Administrador de Historia Clínica
 * Consume la API REST para renderizar evoluciones médicas y agregar nuevos detalles
 */
export const HistoriaClinicaView = ({ paciente, onVolver }) => {
    const [historias, setHistorias] = useState([]);
    const [medicos, setMedicos] = useState([]);
    const [cargando, setCargando] = useState(true);
    const [guardando, setGuardando] = useState(false);

    // Estado del nuevo detalle
    const [nuevoDetalle, setNuevoDetalle] = useState({
        medicoId: '',
        detalleHistoria: ''
    });
    const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

    useEffect(() => {
        cargarDatos();
    }, [paciente.id]);

    const cargarDatos = async () => {
        try {
            setCargando(true);
            const [historiasData, medicosData] = await Promise.all([
                clinicaService.obtenerHistoriasPorPaciente(paciente.id),
                clinicaService.listarMedicos()
            ]);
            setHistorias(historiasData);
            setMedicos(medicosData);
        } catch (error) {
            setMensaje({ tipo: 'error', texto: 'Error al cargar historia clínica o médicos.' });
        } finally {
            setCargando(false);
        }
    };

    const handleDetalleSubmit = async (e) => {
        e.preventDefault();
        if (!nuevoDetalle.medicoId || !nuevoDetalle.detalleHistoria.trim()) {
            setMensaje({ tipo: 'error', texto: 'Seleccione un médico y redacte la evolución clínica.' });
            return;
        }

        try {
            setGuardando(true);
            setMensaje({ tipo: '', texto: '' });

            await clinicaService.agregarDetalleHistoria({
                pacienteId: paciente.id,
                medicoId: nuevoDetalle.medicoId,
                detalleHistoria: nuevoDetalle.detalleHistoria
            });

            setMensaje({ tipo: 'exito', texto: 'Evolución médica registrada exitosamente. Notificación emitida al médico.' });
            setNuevoDetalle({ medicoId: '', detalleHistoria: '' });

            // Recargar datos actualizados
            const historiasActualizadas = await clinicaService.obtenerHistoriasPorPaciente(paciente.id);
            setHistorias(historiasActualizadas);
        } catch (error) {
            setMensaje({ tipo: 'error', texto: 'No se pudo guardar la evolución médica.' });
        } finally {
            setGuardando(false);
        }
    };

    const fotoId = paciente.fotos && paciente.fotos.length > 0 ? paciente.fotos[0].id : null;

    return (
        <div className="container py-3" style={{ maxWidth: '950px' }}>
            <button onClick={onVolver} className="btn btn-sm btn-outline-secondary mb-3">
                ← Volver al Listado de Pacientes
            </button>

            {/* Cabecera del Paciente */}
            <div className="card shadow-sm p-4 border-0 rounded-4 bg-white mb-4">
                <div className="d-flex align-items-center justify-content-between flex-wrap gap-3">
                    <div className="d-flex align-items-center gap-3">
                        {fotoId ? (
                            <img
                                src={clinicaService.obtenerUrlFoto(fotoId)}
                                alt="Foto Paciente"
                                style={{ width: '75px', height: '75px', objectFit: 'cover' }}
                                className="rounded-circle border shadow-sm"
                            />
                        ) : (
                            <div className="rounded-circle bg-light border d-flex align-items-center justify-content-center fw-bold text-secondary"
                                 style={{ width: '75px', height: '75px', fontSize: '1.2rem' }}>
                                {paciente.nombre?.charAt(0)}{paciente.apellido?.charAt(0)}
                            </div>
                        )}
                        <div>
                            <h2 className="h4 fw-bold text-dark mb-1">{paciente.apellido}, {paciente.nombre}</h2>
                            <p className="text-muted small mb-0">
                                Documento: <strong>{paciente.documento}</strong> | ID: <code>{paciente.id}</code>
                            </p>
                        </div>
                    </div>
                    <span className="badge bg-success-subtle text-success px-3 py-2 rounded-pill fw-semibold">
                        Historia Clínica Activa
                    </span>
                </div>
            </div>

            {mensaje.texto && (
                <div className={`alert ${mensaje.tipo === 'error' ? 'alert-danger' : 'alert-success'} py-2 mb-4 small`}>
                    {mensaje.texto}
                </div>
            )}

            <div className="row g-4">
                {/* Formulario Nueva Evolución */}
                <div className="col-md-5">
                    <div className="card shadow-sm p-4 border-0 rounded-4 bg-white sticky-top" style={{ top: '20px' }}>
                        <h4 className="h6 fw-bold text-dark mb-3">Registrar Nueva Evolución</h4>
                        <form onSubmit={handleDetalleSubmit}>
                            <div className="mb-3">
                                <label className="form-label small fw-semibold">Médico Responsable *</label>
                                <select
                                    className="form-select form-select-sm"
                                    value={nuevoDetalle.medicoId}
                                    onChange={(e) => setNuevoDetalle({ ...nuevoDetalle, medicoId: e.target.value })}
                                    required
                                    disabled={guardando}
                                >
                                    <option value="">-- Seleccionar Médico --</option>
                                    {medicos.map((m) => (
                                        <option key={m.id} value={m.id}>
                                            Dr. {m.apellido}, {m.nombre} ({m.documento})
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="mb-3">
                                <label className="form-label small fw-semibold">Detalle / Diagnóstico *</label>
                                <textarea
                                    className="form-control form-control-sm"
                                    rows="4"
                                    placeholder="Describa el diagnóstico, tratamiento o indicaciones..."
                                    value={nuevoDetalle.detalleHistoria}
                                    onChange={(e) => setNuevoDetalle({ ...nuevoDetalle, detalleHistoria: e.target.value })}
                                    required
                                    disabled={guardando}
                                ></textarea>
                            </div>

                            <button
                                type="submit"
                                className="btn btn-primary btn-sm w-100 fw-semibold"
                                disabled={guardando}
                            >
                                {guardando ? 'Guardando Entrada...' : '+ Agregar a Historia Clínica'}
                            </button>
                        </form>
                    </div>
                </div>

                {/* Historial de Detalles */}
                <div className="col-md-7">
                    <div className="card shadow-sm p-4 border-0 rounded-4 bg-white">
                        <h4 className="h6 fw-bold text-dark mb-3">Evoluciones Médicas Registradas</h4>

                        {cargando ? (
                            <div className="text-center py-4 text-muted small">Cargando evoluciones...</div>
                        ) : historias.length === 0 || !historias[0]?.detalles?.length ? (
                            <div className="text-center py-5 text-muted small">
                                No hay evoluciones registradas para este paciente todavía.
                            </div>
                        ) : (
                            <div className="border-start border-2 border-primary ps-3 ms-2">
                                {historias.flatMap(h => h.detalles || []).map((det) => (
                                    <div key={det.id} className="mb-4 position-relative">
                                        <div className="d-flex justify-content-between align-items-center mb-1">
                                            <span className="badge bg-primary-subtle text-primary fw-semibold">
                                                Dr. {det.medico?.apellido}, {det.medico?.nombre}
                                            </span>
                                            <span className="text-muted small">
                                                {new Date(det.fechaHistoria).toLocaleString()}
                                            </span>
                                        </div>
                                        <p className="text-secondary small mb-0 mt-2" style={{ whiteSpace: 'pre-line' }}>
                                            {det.detalleHistoria}
                                        </p>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HistoriaClinicaView;
