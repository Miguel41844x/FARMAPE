import { createContext, useContext, useState } from "react";
import { FaAppleAlt } from "react-icons/fa";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem("token");
        
        if(!token)
            return null;
        return{
            usuario : localStorage.getItem("usuario"),
            rol : localStorage.getItem("rol"),
            nombres : localStorage.getItem("nombres"),
            apellidos : localStorage.getItem("apellidos"),
            idCuenta: localStorage.getItem("idCuenta"),
            idTrabajador: localStorage.getItem("idTrabajador"),
            token,
        };
    });

    const logout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("usuario");
        localStorage.removeItem("rol");
        localStorage.removeItem("nombres");
        localStorage.removeItem("apellidos");
        localStorage.removeItem("idCuenta");
        localStorage.removeItem("idTrabajador");
        
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, setUser, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);