import { useEffect, useState } from "react";
import { actualizarMiPerfil, obtenerMiPerfil } from "../../../services/perfil/perfilService";
import { useAuth } from "../../../context/AuthContext";
import "./userProfileModal.css";

const initialForm = {
    email: "",
    nombres: "",
    apellidos: "",
    telefono: "",
    direccion: "",
};

const UserProfileModal = ({ open, onClose }) => {
    const { updateUserProfile } = useAuth();
    const [form, setForm] = useState(initialForm);
    const [loading, setLoading] = useState(false);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [readOnlyData, setReadOnlyData] = useState({ usuario: "", dni: "", rol: "" });

    useEffect(() => {
        if (!open) return;

        const cargarPerfil = async () => {
            try {
                setLoading(true);
                setError("");
                setSuccess("");
                const perfil = await obtenerMiPerfil();
                setForm({
                    email: perfil.email || "",
                    nombres: perfil.nombres || "",
                    apellidos: perfil.apellidos || "",
                    telefono: perfil.telefono || "",
                    direccion: perfil.direccion || "",
                });
                setReadOnlyData({
                    usuario: perfil.usuario || "",
                    dni: perfil.dni || "",
                    rol: perfil.rol || "",
                });
            } catch (err) {
                setError(err.message || "No se pudo cargar tu perfil");
            } finally {
                setLoading(false);
            }
        };

        cargarPerfil();
    }, [open]);

    if (!open) return null;

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        try {
            setSaving(true);
            setError("");
            setSuccess("");
            const perfilActualizado = await actualizarMiPerfil(form);
            updateUserProfile(perfilActualizado);
            setSuccess("Perfil actualizado correctamente");
        } catch (err) {
            setError(err.message || "No se pudo actualizar tu perfil");
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="profile-modal-backdrop" onMouseDown={onClose}>
            <section className="profile-modal" onMouseDown={(event) => event.stopPropagation()}>
                <header className="profile-modal-header">
                    <div>
                        <h2>Mi perfil</h2>
                        <p>Actualiza tus datos personales de usuario.</p>
                    </div>
                    <button className="profile-modal-close" type="button" onClick={onClose}>×</button>
                </header>

                {loading ? (
                    <p className="profile-modal-loading">Cargando perfil...</p>
                ) : (
                    <form className="profile-form" onSubmit={handleSubmit}>
                        <div className="profile-readonly-grid">
                            <label>
                                Usuario
                                <input value={readOnlyData.usuario} disabled />
                            </label>
                            <label>
                                Rol
                                <input value={readOnlyData.rol} disabled />
                            </label>
                            <label>
                                DNI
                                <input value={readOnlyData.dni} disabled />
                            </label>
                        </div>

                        <div className="profile-form-grid">
                            <label>
                                Nombres
                                <input
                                    name="nombres"
                                    value={form.nombres}
                                    onChange={handleChange}
                                    maxLength={100}
                                    required
                                />
                            </label>

                            <label>
                                Apellidos
                                <input
                                    name="apellidos"
                                    value={form.apellidos}
                                    onChange={handleChange}
                                    maxLength={100}
                                    required
                                />
                            </label>

                            <label>
                                Email
                                <input
                                    name="email"
                                    type="email"
                                    value={form.email}
                                    onChange={handleChange}
                                    maxLength={100}
                                    required
                                />
                            </label>

                            <label>
                                Teléfono
                                <input
                                    name="telefono"
                                    value={form.telefono}
                                    onChange={handleChange}
                                    maxLength={20}
                                    inputMode="tel"
                                    autoComplete="tel"
                                />
                            </label>

                            <label className="profile-field-full">
                                Dirección
                                <input
                                    name="direccion"
                                    value={form.direccion}
                                    onChange={handleChange}
                                    maxLength={150}
                                    autoComplete="street-address"
                                />
                            </label>
                        </div>

                        {error && <p className="profile-message error">{error}</p>}
                        {success && <p className="profile-message success">{success}</p>}

                        <footer className="profile-modal-actions">
                            <button type="button" className="profile-btn secondary" onClick={onClose}>Cancelar</button>
                            <button type="submit" className="profile-btn primary" disabled={saving}>
                                {saving ? "Guardando..." : "Guardar cambios"}
                            </button>
                        </footer>
                    </form>
                )}
            </section>
        </div>
    );
};

export default UserProfileModal;
