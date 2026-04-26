import { useState } from "react";
import "./login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log(email, password);
  };

    return (
        <div className="login-layout">
            <div className="login-image">
                <div className="overlay">
                    <h1>Farmacia Inteligente</h1>
                    <p>Gestión moderna y eficiente de tu clínica</p>
                </div>
            </div>

            <div className="login-form-container">
                <div className="login-card">
                <h2>Acceso al Sistema</h2>
                <p>Ingresa tus credenciales</p>

                <form onSubmit={handleSubmit}>
                    <input
                        type="email"
                        placeholder="Correo electrónico"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                    <input
                        type="password"
                        placeholder="Contraseña"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <button type="submit">Ingresar</button>
                </form>
                </div>
            </div>
        </div>
    );
}

export default Login;