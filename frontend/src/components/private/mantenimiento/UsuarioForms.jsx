import "./usuarioForm.css";
import { useState } from "react";
import { FaEye, FaEyeSlash } from "react-icons/fa";

const UsuarioForms = ({ cerrarFormulario }) => {
    const [showPassword, setShowPassword] = useState(false);

    const [menuRolAbierto, setMenuRolAbierto] = useState(false);

    const roles = [
        "Administrador",
        "Empleado",
        "Cajero",
        "Encargado de Despacho",
        "Encargado de Almacen",
        "Quimico Farmaceutico",
        "Gerente",
    ];

    const seleccionarRol = (rol) => {
        setFormData({
            ...formData,
            rol,
        });

        setMenuRolAbierto(false);
    };

    const [formData, setFormData] = useState({
        dni: "",
        nombres: "",
        apellidos: "",
        telefono: "",
        direccion: "",
        email: "",
        password: "",
        confirmarPassword: "",
        rol: "",
    });

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (formData.password !== formData.confirmarPassword) {
            alert("Las contraseñas no coinciden");
            return;
        }

        try {
            const token = localStorage.getItem("token");

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
                    usuario: formData.email,
                    clave: formData.password,
                    rol: formData.rol,
                }),
            });

            if (!response.ok) {
                throw new Error("No se pudo registrar el usuario");
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
                <h2>Registrar usuario</h2>
                <p>Completa los datos del nuevo usuario</p>
            </div>

            <form className="usuario-form" onSubmit={handleSubmit}>
                <div className="form-group">
                    <label>DNI</label>
                    <input name="dni" value={formData.dni} placeholder="77777777" onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>Nombres</label>
                    <input name="nombres" value={formData.nombres} placeholder="Juan" onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>Apellidos</label>
                    <input name="apellidos" value={formData.apellidos} placeholder="Ramos Martines" onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>Teléfono</label>
                    <input name="telefono" value={formData.telefono} placeholder="985632154" onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>Dirección</label>
                    <input name="direccion" value={formData.direccion} placeholder="Av. Brasil" onChange={handleChange} />
                </div>

                <div className="form-group rol-dropdown-wrapper">
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
                    <label>Email</label>
                    <input type="email" name="email" value={formData.email} placeholder="example@gmail.com" onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>Contraseña</label>
                    <div className="password-container">
                        <input
                            type={showPassword ? "text" : "password"}
                            name="password"
                            value={formData.password}
                            placeholder="**********"
                            onChange={handleChange}
                        />
                        <span className="password-toggle" onClick={() => setShowPassword(!showPassword)}>
                            {showPassword ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>
                </div>


                <button type="submit" className="usuario-submit-btn">
                    Registrar usuario
                </button>
            </form>
        </div>
    );
};

export default UsuarioForms;