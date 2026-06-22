import "./feedback.css";

const FeedbackMessage = ({ type = "info", title, message, onClose }) => {
    if (!message && !title) return null;

    return (
        <div className={`feedback-message feedback-${type}`} role="status">
            <div>
                {title && <strong>{title}</strong>}
                {message && <p>{message}</p>}
            </div>
            {onClose && (
                <button type="button" onClick={onClose} aria-label="Cerrar mensaje">
                    ×
                </button>
            )}
        </div>
    );
};

export default FeedbackMessage;
