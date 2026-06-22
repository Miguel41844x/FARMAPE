import { useEffect, useState } from "react";
import UsuarioForms from "../../../components/private/mantenimiento/usuarios/UsuarioForms";
import UsuariosTable from "../../../components/private/mantenimiento/usuarios/UsuariosTable.jsx";
import FeedbackMessage from "../../../components/common/FeedbackMessage";
import ConfirmDialog from "../../../components/common/ConfirmDialog";
import "./usuarios.css";
import {
    obtenerUsuarios as obtenerUsuariosService,
    actualizarEstadoCuenta,
} from "../../../services/mantenimiento/usuarioService";

const Usuarios = () => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [usuarioEditando, setUsuarioEditando] = useState(null);
    const [usuarios, setUsuarios] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [paginaActual, setPaginaActual] = useState(1);
    const [loading, setLoading] = useState(true);
    const [feedback, setFeedback] = useState(null);
    const [confirmacion, setConfirmacion] = useState(null);

    const usuariosPorPagina = 10;

    const cerrarFormulario = () => {
        setMostrarFormulario(false);
        setUsuarioEditando(null);
    };

    const cargarUsuarios = async () => {
        try {
            setLoading(true);
            const data = await obtenerUsuariosService();
            setUsuarios(Array.isArray(data) ? data : []);
        } catch (error) {
            setFeedback({ type: "error", message: error.message });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarUsuarios();
    }, []);

    useEffect(() => {
        const cerrarConEscape = (event) => {
            if (event.key === "Escape") cerrarFormulario();
        };
        if (mostrarFormulario) document.addEventListener("keydown", cerrarConEscape);
        return () => document.removeEventListener("keydown", cerrarConEscape);
    }, [mostrarFormulario]);

    const abrirCrearUsuario = () => {
        setUsuarioEditando(null);
        setMostrarFormulario(true);
    };

    const editarUsuario = (usuario) => {
        setUsuarioEditando(usuario);
        setMostrarFormulario(true);
    };

    const solicitarDesactivar = (usuario) => {
        setConfirmacion({
            idCuenta: usuario.idCuenta,
            nombre: `${usuario.nombres} ${usuario.apellidos}`,
        });
    };

    const desactivarUsuario = async () => {
        if (!confirmacion) return;
        try {
            const usuarioActualizado = await actualizarEstadoCuenta(confirmacion.idCuenta, "Inactivo");
            setUsuarios((actuales) => actuales.map((usuario) => usuario.idCuenta === confirmacion.idCuenta ? { ...usuario, ...usuarioActualizado } : usuario));
            setFeedback({ type: "success", message: "Usuario desactivado correctamente." });
        } catch (error) {
            setFeedback({ type: "error", message: error.message });
        } finally {
            setConfirmacion(null);
        }
    };

    const usuariosFiltrados = usuarios.filter((usuario) => {
        const texto = busqueda.trim().toLocaleLowerCase("es");
        const rol = usuario.rol?.nombre || usuario.nombreRol || usuario.rol;
        const email = usuario.email || usuario.correo || usuario.cuenta?.email;
        return [usuario.dni, usuario.nombres, usuario.apellidos, usuario.telefono, usuario.direccion, usuario.usuario, email, usuario.estado, rol]
            .some((valor) => String(valor ?? "").toLocaleLowerCase("es").includes(texto));
    });

    const totalPaginas = Math.ceil(usuariosFiltrados.length / usuariosPorPagina);
    const paginaSegura = Math.min(paginaActual, totalPaginas || 1);
    const indiceInicial = (paginaSegura - 1) * usuariosPorPagina;
    const usuariosPaginados = usuariosFiltrados.slice(indiceInicial, indiceInicial + usuariosPorPagina);

    const cambiarBusqueda = (valor) => {
        setBusqueda(valor);
        setPaginaActual(1);
    };

    return (
        <div className="usuarios-container">
            <div className="usuarios-header">
                <div>
                    <h1>Gestión de usuarios</h1>
                    <p>Crea, edita datos personales, credenciales, roles y estado de acceso.</p>
                </div>
                <button className="usuarios-create-btn" onClick={abrirCrearUsuario}>Crear usuario</button>
            </div>

            {feedback && <FeedbackMessage type={feedback.type} message={feedback.message} onClose={() => setFeedback(null)} />}

            {mostrarFormulario && (
                <div className="usuarios-modal-overlay" onClick={cerrarFormulario}>
                    <div className="usuarios-modal-content" onClick={(e) => e.stopPropagation()}>
                        <UsuarioForms usuarioEditando={usuarioEditando} cerrarFormulario={cerrarFormulario} obtenerUsuarios={cargarUsuarios} />
                    </div>
                </div>
            )}

            <UsuariosTable
                usuarios={usuariosPaginados}
                totalUsuarios={usuariosFiltrados.length}
                busqueda={busqueda}
                setBusqueda={cambiarBusqueda}
                loading={loading}
                paginaActual={paginaSegura}
                totalPaginas={totalPaginas}
                paginaAnterior={() => setPaginaActual((pagina) => Math.max(pagina - 1, 1))}
                paginaSiguiente={() => setPaginaActual((pagina) => Math.min(pagina + 1, totalPaginas || 1))}
                onEdit={editarUsuario}
                onDelete={solicitarDesactivar}
            />

            <ConfirmDialog
                open={Boolean(confirmacion)}
                title="Desactivar usuario"
                message={`¿Deseas desactivar a ${confirmacion?.nombre || "este usuario"}? Podrás reactivarlo editando su estado.`}
                confirmText="Desactivar"
                onConfirm={desactivarUsuario}
                onCancel={() => setConfirmacion(null)}
            />
        </div>
    );
};

export default Usuarios;
