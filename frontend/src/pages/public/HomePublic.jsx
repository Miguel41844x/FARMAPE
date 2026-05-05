import NavbarPublic from "../../components/public/NavbarPublic";
import Hero from "../../components/public/Hero";
import Modules from "../../components/public/Modules";
import Footer from "../../components/public/Footer";
import MisionVision from "../../components/public/MisionVision";
import "./homePublic.css";

const HomePublic = () => {
    return(
        <>
            <NavbarPublic/>
            <Hero/>
            <Modules/>
            <MisionVision/>
            <Footer/>
        </>
    );    
}

export default HomePublic;