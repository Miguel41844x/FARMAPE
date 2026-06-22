import "./feedback.css";

const LoadingState = ({ text = "Cargando información..." }) => (
    <div className="loading-state" role="status">
        <span className="loading-dot" />
        <span>{text}</span>
    </div>
);

export default LoadingState;
