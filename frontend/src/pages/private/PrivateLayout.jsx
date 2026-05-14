import Sidebar from "../../components/private/Sidebar";
import { Outlet } from "react-router-dom";
import "./privateLayout.css"

const PrivateLayout = () => {
    return(
        <div className="layout">
            <Sidebar/>
            <div className="main-content">
                <Outlet/>
            </div>
        </div>
    );
}

export default PrivateLayout;