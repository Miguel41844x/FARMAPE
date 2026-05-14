import { useState } from "react";
import { useNavigate } from "react-router-dom";

// Iconos
import { IoIosLogIn } from "react-icons/io";
import { FaShieldAlt, FaEye, FaEyeSlash } from "react-icons/fa";

// Contexto de autentificación
import { useAuth } from "../../context/AuthContext";
import "./login.css";

const Login = () => {
    const [usuario, setUsuario] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);

    const { setUser } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!usuario.trim() || !password.trim()) {
            alert("Ingrese usuario y contraseña");
            return;
        }

        setLoading(true);

        try {
            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    usuario: usuario,
                    clave: password,
                }),
            });

            if (!response.ok) {
                throw new Error("Usuario o contraseña incorrectos");
            }

            const data = await response.json();

            const userData = {
                usuario: data.usuario,
                rol: data.rol,
                nombres: data.nombres,
                apellidos: data.apellidos,
                token: data.token,
            };
            
            localStorage.setItem("token", data.token);
            localStorage.setItem("usuario", data.usuario);
            localStorage.setItem("rol", data.rol);
            localStorage.setItem("nombres", data.nombres);
            localStorage.setItem("apellidos", data.apellidos);

            setUser(userData);

            console.log("Login exitoso:", userData);

            navigate("/homePrivate");
        } catch (error) {
            console.error(error.message);
            alert(error.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-layout">
            <div className="login-card">
                <div className="logo">
                    <img src="/logo.jpeg" alt="logo" className="logo-img" />
                    <div className="logo-text">
                        <span className="logo-top">Farmacias</span>
                        <span className="logo-bottom">Perú</span>
                    </div>
                </div>

                <h2><span>Bienvenido(a)</span> al sistema</h2>
                <p className="login-subtext">Ingresa con tus credenciales</p>

                <form onSubmit={handleSubmit}>
                    <label>Usuario</label>
                    <input
                        type="text"
                        placeholder="e.g: admin@farmape.com.pe"
                        value={usuario}
                        onChange={(e) => setUsuario(e.target.value)}
                    />

                    <label>Contraseña</label>

                    <div className="password-container">
                        <input
                            type={showPassword ? "text" : "password"}
                            placeholder="Ingrese su contraseña"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        <span
                            className="password-toggle"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? <FaEyeSlash /> : <FaEye />}
                        </span>
                    </div>

                    <p className="login-help">
                        ¿Olvidaste tu contraseña?
                        <span> Comunícate con un administrador</span>
                    </p>

                    <div className="login-button">
                        <button type="submit" disabled={loading}>
                            <IoIosLogIn />
                            {loading ? " Iniciando..." : " Iniciar sesión"}
                        </button>
                    </div>
                </form>

                <div className="login-footer">
                    <p className="login-secure">
                        <FaShieldAlt /> Acceso seguro y protegido
                    </p>
                    <p className="login-copy">
                        © 2026 Farmacéuticas Perú - GRUPO 7 S.A.C
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Login;