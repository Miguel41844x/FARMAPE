import "./usuarioForm.css";

import { useEffect, useRef, useState } from "react";
import { FaEye, FaEyeSlash, FaTimes } from "react-icons/fa";
import FeedbackMessage from "../../../common/FeedbackMessage";
import {
    crearUsuario,
    obtenerRoles,
    actualizarUsuarioCompleto,
} from "../../../../services/mantenimiento/usuarioService";

const crearEstadoInicial = (usuario = null) => ({
    idCuenta: usuario?.idCuenta ?? null,
    idTrabajador: usuario?.idTrabajador ?? null,
    dni: usuario?.dni ?? "",
    nombres: usuario?.nombres ?? "",
    apellidos: usuario?.apellidos ?? "",
    telefono: usuario?.telefono ?? "",
    direccion: usuario?.direccion ?? "",
    usuario: usuario?.usuario ?? usuario?.cuenta?.usuario ?? "",
    email: usuario?.email ?? usuario?.correo ?? usuario?.cuenta?.email ?? "",
    password: "",
    idRol: usuario?.idRol ?? usuario?.rol?.idRol ?? "",
    rol: usuario?.rol?.nombre ?? usuario?.nombreRol ?? usuario?.rol ?? "",
    estado: usuario?.estado ?? "Activo",
});

const UsuarioForms = ({ cerrarFormulario, obtenerUsuarios, usuarioEditando = null }) => {
    const [showPassword, setShowPassword] = useState(false);
    const [guardando, setGuardando] = useState(false);
    const [feedback, setFeedback] = useState(null);
    const [menuRolAbierto, setMenuRolAbierto] = useState(false);
    const [menuEstadoAbierto, setMenuEstadoAbierto] = useState(false);
    const [roles, setRoles] = useState([]);
    const rolDropdownRef = useRef(null);
    const estadoDropdownRef = useRef(null);

    const esEdicion = Boolean(usuarioEditando);
    const estados = ["Activo", "Bloqueado", "Inactivo"];
    const [formData, setFormData] = useState(() => crearEstadoInicial(usuarioEditando));

    useEffect(() => {
        obtenerRoles()
            .then(setRoles)
            .catch((error) => setFeedback({ type: "error", message: error.message }));
    }, []);

    useEffect(() => {
        const cerrarMenus = (e) => {
            if (rolDropdownRef.current && !rolDropdownRef.current.contains(e.target)) setMenuRolAbierto(false);
            if (estadoDropdownRef.current && !estadoDropdownRef.current.contains(e.target)) setMenuEstadoAbierto(false);
        };
        document.addEventListener("mousedown", cerrarMenus);
        return () => document.removeEventListener("mousedown", cerrarMenus);
    }, []);

    const seleccionarRol = (rol) => {
        setFormData((prev) => ({ ...prev, idRol: rol.idRol, rol: rol.nombreRol }));
        setMenuRolAbierto(false);
    };

    const seleccionarEstado = (estado) => {
        setFormData((prev) => ({ ...prev, estado }));
        setMenuEstadoAbierto(false);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({ ...prev, [name]: value }));
    };

    const validarFormulario = () => {
        if (!/^\d{8,11}$/.test(formData.dni.trim())) return "El DNI/RUC debe contener entre 8 y 11 dígitos";
        if (!formData.nombres.trim()) return "Ingrese los nombres";
        if (!formData.apellidos.trim()) return "Ingrese los apellidos";
        if (!formData.idRol) return "Seleccione un rol";
        if (!formData.usuario.trim()) return "Ingrese el usuario de acceso";
        if (!formData.email.trim()) return "Ingrese el email";
        if (!/^\S+@\S+\.\S+$/.test(formData.email.trim())) return "Ingrese un email válido";
        if (!esEdicion && !formData.password.trim()) return "Ingrese una contraseña";
        if (formData.password && formData.password.length < 6) return "La contraseña debe tener al menos 6 caracteres";
        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const errorValidacion = validarFormulario();
        if (errorValidacion) {
            setFeedback({ type: "warning", message: errorValidacion });
            return;
        }

        const payload = {
            dni: formData.dni.trim(),
            nombres: formData.nombres.trim(),
            apellidos: formData.apellidos.trim(),
            telefono: formData.telefono.trim(),
            direccion: formData.direccion.trim(),
            usuario: formData.usuario.trim(),
            email: formData.email.trim(),
            idRol: Number(formData.idRol),
            estado: formData.estado,
            ...(formData.password.trim() ? { nuevaClave: formData.password } : {}),
        };

        try {
            setGuardando(true);
            setFeedback(null);

            if (esEdicion) {
                await actualizarUsuarioCompleto(formData.idCuenta, payload);
            } else {
                await crearUsuario({
                    ...payload,
                    clave: formData.password,
                });
            }

            if (obtenerUsuarios) await obtenerUsuarios();
            setFeedback({ type: "success", message: esEdicion ? "Usuario actualizado correctamente" : "Usuario registrado correctamente" });
            setTimeout(cerrarFormulario, 450);
        } catch (error) {
            setFeedback({ type: "error", message: error.message });
        } finally {
            setGuardando(false);
        }
    };

    return (
        <div className="usuario-form-container">
            <div className="usuario-form-header">
                <div>
                    <h2>{esEdicion ? "Editar usuario" : "Registrar usuario"}</h2>
                    <p>{esEdicion ? "Actualiza datos personales, acceso, rol, estado y contraseña opcional." : "Completa los datos del nuevo usuario del sistema."}</p>
                </div>
                <button type="button" className="usuario-form-close" onClick={cerrarFormulario} aria-label="Cerrar formulario">
                    <FaTimes />
                </button>
            </div>

            {feedback && <FeedbackMessage type={feedback.type} message={feedback.message} onClose={() => setFeedback(null)} />}

            <form className="usuario-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>DNI</label>
                    <input name="dni" value={formData.dni} placeholder="Ingrese DNI" onChange={handleChange} inputMode="numeric" minLength="8" maxLength="11" pattern="\d{8,11}" title="Ingrese entre 8 y 11 dígitos" autoFocus required />
                </div>
                <div className="form-group">
                    <label>Nombres</label>
                    <input name="nombres" value={formData.nombres} placeholder="Ingrese nombres" onChange={handleChange} maxLength="100" required />
                </div>
                <div className="form-group">
                    <label>Apellidos</label>
                    <input name="apellidos" value={formData.apellidos} placeholder="Ingrese apellidos" onChange={handleChange} maxLength="100" required />
                </div>
                <div className="form-group">
                    <label>Teléfono</label>
                    <input name="telefono" value={formData.telefono} placeholder="Ingrese teléfono" onChange={handleChange} inputMode="tel" maxLength="20" />
                </div>
                <div className="form-group">
                    <label>Dirección</label>
                    <input name="direccion" value={formData.direccion} placeholder="Ingrese dirección" onChange={handleChange} maxLength="150" />
                </div>
                <div className="form-group">
                    <label>Usuario</label>
                    <input name="usuario" value={formData.usuario} placeholder="Ingrese usuario de acceso" onChange={handleChange} maxLength="50" autoComplete="username" required />
                </div>
                <div className="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value={formData.email} placeholder="Ingrese correo electrónico" onChange={handleChange} maxLength="100" autoComplete="email" required />
                </div>

                <div className="form-group rol-dropdown-wrapper" ref={rolDropdownRef}>
                    <label>Rol</label>
                    <button type="button" className="rol-dropdown-btn" onClick={() => setMenuRolAbierto(!menuRolAbierto)}>
                        <span>{formData.rol || "Seleccione un rol"}</span>
                        <span className={`rol-dropdown-arrow ${menuRolAbierto ? "open" : ""}`}>▾</span>
                    </button>
                    {menuRolAbierto && (
                        <div className="rol-dropdown-menu">
                            {roles.map((rol) => (
                                <button key={rol.idRol} type="button" className={Number(formData.idRol) === rol.idRol ? "active" : ""} onClick={() => seleccionarRol(rol)}>
                                    {rol.nombreRol}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                <div className="form-group rol-dropdown-wrapper" ref={estadoDropdownRef}>
                    <label>Estado</label>
                    <button type="button" className="rol-dropdown-btn" onClick={() => setMenuEstadoAbierto(!menuEstadoAbierto)}>
                        <span>{formData.estado || "Seleccione un estado"}</span>
                        <span className={`rol-dropdown-arrow ${menuEstadoAbierto ? "open" : ""}`}>▾</span>
                    </button>
                    {menuEstadoAbierto && (
                        <div className="rol-dropdown-menu">
                            {estados.map((estado) => (
                                <button key={estado} type="button" className={formData.estado === estado ? "active" : ""} onClick={() => seleccionarEstado(estado)}>
                                    {estado}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                <div className="form-group">
                    <label>{esEdicion ? "Nueva contraseña (opcional)" : "Contraseña"}</label>
                    <div className="password-container">
                        <input type={showPassword ? "text" : "password"} name="password" value={formData.password} placeholder={esEdicion ? "Dejar en blanco para conservar" : "Ingrese contraseña"} onChange={handleChange} minLength={6} maxLength={100} autoComplete="new-password" required={!esEdicion} />
                        <button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? "Ocultar contraseña" : "Mostrar contraseña"}>
                            {showPassword ? <FaEyeSlash /> : <FaEye />}
                        </button>
                    </div>
                </div>

                <button type="submit" className="usuario-submit-btn" disabled={guardando}>
                    {guardando ? "Guardando..." : esEdicion ? "Guardar cambios" : "Registrar usuario"}
                </button>
            </form>
        </div>
    );
};

export default UsuarioForms;
