import { useState } from "react";
import "./datosVenta.css";

function DatosVenta({
    idCliente,
    setIdCliente,
    canalPedido,
    setCanalPedido,
    observacion,
    setObservacion,
    cargarClientes,
    volverProductos,
    generarTicket,
    loadingTicket,
    totalVenta,
}) {
    const [dniBusqueda, setDniBusqueda] = useState("");
    const [buscandoCliente, setBuscandoCliente] = useState(false);
    const [mostrarRegistro, setMostrarRegistro] = useState(false);

    const [clienteEncontrado, setClienteEncontrado] = useState({
        dniRuc: "",
        nombres: "",
        apellidos: "",
        telefono: "",
        direccion: "",
    });

    const [nuevoCliente, setNuevoCliente] = useState({
        dniRuc: "",
        nombres: "",
        apellidos: "",
        telefono: "",
        whatsapp: "",
        direccion: "",
        email: "",
        tipoCliente: "Natural",
    });

    const limpiarClienteEncontrado = () => {
        setIdCliente("");
        setClienteEncontrado({
            dniRuc: "",
            nombres: "",
            apellidos: "",
            telefono: "",
            direccion: "",
        });
    };

    const buscarClientePorDniRuc = async () => {
        const documento = dniBusqueda.trim();

        if (!documento) {
            alert("Ingrese un DNI o RUC para buscar");
            limpiarClienteEncontrado();
            return;
        }

        try {
            setBuscandoCliente(true);

            const token = localStorage.getItem("token");

            const response = await fetch(
                `http://localhost:8080/api/clientes/documento/${documento}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                limpiarClienteEncontrado();
                setClienteEncontrado((prev) => ({
                    ...prev,
                    dniRuc: documento,
                }));

                alert("Cliente no encontrado. Puedes registrarlo manualmente.");
                return;
            }

            const data = await response.json();

            setIdCliente(data.idCliente);

            setClienteEncontrado({
                dniRuc: data.dniRuc || documento,
                nombres: data.nombres || "",
                apellidos: data.apellidos || "",
                telefono: data.telefono || "",
                direccion: data.direccion || "",
            });
        } catch (error) {
            console.error("Error al buscar cliente:", error);
            alert("Error al buscar cliente");
        } finally {
            setBuscandoCliente(false);
        }
    };

    const buscarConEnter = (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            buscarClientePorDniRuc();
        }
    };

    const manejarCambioBusqueda = (e) => {
        setDniBusqueda(e.target.value);

        if (idCliente) {
            limpiarClienteEncontrado();
        }
    };

    const manejarCambioCliente = (e) => {
        const { name, value } = e.target;

        setNuevoCliente((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const limpiarFormularioCliente = () => {
        setNuevoCliente({
            dniRuc: "",
            nombres: "",
            apellidos: "",
            telefono: "",
            whatsapp: "",
            direccion: "",
            email: "",
            tipoCliente: "Natural",
        });
    };

    const registrarCliente = async (e) => {
        e.preventDefault();

        try {
            const token = localStorage.getItem("token");

            if (!nuevoCliente.dniRuc.trim()) {
                alert("Ingresa DNI o RUC del cliente");
                return;
            }

            if (!nuevoCliente.nombres.trim()) {
                alert("Ingresa los nombres del cliente");
                return;
            }

            const response = await fetch("http://localhost:8080/api/clientes", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(nuevoCliente),
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || "No se pudo registrar el cliente");
            }

            const clienteCreado = await response.json();

            if (cargarClientes) {
                await cargarClientes();
            }

            setIdCliente(clienteCreado.idCliente);
            setDniBusqueda(clienteCreado.dniRuc || "");

            setClienteEncontrado({
                dniRuc: clienteCreado.dniRuc || "",
                nombres: clienteCreado.nombres || "",
                apellidos: clienteCreado.apellidos || "",
                telefono: clienteCreado.telefono || "",
                direccion: clienteCreado.direccion || "",
            });

            setMostrarRegistro(false);
            limpiarFormularioCliente();

            alert("Cliente registrado correctamente");
        } catch (error) {
            console.error("Error al registrar cliente:", error);
            alert("Error al registrar cliente: " + error.message);
        }
    };

    return (
        <>
            <section className="datos-venta-card">
                <div className="datos-venta-header">
                    <div>
                        <h3>Datos de la venta</h3>
                        <p>Busca el cliente por DNI/RUC, selecciona el canal y agrega una observación.</p>
                    </div>

                    <button
                        type="button"
                        className="btn-registrar-cliente"
                        onClick={() => setMostrarRegistro(true)}
                    >
                        Registrar cliente
                    </button>
                </div>

                <div className="datos-venta-grid">
                    <div className="datos-venta-field">
                        <label>DNI / RUC del cliente</label>

                        <div className="cliente-search-inline">
                            <input
                                type="text"
                                value={dniBusqueda}
                                onChange={manejarCambioBusqueda}
                                onKeyDown={buscarConEnter}
                                placeholder="Ingrese DNI/RUC y presione Enter"
                            />

                            <button
                                type="button"
                                onClick={buscarClientePorDniRuc}
                                disabled={buscandoCliente}
                            >
                                {buscandoCliente ? "..." : "Buscar"}
                            </button>
                        </div>
                    </div>

                    <div className="datos-venta-field">
                        <label>Nombres</label>
                        <input
                            type="text"
                            value={`${clienteEncontrado.nombres} ${clienteEncontrado.apellidos}`.trim()}
                            placeholder="Se completará al buscar"
                            readOnly
                        />
                    </div>

                    <div className="datos-venta-field">
                        <label>Teléfono</label>
                        <input
                            type="text"
                            value={clienteEncontrado.telefono}
                            placeholder="Teléfono del cliente"
                            readOnly
                        />
                    </div>

                    <div className="datos-venta-field">
                        <label>Dirección</label>
                        <input
                            type="text"
                            value={clienteEncontrado.direccion}
                            placeholder="Dirección del cliente"
                            readOnly
                        />
                    </div>

                    <div className="datos-venta-field">
                        <label>Canal de pedido</label>
                        <select
                            value={canalPedido}
                            onChange={(e) => setCanalPedido(e.target.value)}
                        >
                            <option value="Presencial">Presencial</option>
                            <option value="Telefono">Teléfono</option>
                            <option value="WhatsApp">WhatsApp</option>
                        </select>
                    </div>

                    <div className="datos-venta-field datos-venta-field-full">
                        <label>Observación</label>
                        <textarea
                            value={observacion}
                            onChange={(e) => setObservacion(e.target.value)}
                            placeholder="Ejemplo: Venta registrada desde mostrador"
                            rows="3"
                        />
                    </div>
                </div>

                <div className="datos-venta-total">
                    <span>Total de la orden</span>
                    <h2>S/ {Number(totalVenta || 0).toFixed(2)}</h2>
                </div>

                <div className="datos-venta-actions">
                    <button
                        type="button"
                        className="btn-volver-carrito"
                        onClick={volverProductos}
                    >
                        Volver a productos
                    </button>

                    <button
                        type="button"
                        className="btn-generar-ticket"
                        onClick={generarTicket}
                        disabled={!idCliente || loadingTicket}
                    >
                        {loadingTicket ? "Generando..." : "Confirmar y generar ticket"}
                    </button>
                </div>
            </section>

            {mostrarRegistro && (
                <div className="cliente-modal-overlay">
                    <div className="cliente-modal">
                        <div className="cliente-modal-header">
                            <div>
                                <h3>Registrar cliente nuevo</h3>
                                <p>Completa los datos del cliente para usarlo en la venta.</p>
                            </div>

                            <button
                                type="button"
                                className="btn-cerrar-modal"
                                onClick={() => setMostrarRegistro(false)}
                            >
                                ×
                            </button>
                        </div>

                        <form onSubmit={registrarCliente}>
                            <div className="registro-cliente-grid">
                                <div className="datos-venta-field">
                                    <label>DNI / RUC</label>
                                    <input
                                        name="dniRuc"
                                        value={nuevoCliente.dniRuc}
                                        onChange={manejarCambioCliente}
                                        placeholder="Ejemplo: 76543210"
                                    />
                                </div>

                                <div className="datos-venta-field">
                                    <label>Tipo cliente</label>
                                    <select
                                        name="tipoCliente"
                                        value={nuevoCliente.tipoCliente}
                                        onChange={manejarCambioCliente}
                                    >
                                        <option value="Natural">Natural</option>
                                        <option value="Empresa">Empresa</option>
                                    </select>
                                </div>

                                <div className="datos-venta-field">
                                    <label>Nombres</label>
                                    <input
                                        name="nombres"
                                        value={nuevoCliente.nombres}
                                        onChange={manejarCambioCliente}
                                        placeholder="Nombres"
                                    />
                                </div>

                                <div className="datos-venta-field">
                                    <label>Apellidos</label>
                                    <input
                                        name="apellidos"
                                        value={nuevoCliente.apellidos}
                                        onChange={manejarCambioCliente}
                                        placeholder="Apellidos"
                                    />
                                </div>

                                <div className="datos-venta-field">
                                    <label>Teléfono</label>
                                    <input
                                        name="telefono"
                                        value={nuevoCliente.telefono}
                                        onChange={manejarCambioCliente}
                                        placeholder="Teléfono"
                                    />
                                </div>

                                <div className="datos-venta-field">
                                    <label>WhatsApp</label>
                                    <input
                                        name="whatsapp"
                                        value={nuevoCliente.whatsapp}
                                        onChange={manejarCambioCliente}
                                        placeholder="WhatsApp"
                                    />
                                </div>

                                <div className="datos-venta-field">
                                    <label>Email</label>
                                    <input
                                        name="email"
                                        value={nuevoCliente.email}
                                        onChange={manejarCambioCliente}
                                        placeholder="correo@ejemplo.com"
                                    />
                                </div>

                                <div className="datos-venta-field">
                                    <label>Dirección</label>
                                    <input
                                        name="direccion"
                                        value={nuevoCliente.direccion}
                                        onChange={manejarCambioCliente}
                                        placeholder="Dirección"
                                    />
                                </div>
                            </div>

                            <div className="registro-cliente-actions">
                                <button
                                    type="button"
                                    className="btn-cancelar-cliente"
                                    onClick={() => setMostrarRegistro(false)}
                                >
                                    Cancelar
                                </button>

                                <button
                                    type="submit"
                                    className="btn-guardar-cliente"
                                >
                                    Guardar cliente
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </>
    );
}

export default DatosVenta;