import React, { useState, useEffect } from 'react';
import PacienteForm from './components/PacienteForm';
import PacienteList from './components/PacienteList';
import HistoriaClinicaView from './components/HistoriaClinicaView';
import clinicaService from './services/api';

export const App = () => {
    const [pacientes, setPacientes] = useState([]);
    const [pacienteSeleccionado, setPacienteSeleccionado] = useState(null);
    const [cargando, setCargando] = useState(true);

    useEffect(() => {
        cargarPacientes();
    }, []);

    const cargarPacientes = async () => {
        try {
            setCargando(true);
            const data = await clinicaService.listarPacientes();
            setPacientes(data);
        } catch (error) {
            console.error('Error cargando pacientes:', error);
        } finally {
            setCargando(false);
        }
    };

    const handlePacienteCreado = (nuevoPaciente) => {
        setPacientes(prev => [nuevoPaciente, ...prev]);
    };

    const handleEliminarPaciente = async (id) => {
        if (!window.confirm('¿Desea realizar el borrado lógico de este paciente?')) return;
        try {
            await clinicaService.eliminarPacienteLogico(id);
            setPacientes(prev => prev.filter(p => p.id !== id));
        } catch (error) {
            alert('Error al eliminar paciente: ' + (error.response?.data || error.message));
        }
    };

    return (
        <div className="min-vh-100 bg-light py-4" style={{ fontFamily: "'Inter', sans-serif" }}>
            <header className="container mb-4 text-center">
                <span className="badge bg-primary-subtle text-primary fw-semibold px-3 py-2 rounded-pill mb-2">
                    Versión B: React.js (SPA) + Spring Boot (REST API)
                </span>
                <h1 className="h3 fw-bold text-dark">Portal de Gestión Hospitalaria y Clínica</h1>
                <p className="text-muted small">Arquitectura Desacoplada | Soft Delete | Patrones GoF</p>
            </header>

            <main className="container">
                {pacienteSeleccionado ? (
                    <HistoriaClinicaView
                        paciente={pacienteSeleccionado}
                        onVolver={() => setPacienteSeleccionado(null)}
                    />
                ) : (
                    <div className="row g-4">
                        <div className="col-lg-5">
                            <PacienteForm onPacienteGuardado={handlePacienteCreado} />
                        </div>
                        <div className="col-lg-7">
                            <PacienteList
                                pacientes={pacientes}
                                onVerHistoria={(p) => setPacienteSeleccionado(p)}
                                onEliminar={handleEliminarPaciente}
                                cargando={cargando}
                            />
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
};

export default App;
