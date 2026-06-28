import { useState } from "react";
import { Link } from "react-router-dom";
import { FaShieldAlt } from "react-icons/fa";
import { solicitarRestablecimiento } from "../../services/auth/authService";
import FeedbackMessage from "../../components/common/FeedbackMessage";
import "./forgotPassword.css";

const ForgotPassword = () => {
    const [usuarioOCorreo, setUsuarioOCorreo] = useState("");
    const [mensaje, setMensaje] = useState("");
    const [loading, setLoading] = useState(false);
    const [feedback, setFeedback] = useState(null);

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (!usuarioOCorreo.trim()) {
            setFeedback({ type: "warning", message: "Ingresa tu usuario o correo para registrar la solicitud." });
            return;
        }

        setLoading(true);
        setFeedback(null);
        try {
            const response = await solicitarRestablecimiento({ usuarioOCorreo, mensaje });
            setFeedback({ type: "success", title: "Solicitud registrada", message: response.mensaje });
            setUsuarioOCorreo("");
            setMensaje("");
        } catch (error) {
            setFeedback({ type: "error", title: "No se pudo registrar", message: error.message });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="forgot-layout">
            <section className="forgot-card">
                <div className="forgot-logo">
                    <img src="/logo.jpeg" alt="Farmacias Perú" />
                    <div>
                        <strong>Farmacias Perú</strong>
                        <span>Soporte de acceso</span>
                    </div>
                </div>

                <h1>Recuperar acceso</h1>
                <p className="forgot-subtitle">
                    Este flujo registra una solicitud interna. No envía correos automáticamente; el administrador verificará tu identidad y restablecerá tu clave si corresponde.
                </p>

                {feedback && (
                    <FeedbackMessage
                        type={feedback.type}
                        title={feedback.title}
                        message={feedback.message}
                        onClose={() => setFeedback(null)}
                    />
                )}

                <form onSubmit={handleSubmit} className="forgot-form">
                    <label>
                        Usuario o correo
                        <input
                            value={usuarioOCorreo}
                            onChange={(event) => setUsuarioOCorreo(event.target.value)}
                            placeholder="Ejemplo: cajero01 o cajero01@farmaceuticasperu.com"
                            maxLength={100}
                            required
                            autoComplete="username"
                        />
                    </label>

                    <label>
                        Mensaje para el administrador
                        <textarea
                            value={mensaje}
                            onChange={(event) => setMensaje(event.target.value)}
                            placeholder="Opcional: indica tu área o motivo de la solicitud"
                            maxLength={300}
                        />
                    </label>

                    <button type="submit" disabled={loading}>
                        {loading ? "Registrando solicitud..." : "Enviar solicitud"}
                    </button>
                </form>

                <div className="forgot-footer">
                    <p><FaShieldAlt /> El administrador gestionará la solicitud desde mantenimiento de usuarios.</p>
                    <Link to="/login">Volver al inicio de sesión</Link>
                </div>
            </section>
        </div>
    );
};

export default ForgotPassword;
