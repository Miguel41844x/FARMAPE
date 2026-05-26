import "./usuarioForm.css";

import { useEffect, useRef, useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const UsuarioForms = ({
    cerrarFormulario,
    obtenerUsuarios,
    usuarioEditando = null,
}) => {
    const [showPassword, setShowPassword] = useState(false);

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

    const estados = ["Activo", "Bloqueado", "Inactivo"];

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
        idRol: "",
        rol: "",
        estado: "ACTIVO",
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
        if (!usuarioEditando) return;

        setFormData({
            idCuenta: usuarioEditando.idCuenta ?? null,
            idTrabajador: usuarioEditando.idTrabajador ?? null,
            dni: usuarioEditando.dni ?? "",
            nombres: usuarioEditando.nombres ?? "",
            apellidos: usuarioEditando.apellidos ?? "",
            telefono: usuarioEditando.telefono ?? "",
            direccion: usuarioEditando.direccion ?? "",
            usuario: usuarioEditando.usuario ?? usuarioEditando.cuenta?.usuario ?? "",
            email: usuarioEditando.email ?? usuarioEditando.correo ?? "",
            password: "",
            idRol: usuarioEditando.idRol ?? "",
            rol:
                usuarioEditando.rol?.nombre ??
                usuarioEditando.nombreRol ??
                usuarioEditando.rol ??
                "",
            estado: usuarioEditando.estado ?? "ACTIVO",
        });
    }, [usuarioEditando]);

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

        if (!esEdicion && !formData.usuario.trim()) {
            alert("Ingrese el usuario de acceso");
            return false;
        }

        if (!esEdicion && !formData.email.trim()) {
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
                            idRol: Number(formData.idRol),
                        }),
                    }
                );

                if (!responseTrabajador.ok) {
                    const errorText = await responseTrabajador.text();
                    throw new Error(errorText || "No se pudo actualizar el trabajador");
                }

                if (formData.estado) {
                    const responseEstado = await fetch(
                        `http://localhost:8080/api/trabajadores/${formData.idTrabajador}/estado`,
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

                    if (!responseEstado.ok) {
                        const errorText = await responseEstado.text();
                        throw new Error(errorText || "No se pudo actualizar el estado");
                    }
                }

                alert("Trabajador actualizado correctamente");

                if (obtenerUsuarios) {
                    obtenerUsuarios();
                }

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
                    idRol: Number(formData.idRol),
                    estado: formData.estado,
                }),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "No se pudo registrar el usuario");
            }

            alert("Usuario registrado correctamente");

            if (obtenerUsuarios) {
                obtenerUsuarios();
            }

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

                            <span
                                className="password-toggle"
                                onClick={() => setShowPassword(!showPassword)}
                            >
                                {showPassword ? <FaEyeSlash /> : <FaEye />}
                            </span>
                        </div>
                    </div>
                )}

                <button type="submit" className="usuario-submit-btn">
                    {esEdicion ? "Guardar cambios" : "Registrar usuario"}
                </button>
            </form>
        </div>
    );
};

export default UsuarioForms;