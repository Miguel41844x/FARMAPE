import "./modules.css"

const Modules = () => {
    return (
        <section className="modules">
            <h2>Módulos Principales</h2>

            <div className="grid">
                <div className="card">
                    <h3>Gestión de Ventas</h3>
                    <p>Registro de pedidos, emisión de tickets, control de pagos y despacho de productos.</p>
                </div>

                <div className="card">
                    <h3>Control de Almacén</h3>
                    <p>Supervisión de stock, recepción de productos y validación de inventario.</p>
                </div>

                <div className="card">
                    <h3>Compras y Proveedores</h3>
                    <p>Gestión de pedidos, facturación, notas de crédito y control de pagos.</p>
                </div>

                <div className="card">
                    <h3>Recetas Magistrales</h3>
                    <p>Elaboración personalizada, validación médica y seguimiento de pedidos.</p>
                </div>

                <div className="card">
                    <h3>Reportes y Decisiones</h3>
                    <p>Informes estratégicos para gerencia y control de operaciones.</p>
                </div>
            </div>
        </section>
    );
};

export default Modules;