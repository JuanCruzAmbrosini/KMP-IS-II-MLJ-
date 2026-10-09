package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.EmailLog;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.EmailLogRepository;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${biblioteca.facultad.url:https://frm.utn.edu.ar}")
    private String facultadUrl;

    @Value("${biblioteca.facultad.nombre:Universidad Tecnológica Nacional - FRM}")
    private String facultadNombre;

    @Value("${biblioteca.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${biblioteca.mail.from:biblioteca.universitaria@frm.utn.edu.ar}")
    private String mailFrom;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private EmailLogRepository emailLogRepository;

    @Override
    public EmailLog enviarRecordatorioVencimiento(Persona persona, Libro libro) {
        String destinatario = persona.getEmail();
        String nombreCompleto = persona.getNombre() + " " + persona.getApellido();
        String asunto = "⏰ Recordatorio de Devolución: El libro \"" + libro.getTitulo() + "\" vence mañana";

        String fechaVenc = (libro.getFechaVencimientoDevolucion() != null)
                ? libro.getFechaVencimientoDevolucion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "Mañana";

        String html = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f1f5f9; margin: 0; padding: 20px; color: #1e293b; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }
                    .header { background: linear-gradient(135deg, #0284c7, #0369a1); color: #ffffff; padding: 25px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 24px; font-weight: 700; }
                    .badge { display: inline-block; background: #fef08a; color: #854d0e; padding: 4px 12px; border-radius: 9999px; font-size: 13px; font-weight: 600; margin-top: 10px; }
                    .body { padding: 30px 25px; line-height: 1.6; }
                    .info-card { background: #f8fafc; border-left: 4px solid #0284c7; padding: 15px 20px; border-radius: 6px; margin: 20px 0; }
                    .info-row { margin: 8px 0; font-size: 15px; }
                    .info-row strong { color: #0f172a; }
                    .footer { background: #f8fafc; padding: 20px; text-align: center; font-size: 13px; color: #64748b; border-top: 1px solid #e2e8f0; }
                    .btn { display: inline-block; background: #0284c7; color: #ffffff !important; text-decoration: none; padding: 12px 25px; border-radius: 8px; font-weight: 600; margin-top: 15px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📚 Biblioteca Universitaria</h1>
                        <span class="badge">Aviso de Vencimiento de Préstamo (24 Horas)</span>
                    </div>
                    <div class="body">
                        <p>Estimado/a <strong>%s</strong>,</p>
                        <p>Te recordamos que el plazo de préstamo para el siguiente material bibliográfico vence <strong>mañana</strong>:</p>
                        <div class="info-card">
                            <div class="info-row"><strong>📖 Título:</strong> %s</div>
                            <div class="info-row"><strong>✍️ Autor:</strong> %s</div>
                            <div class="info-row"><strong>📅 Fecha Límite de Devolución:</strong> %s</div>
                        </div>
                        <p>Por favor, acércate a la biblioteca para realizar la devolución o solicitar una renovación en caso de estar disponible.</p>
                        <div style="text-align: center;">
                            <a href="%s" class="btn">Acceder al Portal de la Facultad</a>
                        </div>
                    </div>
                    <div class="footer">
                        <p>%s — Sistema Automatizado de Gestión Bibliotecaria</p>
                        <p>Este es un mensaje automático. Por favor no responder a esta casilla.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreCompleto, libro.getTitulo(), (libro.getAutor() != null ? libro.getAutor() : "Varios"), fechaVenc, facultadUrl, facultadNombre);

        return procesarYGuardar("RECORDATORIO_VENCIMIENTO", destinatario, nombreCompleto, asunto, html);
    }

    @Override
    public EmailLog enviarSalutacionCumpleanios(Persona persona) {
        String destinatario = persona.getEmail();
        String nombreCompleto = persona.getNombre() + " " + persona.getApellido();
        String asunto = "🎂 ¡Feliz Cumpleaños, " + persona.getNombre() + "! — Saludos de la Biblioteca Universitaria";

        String html = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #fdf2f8; margin: 0; padding: 20px; color: #1e293b; }
                    .container { max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(219,39,119,0.12); border: 1px solid #fbcfe8; }
                    .header { background: linear-gradient(135deg, #ec4899, #db2777, #be185d); color: #ffffff; padding: 35px 20px; text-align: center; }
                    .header h1 { margin: 0; font-size: 28px; font-weight: 800; }
                    .header p { margin: 8px 0 0 0; font-size: 16px; opacity: 0.95; }
                    .body { padding: 35px 30px; line-height: 1.7; text-align: center; }
                    .greeting { font-size: 20px; font-weight: 700; color: #be185d; margin-bottom: 15px; }
                    .message { font-size: 15px; color: #334155; margin-bottom: 25px; }
                    .quote-box { background: #fdf4ff; border: 1px dashed #f472b6; padding: 15px; border-radius: 10px; margin: 20px 0; font-style: italic; color: #86198f; }
                    .btn-container { margin: 30px 0 15px 0; }
                    .btn-facultad { display: inline-block; background: linear-gradient(135deg, #2563eb, #1d4ed8); color: #ffffff !important; text-decoration: none; padding: 14px 32px; border-radius: 30px; font-weight: 700; font-size: 16px; box-shadow: 0 4px 15px rgba(37,99,235,0.35); }
                    .footer { background: #f8fafc; padding: 25px 20px; text-align: center; font-size: 13px; color: #64748b; border-top: 1px solid #f1f5f9; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎉 ¡Feliz Cumpleaños! 🎂</h1>
                        <p>Saludos afectuosos de la Biblioteca Universitaria</p>
                    </div>
                    <div class="body">
                        <div class="greeting">¡Que tengas un día extraordinario, %s!</div>
                        <p class="message">
                            En nombre de todo el equipo de la Biblioteca y la comunidad académica, queremos hacerte llegar nuestras más cálidas felicitaciones en el día de tu cumpleaños.
                        </p>
                        <div class="quote-box">
                            "Un libro abierto es un cerebro que habla; cerrado, un amigo que espera; olvidado, un alma que perdona; destruido, un corazón que llora."
                        </div>
                        <p class="message">
                            Te deseamos un nuevo año de vida lleno de metas alcanzadas, nuevos descubrimientos y grandes lecturas. ¡Gracias por formar parte de nuestra facultad!
                        </p>
                        <div class="btn-container">
                            <a href="%s" target="_blank" class="btn-facultad">
                                🏛️ Ir a la Página de la Facultad (%s)
                            </a>
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>%s</strong></p>
                        <p>Sistema Automatizado de Notificaciones y Salutaciones</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreCompleto, facultadUrl, facultadNombre, facultadNombre);

        return procesarYGuardar("SALUTACION_CUMPLEANIOS", destinatario, nombreCompleto, asunto, html);
    }

    private EmailLog procesarYGuardar(String tipo, String destinatario, String nombreDestinatario, String asunto, String html) {
        String estado = "ENVIADO_SIMULADO";

        if (mailEnabled && mailSender != null) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(mailFrom);
                helper.setTo(destinatario);
                helper.setSubject(asunto);
                helper.setText(html, true);
                mailSender.send(message);
                estado = "ENVIADO_SMTP";
                log.info("📧 [EMAIL REAL ENVIADO] Tipo: {} | Destinatario: {}", tipo, destinatario);
            } catch (Exception ex) {
                log.error("⚠️ [ERROR SMTP] Error al enviar email por servidor SMTP: {}. Pasando a registro simulado.", ex.getMessage());
                estado = "ERROR_SMTP_SIMULADO";
            }
        }

        // Siempre mostrar en consola con formato destacado para auditoría y evaluación
        imprimirLogConsola(tipo, destinatario, nombreDestinatario, asunto, estado);

        EmailLog emailLog = new EmailLog(
                null,
                tipo,
                destinatario,
                nombreDestinatario,
                asunto,
                html,
                LocalDateTime.now(),
                estado
        );

        return emailLogRepository.save(emailLog);
    }

    private void imprimirLogConsola(String tipo, String destinatario, String nombre, String asunto, String estado) {
        System.out.println("\n" + "=".repeat(85));
        System.out.printf("📬 [SIMULADOR DE EMAIL AUTOMÁTICO] - Estado: %s%n", estado);
        System.out.printf("🏷️  Tipo:          %s%n", tipo);
        System.out.printf("👤 Destinatario:  %s (%s)%n", nombre, destinatario);
        System.out.printf("📝 Asunto:        %s%n", asunto);
        System.out.printf("🔗 Redirección:   %s%n", (tipo.equals("SALUTACION_CUMPLEANIOS") ? facultadUrl + " [Botón Facultad Embebido]" : "N/A"));
        System.out.printf("🕒 Fecha y Hora:  %s%n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("=".repeat(85) + "\n");
    }

    @Override
    public List<EmailLog> obtenerHistorial() {
        return emailLogRepository.findAllByOrderByFechaEnvioDesc();
    }
}
