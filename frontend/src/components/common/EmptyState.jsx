import "./feedback.css";

const EmptyState = ({ icon = "📭", title = "Sin registros", message, actionText, onAction }) => (
    <div className="empty-state-box">
        <div className="empty-state-icon">{icon}</div>
        <h3>{title}</h3>
        {message && <p>{message}</p>}
        {actionText && onAction && (
            <button type="button" onClick={onAction}>{actionText}</button>
        )}
    </div>
);

export default EmptyState;
