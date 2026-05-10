import { useState } from "react";
//Iconos
import { IoIosLogIn } from "react-icons/io";
import { FaShieldAlt, FaEye, FaEyeSlash } from "react-icons/fa";

//Conexto de autentificación
import { useAuth } from "../../context/AuthContext";
import "./login.css";

/**
 * Componente Login
 *
 * Permite autenticar usuarios mediante:
 * - Email
 * - Contraseña
 *
 * Funcionalidades:
 * - Mostrar/Ocultar contraseña
 * - Consumo de API login
 * - Guardado de token
 * - Persistencia del usuario en contexto
 */

const Login = () => {

    // Estados del formulario
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    // Control visual de contraseña
    const [showPassword, setShowPassword] = useState(false);

    // Contexto global de usuario
    const { setUser } = useAuth();

    /**
    * Maneja el envío del formulario
    */

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email: email,
                    clave: password,
                }),
            });

            if (!response.ok) {
                throw new Error("Email o contraseña incorrectos");
            }

            const data = await response.json();

            localStorage.setItem("token", data.token);

            const userData = {
                email: data.email,
                rol: data.rol,
                nombres: data.nombres,
                apellidos: data.apellidos,
                token: data.token,
            };

            setUser(userData);

            console.log("Login exitoso:", userData);

        } catch (error) {
            console.error(error.message);
            alert(error.message);
        }
    };

    return (
        <div className="login-layout">
            <div className="login-card">
                <div className="logo">
                    <img src="/logo.jpeg" alt="logo" className="logo-img"/>
                    <div className="logo-text">
                        <span className="logo-top">Farmacias</span>
                        <span className="logo-bottom">Perú</span>
                    </div>
                </div>

                <h2><span>Bienvenido(a)</span> al sistema</h2>
                <p className="login-subtext">Ingresa con tus credenciales</p>

                <form onSubmit={handleSubmit}>
                    <label>Email</label>
                    <input
                        type="email"
                        placeholder="e.g: example@farmape.com"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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
                        <button type="submit">
                            <IoIosLogIn /> Iniciar sesión
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
}

export default Login;