import { useState } from "react";
import UsuarioForms from "../../../components/private/mantenimiento/UsuarioForms";
import UsuariosTable from "../../../components/private/mantenimiento/UsuariosTable.jsx";
import "./usuarios.css";

const Usuarios = () => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [usuarioEditando, setUsuarioEditando] = useState(null);

    const [usuarios, setUsuarios] = useState([
        {
            id: 1,
            dni: "77777777",
            nombres: "Renzo",
            apellidos: "Pérez",
            telefono: "956321654",
            direccion: "Av. Brasil",
            email: "renzo@farmape.com",
            rol: "ADMIN",
            estado: "Activo",
        },
        {
            id: 2,
            dni: "88888888",
            nombres: "José",
            apellidos: "Ramos",
            telefono: "987654321",
            direccion: "Av. Perú",
            email: "jose@farmape.com",
            rol: "VENDEDOR",
            estado: "Activo",
        },
        {
            id: 3,
            dni: "99999999",
            nombres: "María",
            apellidos: "Gonzales",
            telefono: "912345678",
            direccion: "Av. Arequipa",
            email: "maria@farmape.com",
            rol: "VENDEDOR",
            estado: "Inactivo",
        },
    ]);

    const abrirCrearUsuario = () => {
        setUsuarioEditando(null);
        setMostrarFormulario(true);
    };

    const editarUsuario = (usuario) => {
        setUsuarioEditando(usuario);
        setMostrarFormulario(true);
    };

    const eliminarUsuario = (id) => {
        const confirmar = confirm("¿Seguro que deseas eliminar este usuario?");
        if (!confirmar) return;

        setUsuarios(usuarios.filter((usuario) => usuario.id !== id));
    };

    const cerrarFormulario = () => {
        setMostrarFormulario(false);
        setUsuarioEditando(null);
    };

    return (
        <div className="usuarios-container">
            <div className="usuarios-header">
                <div>
                    <h1>Gestión de usuarios</h1>
                    <p>Crea, edita y administra usuarios del sistema.</p>
                </div>

                <button
                    className="usuarios-create-btn"
                    onClick={abrirCrearUsuario}
                >
                    Crear usuario
                </button>
            </div>

            {mostrarFormulario && (
                <div
                    className="usuarios-modal-overlay"
                    onClick={cerrarFormulario}
                >
                    <div
                        className="usuarios-modal-content"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <UsuarioForms
                            usuarioEditando={usuarioEditando}
                            cerrarFormulario={cerrarFormulario}
                        />
                    </div>
                </div>
            )}

            <UsuariosTable
                usuarios={usuarios}
                onEdit={editarUsuario}
                onDelete={eliminarUsuario}
            />
        </div>
    );
};

export default Usuarios;