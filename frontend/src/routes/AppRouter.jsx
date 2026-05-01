import { Route, Routes } from "react-router-dom";
import { HomePublic } from "../pages/public/HomePublic";
import Login from "../pages/auth/Login";

function AppRouter (){
    return(
        <Routes>
            <Route path="/" element={<HomePublic/>} />
            <Route path="/login" element={<Login/>}/>
        </Routes>
    );
}

export default AppRouter;


