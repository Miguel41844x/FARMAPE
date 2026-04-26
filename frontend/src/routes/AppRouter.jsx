import { Route, Routes } from "react-router-dom";
import { Home } from "../pages/public/Home";
import Login from "../pages/auth/Login";

function AppRouter (){
    return(
        <Routes>
            <Route path="/" element={<Home/>} />
            <Route path="/login" element={<Login/>}/>
        </Routes>
    );
}

export default AppRouter;


