import "./usuarioForm.css";

import { useEffect, useRef, useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const UsuarioForms = ({ cerrarFormulario, usuarioEditar = null }) => {
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const [menuRolAbierto, setMenuRolAbierto] = useState(false);
    const rolDropdownRef = useRef(null);

    const [menuEstadoAbierto, setMenuEstadoAbierto] = useState(false);
    const estadoDropdownRef = useRef(null);

    const esEdicion = Boolean(usuarioEditar);

    const roles = [
        "Administrador",
        "Empleado",
        "Cajero",
        "Encargado de Despacho",
        "Encargado de Almacen",
        "Quimico Farmaceutico",
        "Gerente",
    ];

    const estados = ["Activo", "Bloqueado", "Inactivo"];

    const seleccionarEstado = (estado) => {
        setFormData((prev) => ({
            ...prev,
            estado,
        }));

        setMenuEstadoAbierto(false);
    };

    const [formData, setFormData] = useState({
        idCuenta: null,
        idTrabajador: null,
        dni: "",
        nombres: "",
        apellidos: "",
        telefono: "",
        direccion: "",
        usuario: "",
        email: "",
        password: "",
        rol: "",
        estado: "Activo",
    });

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

    useEffect(() => {
        if (!usuarioEditar) return;

        setFormData({
            idCuenta: usuarioEditar.idCuenta ?? null,
            idTrabajador: usuarioEditar.idTrabajador ?? null,
            dni: usuarioEditar.dni ?? "",
            nombres: usuarioEditar.nombres ?? "",
            apellidos: usuarioEditar.apellidos ?? "",
            telefono: usuarioEditar.telefono ?? "",
            direccion: usuarioEditar.direccion ?? "",
            usuario: usuarioEditar.usuario ?? "",
            email: usuarioEditar.email ?? "",
            password: "",
            rol: usuarioEditar.rol ?? "",
            estado: usuarioEditar.estado ?? "Activo",
        });
    }, [usuarioEditar]);

    const seleccionarRol = (rol) => {
        setFormData((prev) => ({
            ...prev,
            rol,
        }));

        setMenuRolAbierto(false);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const validarFormulario = () => {
        if (!formData.dni.trim()) {
            alert("Ingrese el DNI");
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

        if (!formData.usuario.trim()) {
            alert("Ingrese el usuario de acceso");
            return false;
        }

        if (!formData.email.trim()) {
            alert("Ingrese el email");
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
            const token = localStorage.getItem("token");

            if (esEdicion) {
                
                const responseTrabajador = await fetch(
                    `http://localhost:8080/api/trabajadores/${formData.idTrabajador}`,
                    {
                        method: "PUT",
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${token}`,
                        },
                        body: JSON.stringify({
                            dni: formData.dni,
                            nombres: formData.nombres,
                            apellidos: formData.apellidos,
                            telefono: formData.telefono,
                            direccion: formData.direccion,
                            rol: formData.rol,
                            estado: formData.estado,
                        }),
                    }
                );

                if (!responseTrabajador.ok) {
                    const errorText = await responseTrabajador.text();
                    throw new Error(errorText || "No se pudo actualizar el trabajador");
                }

                const responseUsuario = await fetch(
                    `http://localhost:8080/api/usuarios/${formData.idCuenta}/estado`,
                    {
                        method: "PATCH",
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${token}`,
                        },
                        body: JSON.stringify({
                            estado: formData.estado,
                        }),
                    }
                );

                if (!responseUsuario.ok) {
                    const errorText = await responseUsuario.text();
                    throw new Error(errorText || "No se pudo actualizar el estado del usuario");
                }

                alert("Usuario actualizado correctamente");
                cerrarFormulario();
                return;
            }

            const response = await fetch("http://localhost:8080/api/usuarios", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    dni: formData.dni,
                    nombres: formData.nombres,
                    apellidos: formData.apellidos,
                    telefono: formData.telefono,
                    direccion: formData.direccion,
                    usuario: formData.usuario,
                    email: formData.email,
                    clave: formData.password,
                    rol: formData.rol,
                    estado: formData.estado,
                }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "No se pudo registrar el usuario");
            }

            alert("Usuario registrado correctamente");
            cerrarFormulario();
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    return (
        <div className="usuario-form-container">
            <div className="usuario-form-header">
                <h2>{esEdicion ? "Editar usuario" : "Registrar usuario"}</h2>
                <p>
                    {esEdicion
                        ? "Actualiza los datos del usuario seleccionado"
                        : "Completa los datos del nuevo usuario"}
                </p>
            </div>

            <form className="usuario-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>DNI</label>
                    <input
                        name="dni"
                        value={formData.dni}
                        placeholder="Ingrese DNI"
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label>Nombres</label>
                    <input
                        name="nombres"
                        value={formData.nombres}
                        placeholder="Ingrese nombres"
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label>Apellidos</label>
                    <input
                        name="apellidos"
                        value={formData.apellidos}
                        placeholder="Ingrese apellidos"
                        onChange={handleChange}
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
                                    key={rol}
                                    type="button"
                                    className={formData.rol === rol ? "active" : ""}
                                    onClick={() => seleccionarRol(rol)}
                                >
                                    {rol}
                                </button>
                            ))}
                        </div>
                    )}
                </div>

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

                <div className="form-group">
                    <label>
                        {esEdicion
                            ? "Nueva contraseña (opcional)"
                            : "Contraseña"}
                    </label>

                    <div className="password-container">
                        <input
                            type={showPassword ? "text" : "password"}
                            name="password"
                            value={formData.password}
                            placeholder={
                                esEdicion
                                    ? "Dejar vacío para mantener la actual"
                                    : "Ingrese contraseña"
                            }
                            onChange={handleChange}
                        />

                        <span
                            className="password-toggle"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>
                </div>

                <button type="submit" className="usuario-submit-btn">
                    {esEdicion ? "Guardar cambios" : "Registrar usuario"}
                </button>
            </form>
        </div>
    );
};

export default UsuarioForms;