import React from 'react';
import clinicaService from '../services/api';

export const PacienteList = ({ pacientes, onVerHistoria, onEliminar, cargando }) => {
    return (
        <div className="card shadow-sm p-4 border-0 rounded-4 bg-white">
            <h3 className="h5 fw-bold text-dark mb-3">Pacientes Registrados</h3>
            {cargando ? (
                <div className="text-center py-4 text-muted small">Cargando pacientes...</div>
            ) : pacientes.length === 0 ? (
                <div className="text-center py-4 text-muted small">No hay pacientes activos.</div>
            ) : (
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead className="table-light">
                            <tr>
                                <th style={{ width: '60px' }}>Foto</th>
                                <th>Documento</th>
                                <th>Paciente</th>
                                <th className="text-end">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {pacientes.map((p) => {
                                const fotoId = p.fotos && p.fotos.length > 0 ? p.fotos[0].id : null;
                                return (
                                    <tr key={p.id}>
                                        <td>
                                            {fotoId ? (
                                                <img
                                                    src={clinicaService.obtenerUrlFoto(fotoId)}
                                                    alt="Foto"
                                                    style={{ width: '42px', height: '42px', objectFit: 'cover' }}
                                                    className="rounded-circle border"
                                                />
                                            ) : (
                                                <div
                                                    className="rounded-circle bg-light border d-flex align-items-center justify-content-center fw-bold text-muted"
                                                    style={{ width: '42px', height: '42px', fontSize: '0.85rem' }}
                                                >
                                                    {p.nombre?.charAt(0)}{p.apellido?.charAt(0)}
                                                </div>
                                            )}
                                        </td>
                                        <td className="fw-semibold">{p.documento}</td>
                                        <td>{p.apellido}, {p.nombre}</td>
                                        <td className="text-end">
                                            <button
                                                onClick={() => onVerHistoria(p)}
                                                className="btn btn-sm btn-outline-primary me-2"
                                            >
                                                📋 Historia Clínica
                                            </button>
                                            <button
                                                onClick={() => onEliminar(p.id)}
                                                className="btn btn-sm btn-outline-danger"
                                            >
                                                🗑️ Borrar
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default PacienteList;
