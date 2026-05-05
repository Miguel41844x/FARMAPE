import { Route, Routes, Navigate } from "react-router-dom";
import PrivateLayout from "../pages/private/PrivateLayout";

import HomePublic from "../pages/public/HomePublic";
import Login from "../pages/auth/Login";

import HomePrivate from "../pages/private/HomePrivate";


function AppRouter (){
    return(
        <Routes>
            <Route path="/" element={<HomePublic/>}/>

            <Route path="/login" element={<Login />} />

            <Route element={<PrivateLayout />}>
                <Route path="/homePrivate" element={<HomePrivate />} />
            </Route> 
        </Routes>
    );
}

export default AppRouter;
