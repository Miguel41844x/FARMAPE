import "./usuarioForm.css";

import { useEffect, useRef, useState } from "react";
import { FaEye, FaEyeSlash, FaTimes } from "react-icons/fa";

import {
    crearUsuario,
    actualizarTrabajador,
    actualizarEstadoTrabajador,
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
    estado: String(usuario?.estado ?? "ACTIVO").toUpperCase(),
});

const UsuarioForms = ({
    cerrarFormulario,
    obtenerUsuarios,
    usuarioEditando = null,
}) => {
    const [showPassword, setShowPassword] = useState(false);
    const [guardando, setGuardando] = useState(false);

    const [menuRolAbierto, setMenuRolAbierto] = useState(false);
    const rolDropdownRef = useRef(null);

    const [menuEstadoAbierto, setMenuEstadoAbierto] = useState(false);
    const estadoDropdownRef = useRef(null);

    const esEdicion = Boolean(usuarioEditando);

    const roles = [
        { idRol: 1, nombreRol: "Administrador" },
        { idRol: 2, nombreRol: "Empleado" },
        { idRol: 3, nombreRol: "Cajero" },
        { idRol: 4, nombreRol: "Encargado de Despacho" },
        { idRol: 5, nombreRol: "Encargado de Almacen" },
        { idRol: 6, nombreRol: "Quimico Farmaceutico" },
        { idRol: 7, nombreRol: "Gerente" },
    ];

    const estados = ["ACTIVO", "BLOQUEADO", "INACTIVO"];

    const [formData, setFormData] = useState(() =>
        crearEstadoInicial(usuarioEditando)
    );

    useEffect(() => {
        const cerrarMenus = (e) => {
            if (
                rolDropdownRef.current &&
                !rolDropdownRef.current.contains(e.target)
            ) {
                setMenuRolAbierto(false);
            }

            if (
                estadoDropdownRef.current &&
                !estadoDropdownRef.current.contains(e.target)
            ) {
                setMenuEstadoAbierto(false);
            }
        };

        document.addEventListener("mousedown", cerrarMenus);

        return () => {
            document.removeEventListener("mousedown", cerrarMenus);
        };
    }, []);

    const seleccionarRol = (rol) => {
        setFormData((prev) => ({
            ...prev,
            idRol: rol.idRol,
            rol: rol.nombreRol,
        }));

        setMenuRolAbierto(false);
    };

    const seleccionarEstado = (estado) => {
        setFormData((prev) => ({
            ...prev,
            estado,
        }));

        setMenuEstadoAbierto(false);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const validarFormulario = () => {
        if (!/^\d{8}$/.test(formData.dni.trim())) {
            alert("El DNI debe contener 8 dígitos");
            return false;
        }

        if (!formData.nombres.trim()) {
            alert("Ingrese los nombres");
            return false;
        }

        if (!formData.apellidos.trim()) {
            alert("Ingrese los apellidos");
            return false;
        }

        if (!formData.rol.trim()) {
            alert("Seleccione un rol");
            return false;
        }

        if (!esEdicion && !formData.usuario.trim()) {
            alert("Ingrese el usuario de acceso");
            return false;
        }

        if (!esEdicion && !formData.email.trim()) {
            alert("Ingrese el email");
            return false;
        }

        if (!esEdicion && !/^\S+@\S+\.\S+$/.test(formData.email.trim())) {
            alert("Ingrese un email válido");
            return false;
        }

        if (!esEdicion && !formData.password.trim()) {
            alert("Ingrese una contraseña");
            return false;
        }

        return true;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validarFormulario()) return;

        try {
            setGuardando(true);

            if (esEdicion) {
                await actualizarTrabajador(formData.idTrabajador, {
                    dni: formData.dni.trim(),
                    nombres: formData.nombres.trim(),
                    apellidos: formData.apellidos.trim(),
                    telefono: formData.telefono.trim(),
                    direccion: formData.direccion.trim(),
                    idRol: Number(formData.idRol),
                });

                if (formData.estado) {
                    await actualizarEstadoTrabajador(
                        formData.idTrabajador,
                        formData.estado
                    );
                }

                alert("Trabajador actualizado correctamente");

                if (obtenerUsuarios) {
                    await obtenerUsuarios();
                }

                cerrarFormulario();
                return;
            }

            await crearUsuario({
                dni: formData.dni.trim(),
                nombres: formData.nombres.trim(),
                apellidos: formData.apellidos.trim(),
                telefono: formData.telefono.trim(),
                direccion: formData.direccion.trim(),
                usuario: formData.usuario.trim(),
                email: formData.email.trim(),
                clave: formData.password,
                idRol: Number(formData.idRol),
                estado: formData.estado,
            });

            alert("Usuario registrado correctamente");

            if (obtenerUsuarios) {
                await obtenerUsuarios();
            }

            cerrarFormulario();
        } catch (error) {
            console.error(error);
            alert(error.message);
        } finally {
            setGuardando(false);
        }
    };

    return (
        <div className="usuario-form-container">
            <div className="usuario-form-header">
                <div>
                    <h2>{esEdicion ? "Editar usuario" : "Registrar usuario"}</h2>
                    <p>
                        {esEdicion
                            ? "Actualiza los datos del usuario seleccionado"
                            : "Completa los datos del nuevo usuario"}
                    </p>
                </div>
                <button
                    type="button"
                    className="usuario-form-close"
                    onClick={cerrarFormulario}
                    aria-label="Cerrar formulario"
                >
                    <FaTimes />
                </button>
            </div>

            <form className="usuario-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>DNI</label>
                    <input
                        name="dni"
                        value={formData.dni}
                        placeholder="Ingrese DNI"
                        onChange={handleChange}
                        inputMode="numeric"
                        maxLength="8"
                        autoFocus
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Nombres</label>
                    <input
                        name="nombres"
                        value={formData.nombres}
                        placeholder="Ingrese nombres"
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Apellidos</label>
                    <input
                        name="apellidos"
                        value={formData.apellidos}
                        placeholder="Ingrese apellidos"
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label>Teléfono</label>
                    <input
                        name="telefono"
                        value={formData.telefono}
                        placeholder="Ingrese teléfono"
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label>Dirección</label>
                    <input
                        name="direccion"
                        value={formData.direccion}
                        placeholder="Ingrese dirección"
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group rol-dropdown-wrapper" ref={rolDropdownRef}>
                    <label>Rol</label>

                    <button
                        type="button"
                        className="rol-dropdown-btn"
                        onClick={() => setMenuRolAbierto(!menuRolAbierto)}
                    >
                        <span>{formData.rol || "Seleccione un rol"}</span>
                        <span className={`rol-dropdown-arrow ${menuRolAbierto ? "open" : ""}`}>
                            ▾
                        </span>
                    </button>

                    {menuRolAbierto && (
                        <div className="rol-dropdown-menu">
                            {roles.map((rol) => (
                                <button
                                    key={rol.idRol}
                                    type="button"
                                    className={Number(formData.idRol) === rol.idRol ? "active" : ""}
                                    onClick={() => seleccionarRol(rol)}
                                >
                                    {rol.nombreRol}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

                {!esEdicion && (
                    <>
                        <div className="form-group">
                            <label>Usuario</label>
                            <input
                                name="usuario"
                                value={formData.usuario}
                                placeholder="Ingrese usuario de acceso"
                                onChange={handleChange}
                            />
                        </div>

                        <div className="form-group">
                            <label>Email</label>
                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                placeholder="Ingrese correo electrónico"
                                onChange={handleChange}
                            />
                        </div>
                    </>
                )}

                {esEdicion && (
                    <div className="form-group rol-dropdown-wrapper" ref={estadoDropdownRef}>
                        <label>Estado</label>

                        <button
                            type="button"
                            className="rol-dropdown-btn"
                            onClick={() => setMenuEstadoAbierto(!menuEstadoAbierto)}
                        >
                            <span>{formData.estado || "Seleccione un estado"}</span>
                            <span className={`rol-dropdown-arrow ${menuEstadoAbierto ? "open" : ""}`}>
                                ▾
                            </span>
                        </button>

                        {menuEstadoAbierto && (
                            <div className="rol-dropdown-menu">
                                {estados.map((estado) => (
                                    <button
                                        key={estado}
                                        type="button"
                                        className={formData.estado === estado ? "active" : ""}
                                        onClick={() => seleccionarEstado(estado)}
                                    >
                                        {estado}
                                    </button>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {!esEdicion && (
                    <div className="form-group">
                        <label>Contraseña</label>

                        <div className="password-container">
                            <input
                                type={showPassword ? "text" : "password"}
                                name="password"
                                value={formData.password}
                                placeholder="Ingrese contraseña"
                                onChange={handleChange}
                            />

                            <button
                                type="button"
                                className="password-toggle"
                                onClick={() => setShowPassword(!showPassword)}
                                aria-label={showPassword ? "Ocultar contraseña" : "Mostrar contraseña"}
                            >
                                {showPassword ? <FaEyeSlash /> : <FaEye />}
                            </button>
                        </div>
                    </div>
                )}

                <button type="submit" className="usuario-submit-btn" disabled={guardando}>
                    {guardando
                        ? "Guardando..."
                        : esEdicion ? "Guardar cambios" : "Registrar usuario"}
                </button>
            </form>
        </div>
    );
};

export default UsuarioForms;
