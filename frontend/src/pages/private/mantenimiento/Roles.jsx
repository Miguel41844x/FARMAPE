import { useEffect, useMemo, useState } from "react";
import { useAuth } from "../../../context/AuthContext";
import { PERMISSIONS } from "../../../constants/permissions";
import {
    actualizarRol,
    cambiarEstadoRol,
    crearRol,
    eliminarRol,
    obtenerPermisos,
    obtenerRoles,
} from "../../../services/mantenimiento/usuarioService";
import "./roles.css";

const estadoInicial = {
    idRol: null,
    codigo: "",
    nombreRol: "",
    descripcion: "",
    idPermisos: [],
};

function Roles() {
    const { hasPermission } = useAuth();
    const puedeGestionarRoles = hasPermission(PERMISSIONS.ROLE_MANAGE);
    const puedeAsignarPermisos = hasPermission(PERMISSIONS.ROLE_ASSIGN);

    const [roles, setRoles] = useState([]);
    const [permisos, setPermisos] = useState([]);
    const [form, setForm] = useState(estadoInicial);
    const [abierto, setAbierto] = useState(false);
    const [loading, setLoading] = useState(true);
    const [guardando, setGuardando] = useState(false);
    const [error, setError] = useState("");

    const permisosPorModulo = useMemo(() => permisos.reduce((grupos, permiso) => {
        grupos[permiso.modulo] = [...(grupos[permiso.modulo] || []), permiso];
        return grupos;
    }, {}), [permisos]);

    const cargar = async () => {
        const [rolesData, permisosData] = await Promise.all([
            obtenerRoles(true),
            obtenerPermisos(),
        ]);
        setRoles(rolesData);
        setPermisos(permisosData);
    };

    useEffect(() => {
        let activo = true;
        Promise.all([obtenerRoles(true), obtenerPermisos()])
            .then(([rolesData, permisosData]) => {
                if (!activo) return;
                setRoles(rolesData);
                setPermisos(permisosData);
            })
            .catch((err) => activo && setError(err.message))
            .finally(() => activo && setLoading(false));
        return () => { activo = false; };
    }, []);

    const abrirNuevo = () => {
        if (!puedeGestionarRoles) {
            setError("No tienes permiso para crear roles");
            return;
        }
        setForm(estadoInicial);
        setAbierto(true);
        setError("");
    };

    const abrirEdicion = (rol) => {
        if (!puedeGestionarRoles && !puedeAsignarPermisos) {
            setError("No tienes permiso para editar roles");
            return;
        }
        setForm({
            idRol: rol.idRol,
            codigo: rol.codigo,
            nombreRol: rol.nombreRol,
            descripcion: rol.descripcion || "",
            idPermisos: rol.idPermisos || [],
        });
        setAbierto(true);
        setError("");
    };

    const alternarPermiso = (idPermiso) => {
        if (!puedeAsignarPermisos) return;
        setForm((actual) => ({
            ...actual,
            idPermisos: actual.idPermisos.includes(idPermiso)
                ? actual.idPermisos.filter((id) => id !== idPermiso)
                : [...actual.idPermisos, idPermiso],
        }));
    };

    const guardar = async (event) => {
        event.preventDefault();
        if (form.idRol && !puedeGestionarRoles && !puedeAsignarPermisos) {
            setError("No tienes permiso para editar roles");
            return;
        }
        if (!form.idRol && !puedeGestionarRoles) {
            setError("No tienes permiso para crear roles");
            return;
        }

        setGuardando(true);
        setError("");
        try {
            const payload = {
                codigo: form.codigo,
                nombreRol: form.nombreRol,
                descripcion: form.descripcion,
                idPermisos: form.idPermisos,
            };
            if (form.idRol) {
                await actualizarRol(form.idRol, payload);
            } else {
                await crearRol(payload);
            }
            await cargar();
            setAbierto(false);
        } catch (err) {
            setError(err.message);
        } finally {
            setGuardando(false);
        }
    };

    const alternarEstado = async (rol) => {
        if (!puedeGestionarRoles) {
            setError("No tienes permiso para cambiar el estado de roles");
            return;
        }
        try {
            await cambiarEstadoRol(rol.idRol, !rol.activo);
            await cargar();
        } catch (err) {
            setError(err.message);
        }
    };

    const borrar = async (rol) => {
        if (!puedeGestionarRoles) {
            setError("No tienes permiso para eliminar roles");
            return;
        }
        if (!confirm(`¿Eliminar el rol ${rol.nombreRol}?`)) return;
        try {
            await eliminarRol(rol.idRol);
            await cargar();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <main className="roles-page">
            <header className="roles-header">
                <div>
                    <h1>Roles y permisos</h1>
                    <p>Define las capacidades del sistema sin modificar el código.</p>
                </div>
                {puedeGestionarRoles && (
                    <button type="button" onClick={abrirNuevo}>Crear rol</button>
                )}
            </header>

            {error && <div className="roles-error" role="alert">{error}</div>}

            <section className="roles-table-card">
                <table className="roles-table">
                    <thead><tr><th>Rol</th><th>Código</th><th>Permisos</th><th>Estado</th><th>Acciones</th></tr></thead>
                    <tbody>
                        {loading ? (
                            <tr><td colSpan="5">Cargando roles...</td></tr>
                        ) : roles.map((rol) => (
                            <tr key={rol.idRol}>
                                <td><strong>{rol.nombreRol}</strong><small>{rol.descripcion}</small></td>
                                <td><code>{rol.codigo}</code></td>
                                <td>{rol.permisos?.length || 0}</td>
                                <td><span className={`roles-status ${rol.activo ? "activo" : "inactivo"}`}>{rol.activo ? "Activo" : "Inactivo"}</span></td>
                                <td className="roles-actions">
                                    {(puedeGestionarRoles || puedeAsignarPermisos) && (
                                        <button type="button" onClick={() => abrirEdicion(rol)}>Editar</button>
                                    )}
                                    {puedeGestionarRoles && (
                                        <>
                                            <button type="button" onClick={() => alternarEstado(rol)}>{rol.activo ? "Desactivar" : "Activar"}</button>
                                            <button type="button" className="danger" onClick={() => borrar(rol)}>Eliminar</button>
                                        </>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </section>

            {abierto && (
                <div className="roles-modal" onMouseDown={() => setAbierto(false)}>
                    <form className="roles-form" onSubmit={guardar} onMouseDown={(event) => event.stopPropagation()}>
                        <header><h2>{form.idRol ? "Editar rol" : "Crear rol"}</h2><button type="button" onClick={() => setAbierto(false)}>×</button></header>
                        <label>Nombre<input required value={form.nombreRol} onChange={(e) => setForm({ ...form, nombreRol: e.target.value })} disabled={!puedeGestionarRoles} /></label>
                        <label>Código<input required value={form.codigo} onChange={(e) => setForm({ ...form, codigo: e.target.value })} placeholder="EJEMPLO_ROL" disabled={!puedeGestionarRoles} /></label>
                        <label>Descripción<textarea value={form.descripcion} onChange={(e) => setForm({ ...form, descripcion: e.target.value })} disabled={!puedeGestionarRoles} /></label>
                        <div className="roles-permisos">
                            <h3>Permisos asignados</h3>
                            {Object.entries(permisosPorModulo).map(([modulo, items]) => (
                                <fieldset key={modulo}>
                                    <legend>{modulo}</legend>
                                    {items.map((permiso) => (
                                        <label key={permiso.idPermiso} className="permiso-check">
                                            <input type="checkbox" checked={form.idPermisos.includes(permiso.idPermiso)} onChange={() => alternarPermiso(permiso.idPermiso)} disabled={!puedeAsignarPermisos} />
                                            <span><strong>{permiso.nombre}</strong><small>{permiso.codigo}</small></span>
                                        </label>
                                    ))}
                                </fieldset>
                            ))}
                        </div>
                        <footer><button type="button" onClick={() => setAbierto(false)}>Cancelar</button><button type="submit" disabled={guardando}>{guardando ? "Guardando..." : "Guardar rol"}</button></footer>
                    </form>
                </div>
            )}
        </main>
    );
}

export default Roles;
