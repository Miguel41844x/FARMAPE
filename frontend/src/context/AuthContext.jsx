/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext, useState } from "react";

const AuthContext = createContext();

const tokenExpirado = (token) => {
    try {
        const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
        return !payload.exp || payload.exp * 1000 <= Date.now();
    } catch {
        return true;
    }
};

export const AuthProvider = ({ children }) => {
    
    const [user, setUser] = useState(() => {
        const token = localStorage.getItem("token");
        
        if(!token || tokenExpirado(token))
            return null;

        let permisos = [];
        try {
            permisos = JSON.parse(localStorage.getItem("permisos") || "[]");
        } catch {
            permisos = [];
        }

        return{
            usuario : localStorage.getItem("usuario"),
            rol : localStorage.getItem("rol"),
            nombres : localStorage.getItem("nombres"),
            apellidos : localStorage.getItem("apellidos"),
            idCuenta: localStorage.getItem("idCuenta"),
            idTrabajador: localStorage.getItem("idTrabajador"),
            permisos,
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
        localStorage.removeItem("permisos");
        
        setUser(null);
    };

    const updateUserProfile = (perfilActualizado) => {
        localStorage.setItem("nombres", perfilActualizado.nombres || "");
        localStorage.setItem("apellidos", perfilActualizado.apellidos || "");

        setUser((current) => {
            if (!current) return current;
            return {
                ...current,
                nombres: perfilActualizado.nombres,
                apellidos: perfilActualizado.apellidos,
            };
        });
    };

    const hasPermission = (permission) => user?.permisos?.includes(permission) ?? false;

    return (
        <AuthContext.Provider value={{ user, setUser, logout, hasPermission, updateUserProfile }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
