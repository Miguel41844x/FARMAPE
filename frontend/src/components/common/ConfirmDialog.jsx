import "./feedback.css";

const ConfirmDialog = ({ open, title, message, confirmText = "Confirmar", cancelText = "Cancelar", onConfirm, onCancel }) => {
    if (!open) return null;

    return (
        <div className="confirm-overlay" role="presentation" onClick={onCancel}>
            <div className="confirm-card" role="dialog" aria-modal="true" onClick={(event) => event.stopPropagation()}>
                <h3>{title}</h3>
                <p>{message}</p>
                <div className="confirm-actions">
                    <button type="button" className="confirm-cancel" onClick={onCancel}>{cancelText}</button>
                    <button type="button" className="confirm-primary" onClick={onConfirm}>{confirmText}</button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmDialog;
