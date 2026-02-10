import { useState } from "react";
import { Link } from "react-router-dom";

function PlantList() {
    const [query, setQuery] = useState("");
    const [plants, setPlants] = useState([]);

    function searchPlants() {
        fetch(`/api/plants/search?name=${query}`)
            .then(res => res.json())
            .then(data => {
                console.log('Received plants data:', data);
                if (data.length > 0) {
                    console.log('First plant:', data[0]);
                    console.log('First plant imageUrl:', data[0].imageUrl);
                }
                setPlants(data);
            })
            .catch(err => console.error('Error fetching plants:', err));
    }

    return(
        <div>
            <h1>Search plants</h1>

            <input
                className="search-bar"
                value={query}
                onChange={e => setQuery(e.target.value)}
                placeholder="Search for your plant!"
            />
            <button className="search-button" onClick={searchPlants}>Search</button>

            <div className="grid">
                {plants.map(plant=> (
                    <Link to={`/plant/${plant.id}`} key={plant.id} className="card">
                        {plant.imageUrl ? (
                            <img
                                src={plant.imageUrl}
                                alt={plant.name}
                                onError={(e) => {
                                    console.error('Image failed to load:', plant.imageUrl);
                                    e.target.style.display = 'none';
                                }}
                            />
                        ) : (
                            <div className="no-image">No image available</div>
                        )}
                        <h3>{plant.name}</h3>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default PlantList;