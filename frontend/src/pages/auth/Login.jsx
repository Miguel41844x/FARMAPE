import { useState } from "react";
import { IoIosLogIn } from "react-icons/io";
import { FaShieldAlt, FaEye, FaEyeSlash } from "react-icons/fa";
import "./login.css";

const Login = () => {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log(email, password);
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