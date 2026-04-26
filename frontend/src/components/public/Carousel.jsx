import { useState, useEffect } from "react";
import "./carousel.css"
const images = [
    "/imagen1.png",
    "/imagen2.png",
    "/imagen3.png",
    "/imagen4.png"
];


function Carousel() {
  const [index, setIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setIndex((prev) => (prev + 1) % images.length);
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="carousel">
      <img src={images[index]} alt="banner" />

      {/* DOTS */}
      <div className="dots">
        {images.map((_, i) => (
          <span
            key={i}
            className={`dot ${i === index ? "active" : ""}`}
            onClick={() => setIndex(i)}
          ></span>
        ))}
      </div>
    </div>
  );
}

export default Carousel;