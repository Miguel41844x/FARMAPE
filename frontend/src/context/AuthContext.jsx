import { createContext, useContext, useState } from "react";

/**
 * AuthContext
 *
 * Contexto global para manejar la información
 * del usuario autenticado en toda la aplicación.
 */

const AuthContext = createContext();

/**
 * AuthProvider
 *
 * Envuelve la aplicación y permite compartir
 * el estado del usuario entre componentes.
 */

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState({
        name: "Renzo Pérez",
        email: "renzo@gmail.com"
        });

    return (
        <AuthContext.Provider value={{ user, setUser }}>
        {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);