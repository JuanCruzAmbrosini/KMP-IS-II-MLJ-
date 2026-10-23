import React, { useState } from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import confetti from 'canvas-confetti';
import { Calendar, Clock, Users, MapPin, Sparkles, CheckCircle2, ChevronRight, ChevronLeft, AlertCircle, Utensils } from 'lucide-react';

export const ClientBookingWizard = ({ onBookingComplete }) => {
  const { salones, turnos, crearReserva, selectedDate, setSelectedDate } = useRestaurant();
  const { currentUser } = useAuth();

  const [step, setStep] = useState(1);
  const [comensales, setComensales] = useState(2);
  const [fecha, setFecha] = useState(selectedDate);
  const [salonId, setSalonId] = useState(1);
  const [turnoId, setTurnoId] = useState(3); // Cena 1er Turno
  const [clienteNombre, setClienteNombre] = useState(currentUser.nombre || '');
  const [clienteEmail, setClienteEmail] = useState(currentUser.email || '');
  const [clienteTelefono, setClienteTelefono] = useState(currentUser.telefono || '');
  const [notasEspeciales, setNotasEspeciales] = useState('');
  const [alergias, setAlergias] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [reservaConfirmada, setReservaConfirmada] = useState(null);

  const toggleAlergia = (item) => {
    setAlergias(prev => 
      prev.includes(item) ? prev.filter(a => a !== item) : [...prev, item]
    );
  };

  const handleFinalSubmit = async (e) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      const notasFinales = [
        notasEspeciales,
        alergias.length > 0 ? `Restricciones alimentarias: ${alergias.join(', ')}` : null
      ].filter(Boolean).join(' | ');

      const res = await crearReserva({
        clienteNombre,
        clienteEmail,
        clienteTelefono,
        comensales: Number(comensales),
        fecha,
        salonId: Number(salonId),
        turnoId: Number(turnoId),
        notasEspeciales: notasFinales,
        tipo: 'ESTANDAR'
      });

      // Confetti celebratory effect
      try {
        confetti({
          particleCount: 80,
          spread: 70,
          origin: { y: 0.6 }
        });
      } catch (err) {}

      setReservaConfirmada(res);
      setStep(4);
      if (onBookingComplete) onBookingComplete(res);
    } catch (err) {
      // Toast gestionado en context
    } finally {
      setIsSubmitting(false);
    }
  };

  const selectedSalon = salones.find(s => s.id === Number(salonId));
  const selectedTurno = turnos.find(t => t.id === Number(turnoId));

  return (
    <div className="wizard-container">
      <div className="wizard-card">
        {/* Wizard Progress Steps */}
        <div className="wizard-steps">
          <div className={`wizard-step-item ${step === 1 ? 'active' : step > 1 ? 'completed' : ''}`}>
            <div className="wizard-step-bubble">1</div>
            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: step >= 1 ? 'var(--text-main)' : 'var(--text-dim)' }}>
              Comensales
            </span>
          </div>
          <div style={{ flex: 1, height: '2px', background: step > 1 ? '#10b981' : 'var(--glass-border)', margin: '0 8px', marginTop: '-14px' }} />

          <div className={`wizard-step-item ${step === 2 ? 'active' : step > 2 ? 'completed' : ''}`}>
            <div className="wizard-step-bubble">2</div>
            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: step >= 2 ? 'var(--text-main)' : 'var(--text-dim)' }}>
              Turno & Salón
            </span>
          </div>
          <div style={{ flex: 1, height: '2px', background: step > 2 ? '#10b981' : 'var(--glass-border)', margin: '0 8px', marginTop: '-14px' }} />

          <div className={`wizard-step-item ${step === 3 ? 'active' : step > 3 ? 'completed' : ''}`}>
            <div className="wizard-step-bubble">3</div>
            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: step >= 3 ? 'var(--text-main)' : 'var(--text-dim)' }}>
              Datos & Preferencias
            </span>
          </div>
          <div style={{ flex: 1, height: '2px', background: step === 4 ? '#10b981' : 'var(--glass-border)', margin: '0 8px', marginTop: '-14px' }} />

          <div className={`wizard-step-item ${step === 4 ? 'active completed' : ''}`}>
            <div className="wizard-step-bubble"><CheckCircle2 size={16} /></div>
            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: step === 4 ? '#10b981' : 'var(--text-dim)' }}>
              Confirmación
            </span>
          </div>
        </div>

        {/* STEP 1: COMENSALES Y FECHA */}
        {step === 1 && (
          <div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '6px', color: 'var(--text-main)' }}>
              ¿Para cuántas personas será la experiencia gastronómica?
            </h2>
            <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginBottom: '24px' }}>
              Selecciona la cantidad de comensales y la fecha deseada. El motor calculará la mejor mesa disponible.
            </p>

            {/* Quick Guest Selector Buttons */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(70px, 1fr))', gap: '10px', marginBottom: '24px' }}>
              {[1, 2, 3, 4, 5, 6, 7, 8].map(num => (
                <button
                  key={num}
                  type="button"
                  className={`btn ${comensales === num ? 'btn-primary' : 'btn-secondary'}`}
                  style={{ padding: '14px 10px', flexDirection: 'column', gap: '4px' }}
                  onClick={() => setComensales(num)}
                >
                  <Users size={18} />
                  <span style={{ fontSize: '0.95rem', fontWeight: 700 }}>{num}</span>
                  <span style={{ fontSize: '0.65rem' }}>{num === 1 ? 'Persona' : 'Personas'}</span>
                </button>
              ))}
            </div>

            {/* Date Selector */}
            <div className="wizard-form-group">
              <label className="wizard-label">Fecha de la Reserva</label>
              <input
                type="date"
                className="wizard-input"
                min={new Date().toISOString().split('T')[0]}
                value={fecha}
                onChange={e => {
                  setFecha(e.target.value);
                  setSelectedDate(e.target.value);
                }}
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '24px' }}>
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => setStep(2)}
                style={{ gap: '6px' }}
              >
                Continuar a Turno & Salón <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}

        {/* STEP 2: TURNO Y SALON */}
        {step === 2 && (
          <div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '6px', color: 'var(--text-main)' }}>
              Selecciona Salón y Turno Horario
            </h2>
            <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginBottom: '20px' }}>
              Cada salón posee una atmósfera distintiva. Los turnos aseguran una estadía relajada de hasta 120 minutos.
            </p>

            {/* Salon Cards */}
            <div className="wizard-form-group">
              <label className="wizard-label">Salón o Ambiente</label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '10px' }}>
                {salones.map(s => (
                  <div
                    key={s.id}
                    onClick={() => setSalonId(s.id)}
                    style={{
                      background: salonId === s.id ? 'rgba(16, 185, 129, 0.12)' : 'var(--bg-dark-900)',
                      border: `2px solid ${salonId === s.id ? '#10b981' : 'var(--glass-border)'}`,
                      borderRadius: 'var(--radius-md)',
                      padding: '14px',
                      cursor: 'pointer',
                      transition: 'all 0.2s'
                    }}
                  >
                    <div style={{ fontWeight: 700, fontSize: '0.9rem', color: 'var(--text-main)', marginBottom: '4px' }}>
                      {s.nombre}
                    </div>
                    <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
                      {s.descripcion}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Turnos Selector */}
            <div className="wizard-form-group">
              <label className="wizard-label">Turno Horario</label>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '10px' }}>
                {turnos.map(t => (
                  <div
                    key={t.id}
                    onClick={() => setTurnoId(t.id)}
                    style={{
                      background: turnoId === t.id ? 'rgba(245, 158, 11, 0.12)' : 'var(--bg-dark-900)',
                      border: `2px solid ${turnoId === t.id ? '#f59e0b' : 'var(--glass-border)'}`,
                      borderRadius: 'var(--radius-md)',
                      padding: '12px 14px',
                      cursor: 'pointer',
                      transition: 'all 0.2s',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '10px'
                    }}
                  >
                    <Clock size={18} color={turnoId === t.id ? '#f59e0b' : 'var(--text-muted)'} />
                    <div>
                      <div style={{ fontWeight: 700, fontSize: '0.85rem', color: 'var(--text-main)' }}>
                        {t.nombre}
                      </div>
                      <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                        {t.horaInicio} a {t.horaFin} ({t.duracionMinutos} min)
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            {/* Smart Allocation Info Callout */}
            <div className="algorithm-callout">
              <Sparkles size={20} color="#a855f7" style={{ flexShrink: 0 }} />
              <div>
                <strong style={{ color: '#c084fc', fontSize: '0.825rem' }}>Algoritmo de Asignación Fudo (Sección 4.2.B):</strong>
                <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                  El motor backend evalúa todas las mesas libres del <strong style={{ color: 'white' }}>{selectedSalon?.nombre}</strong> y asigna automáticamente la que satisfaga <span style={{ fontFamily: 'var(--font-mono)', color: '#a7f3d0' }}>min(Capacidad - {comensales})</span> para no desperdiciar sillas.
                </p>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '24px' }}>
              <button type="button" className="btn btn-secondary" onClick={() => setStep(1)} style={{ gap: '6px' }}>
                <ChevronLeft size={16} /> Volver
              </button>
              <button type="button" className="btn btn-primary" onClick={() => setStep(3)} style={{ gap: '6px' }}>
                Continuar a Datos <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}

        {/* STEP 3: DATOS DEL COMENSAL Y REQUERIMIENTOS */}
        {step === 3 && (
          <form onSubmit={handleFinalSubmit}>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 800, marginBottom: '6px', color: 'var(--text-main)' }}>
              Tus Datos de Contacto y Notas Especiales
            </h2>
            <p style={{ fontSize: '0.825rem', color: 'var(--text-muted)', marginBottom: '20px' }}>
              Te enviaremos la confirmación instantánea con tu código de reserva.
            </p>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px', marginBottom: '16px' }}>
              <div>
                <label className="wizard-label">Nombre y Apellido *</label>
                <input
                  type="text"
                  required
                  className="wizard-input"
                  value={clienteNombre}
                  onChange={e => setClienteNombre(e.target.value)}
                  placeholder="Ej. Mateo Peralta"
                />
              </div>
              <div>
                <label className="wizard-label">Teléfono Móvil *</label>
                <input
                  type="tel"
                  required
                  className="wizard-input"
                  value={clienteTelefono}
                  onChange={e => setClienteTelefono(e.target.value)}
                  placeholder="Ej. +54 9 351 555-1234"
                />
              </div>
            </div>

            <div className="wizard-form-group">
              <label className="wizard-label">Email de Confirmación *</label>
              <input
                type="email"
                required
                className="wizard-input"
                value={clienteEmail}
                onChange={e => setClienteEmail(e.target.value)}
                placeholder="tuemail@ejemplo.com"
              />
            </div>

            {/* Restricciones alimentarias rápidas */}
            <div className="wizard-form-group">
              <label className="wizard-label">Restricciones Alimentarias / Alergias</label>
              <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                {['Sin Gluten (Celíaco)', 'Vegetariano', 'Vegano', 'Alergia a Mariscos', 'Sin Lactosa', 'Silla de Bebé'].map(item => (
                  <button
                    key={item}
                    type="button"
                    onClick={() => toggleAlergia(item)}
                    className={`btn btn-sm ${alergias.includes(item) ? 'btn-gold' : 'btn-secondary'}`}
                  >
                    {item}
                  </button>
                ))}
              </div>
            </div>

            <div className="wizard-form-group">
              <label className="wizard-label">Notas Adicionales (Opcional)</label>
              <textarea
                rows={2}
                className="wizard-textarea"
                value={notasEspeciales}
                onChange={e => setNotasEspeciales(e.target.value)}
                placeholder="Ej. Aniversario de bodas, preferencia por ubicación tranquila o cerca de ventanal..."
              />
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '24px' }}>
              <button type="button" className="btn btn-secondary" onClick={() => setStep(2)} style={{ gap: '6px' }}>
                <ChevronLeft size={16} /> Volver
              </button>
              <button type="submit" disabled={isSubmitting} className="btn btn-gold" style={{ padding: '10px 24px', fontSize: '0.95rem' }}>
                {isSubmitting ? 'Procesando algoritmo...' : 'Confirmar Reserva Ahora'}
              </button>
            </div>
          </form>
        )}

        {/* STEP 4: CONFIRMACION EXITOSA */}
        {step === 4 && reservaConfirmada && (
          <div style={{ textAlign: 'center', padding: '20px 0' }}>
            <div style={{ width: '64px', height: '64px', borderRadius: '50%', background: 'rgba(16, 185, 129, 0.15)', border: '2px solid #10b981', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px', color: '#10b981' }}>
              <CheckCircle2 size={36} />
            </div>

            <h2 style={{ fontSize: '1.45rem', fontWeight: 800, color: 'var(--text-main)', marginBottom: '8px' }}>
              ¡Tu Reserva está Confirmada!
            </h2>
            <div style={{ display: 'inline-block', background: 'var(--bg-dark-900)', border: '1px solid var(--glass-border)', padding: '6px 16px', borderRadius: 'var(--radius-md)', fontFamily: 'var(--font-mono)', fontSize: '1.1rem', fontWeight: 800, color: '#38bdf8', marginBottom: '20px' }}>
              {reservaConfirmada.reserva.codigoReserva}
            </div>

            {/* Booking Summary Box */}
            <div style={{ maxWidth: '480px', margin: '0 auto 24px', background: 'var(--bg-dark-900)', border: '1px solid var(--glass-border)', borderRadius: 'var(--radius-lg)', padding: '18px', textAlign: 'left', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--glass-border)', paddingBottom: '8px' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Mesa Asignada</span>
                <span style={{ fontWeight: 700, color: '#10b981', fontFamily: 'var(--font-mono)' }}>
                  Mesa #{reservaConfirmada.mesaAsignada.numero} (Capacidad: {reservaConfirmada.mesaAsignada.capacidadMaxima})
                </span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--glass-border)', paddingBottom: '8px' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Salón & Turno</span>
                <span style={{ fontWeight: 600, color: 'var(--text-main)', fontSize: '0.85rem' }}>
                  {reservaConfirmada.reserva.salonNombre} | {reservaConfirmada.reserva.turnoNombre}
                </span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Comensales & Fecha</span>
                <span style={{ fontWeight: 600, color: 'var(--text-main)', fontSize: '0.85rem' }}>
                  {reservaConfirmada.reserva.comensales} personas el {reservaConfirmada.reserva.fecha}
                </span>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'center', gap: '12px' }}>
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => {
                  setStep(1);
                  setReservaConfirmada(null);
                }}
              >
                Hacer Otra Reserva
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
