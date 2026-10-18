import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Accept': 'application/json',
    }
});

export const clinicaService = {
    // ==========================================
    // PACIENTES Y FOTOS
    // ==========================================
    listarPacientes: async () => {
        const response = await apiClient.get('/pacientes');
        return response.data;
    },

    buscarPacientePorId: async (id) => {
        const response = await apiClient.get(`/pacientes/${id}`);
        return response.data;
    },

    /**
     * Envío de datos del paciente junto con el archivo binario FotoPaciente mediante FormData
     * multipart/form-data
     */
    registrarPacienteConFoto: async (pacienteData, fotoFile) => {
        const formData = new FormData();
        formData.append('nombre', pacienteData.nombre);
        formData.append('apellido', pacienteData.apellido);
        formData.append('documento', pacienteData.documento);

        if (fotoFile) {
            formData.append('foto', fotoFile);
        }

        const response = await apiClient.post('/pacientes', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    },

    eliminarPacienteLogico: async (id) => {
        const response = await apiClient.delete(`/pacientes/${id}`);
        return response.data;
    },

    obtenerUrlFoto: (fotoId) => {
        return `${API_BASE_URL}/pacientes/foto/${fotoId}`;
    },

    // ==========================================
    // HISTORIAS CLÍNICAS Y DETALLES
    // ==========================================
    obtenerHistoriasPorPaciente: async (pacienteId) => {
        const response = await apiClient.get(`/historias-clinicas/paciente/${pacienteId}`);
        return response.data;
    },

    agregarDetalleHistoria: async (detalleData) => {
        // detalleData: { pacienteId, medicoId, detalleHistoria, fechaHistoria }
        const response = await apiClient.post('/historias-clinicas/detalles', detalleData, {
            headers: {
                'Content-Type': 'application/json',
            }
        });
        return response.data;
    },

    // ==========================================
    // MÉDICOS
    // ==========================================
    listarMedicos: async () => {
        const response = await apiClient.get('/medicos');
        return response.data;
    }
};

export default clinicaService;
