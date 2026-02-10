import { useState } from "react";
import { Link } from "react-router-dom";

function PlantList() {
    const [query, setQuery] = useState("");
    const [plants, setPlants] = useState([]);

    function searchPlants() {
        fetch(`/api/plants/search?name=${query}`)
            .then(res => res.json())
            .then(data => setPlants(data))
            .catch(err => console.error('Error fetching plants:', err));
    }

    return(
        <div>
            <h1>Search plants</h1>

            <input
                value={query}
                onChange={e => setQuery(e.target.value)}
                placeholder="Search for your plant!"
            />
            <button onClick={searchPlants}>Search</button>

            <div className="grid">
                {plants.map(plant=> (
                    <Link to={`/plant/${plant.id}`} key={plant.id} className="card">
                        <img src={plant.imageUrl} alt={plant.name}/>
                        <h3>{plant.name}</h3>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default PlantList;