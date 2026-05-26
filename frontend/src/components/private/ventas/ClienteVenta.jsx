import { useState } from "react";
import "./clienteVenta.css";

const comprobantes = {
    BOLETA_SIMPLE: "Boleta simple",
    BOLETA_DNI: "Boleta con DNI",
    FACTURA: "Factura",
};

const ClienteVenta = ({
    totalVenta,
    carrito,
    volverProductos,
    confirmarVenta,
}) => {
    const [tipoComprobante, setTipoComprobante] = useState("BOLETA_SIMPLE");
    const [menuAbierto, setMenuAbierto] = useState(false);
    const [buscandoCliente, setBuscandoCliente] = useState(false);

    const [cliente, setCliente] = useState({
        dni: "",
        nombres: "",
        apellidos: "",
        telefono: "",
        ruc: "",
        razonSocial: "",
        direccion: "",
    });

    const handleChange = (e) => {
        setCliente({
            ...cliente,
            [e.target.name]: e.target.value,
        });
    };

    const seleccionarComprobante = (tipo) => {
        setTipoComprobante(tipo);
        setMenuAbierto(false);
    };

    const buscarClientePorDni = async () => {
        if (!cliente.dni.trim()) {
            alert("Ingrese un DNI para buscar");
            return;
        }

        try {
            setBuscandoCliente(true);

            const token = localStorage.getItem("token");

            const response = await fetch(
                `http://localhost:8080/api/clientes/dni/${cliente.dni}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                alert("Cliente no encontrado. Puede registrarlo manualmente.");
                return;
            }

            const data = await response.json();

            setCliente({
                ...cliente,
                dni: data.dni || cliente.dni,
                nombres: data.nombres || "",
                apellidos: data.apellidos || "",
                telefono: data.telefono || "",
                direccion: data.direccion || "",
            });
        } catch (error) {
            console.error(error);
            alert("Error al buscar cliente");
        } finally {
            setBuscandoCliente(false);
        }
    };

    const buscarClientePorRuc = async () => {
        if (!cliente.ruc.trim()) {
            alert("Ingrese un RUC para buscar");
            return;
        }

        try {
            setBuscandoCliente(true);

            const token = localStorage.getItem("token");

            const response = await fetch(
                `http://localhost:8080/api/clientes/ruc/${cliente.ruc}`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (!response.ok) {
                alert("Cliente no encontrado. Puede registrarlo manualmente.");
                return;
            }

            const data = await response.json();

            setCliente({
                ...cliente,
                ruc: data.ruc || cliente.ruc,
                razonSocial: data.razonSocial || "",
                direccion: data.direccion || "",
                telefono: data.telefono || "",
            });
        } catch (error) {
            console.error(error);
            alert("Error al buscar cliente");
        } finally {
            setBuscandoCliente(false);
        }
    };

    const handleConfirmar = () => {
        if (carrito.length === 0) {
            alert("Agrega productos antes de confirmar");
            return;
        }

        if (tipoComprobante === "BOLETA_DNI" && !cliente.dni.trim()) {
            alert("Ingrese DNI del cliente");
            return;
        }

        if (tipoComprobante === "FACTURA" && !cliente.ruc.trim()) {
            alert("Ingrese RUC del cliente");
            return;
        }

        confirmarVenta({
            tipoComprobante,
            cliente,
        });
    };

    return (
        <div className="cliente-venta-card">
            <div className="cliente-venta-header">
                <div>
                    <h2>Datos de venta</h2>
                    <p>Selecciona el comprobante y registra el cliente.</p>
                </div>

                <button
                    className="cliente-volver-btn"
                    onClick={volverProductos}
                >
                    Volver
                </button>
            </div>

            <div className="comprobante-dropdown">
                <label>Tipo de comprobante</label>

                <button
                    type="button"
                    className="comprobante-dropdown-btn"
                    onClick={() => setMenuAbierto(!menuAbierto)}
                >
                    <span>{comprobantes[tipoComprobante]}</span>
                    <span className={`dropdown-arrow ${menuAbierto ? "open" : ""}`}>
                        ▾
                    </span>
                </button>

                {menuAbierto && (
                    <div className="comprobante-dropdown-menu">
                        {Object.entries(comprobantes).map(([key, value]) => (
                            <button
                                key={key}
                                type="button"
                                className={tipoComprobante === key ? "active" : ""}
                                onClick={() => seleccionarComprobante(key)}
                            >
                                {value}
                            </button>
                        ))}
                    </div>
                )}
            </div>

            {tipoComprobante === "BOLETA_SIMPLE" && (
                <div className="cliente-info-box">
                    <h3>Boleta simple</h3>
                    <p>No requiere registrar datos del cliente.</p>
                </div>
            )}

            {tipoComprobante === "BOLETA_DNI" && (
                <div className="cliente-form">
                    <div className="cliente-search-row">
                        <div className="cliente-form-group">
                            <label>DNI</label>
                            <input
                                name="dni"
                                value={cliente.dni}
                                onChange={handleChange}
                                placeholder="Buscar por DNI"
                            />
                        </div>

                        <button
                            type="button"
                            onClick={buscarClientePorDni}
                            disabled={buscandoCliente}
                        >
                            {buscandoCliente ? "Buscando..." : "Buscar"}
                        </button>
                    </div>

                    <div className="cliente-form-group">
                        <label>Nombres</label>
                        <input
                            name="nombres"
                            value={cliente.nombres}
                            onChange={handleChange}
                            placeholder="Nombres del cliente"
                        />
                    </div>

                    <div className="cliente-form-group">
                        <label>Apellidos</label>
                        <input
                            name="apellidos"
                            value={cliente.apellidos}
                            onChange={handleChange}
                            placeholder="Apellidos del cliente"
                        />
                    </div>

                    <div className="cliente-form-group">
                        <label>Teléfono</label>
                        <input
                            name="telefono"
                            value={cliente.telefono}
                            onChange={handleChange}
                            placeholder="Teléfono"
                        />
                    </div>
                </div>
            )}

            {tipoComprobante === "FACTURA" && (
                <div className="cliente-form">
                    <div className="cliente-search-row">
                        <div className="cliente-form-group">
                            <label>RUC</label>
                            <input
                                name="ruc"
                                value={cliente.ruc}
                                onChange={handleChange}
                                placeholder="Buscar por RUC"
                            />
                        </div>

                        <button
                            type="button"
                            onClick={buscarClientePorRuc}
                            disabled={buscandoCliente}
                        >
                            {buscandoCliente ? "Buscando..." : "Buscar"}
                        </button>
                    </div>

                    <div className="cliente-form-group">
                        <label>Razón social</label>
                        <input
                            name="razonSocial"
                            value={cliente.razonSocial}
                            onChange={handleChange}
                            placeholder="Razón social"
                        />
                    </div>

                    <div className="cliente-form-group">
                        <label>Dirección fiscal</label>
                        <input
                            name="direccion"
                            value={cliente.direccion}
                            onChange={handleChange}
                            placeholder="Dirección fiscal"
                        />
                    </div>

                    <div className="cliente-form-group">
                        <label>Teléfono</label>
                        <input
                            name="telefono"
                            value={cliente.telefono}
                            onChange={handleChange}
                            placeholder="Teléfono"
                        />
                    </div>
                </div>
            )}

            <div className="cliente-total-card">
                <span>Total a pagar</span>
                <h2>S/ {totalVenta.toFixed(2)}</h2>
            </div>

            <button
                className="cliente-confirmar-btn"
                onClick={handleConfirmar}
            >
                Confirmar venta
            </button>
        </div>
    );
};

export default ClienteVenta;