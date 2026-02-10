import { BrowserRouter, Routes, Route } from "react-router-dom"; //BrowserRouter lyssnar på ändringar i webbläsarens url
import PlantList from "./PlantList";
import PlantDetail from "./PlantDetail";
import './style.css';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<PlantList/>} />
                <Route path="/plant/:id" element={<PlantDetail/>} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;