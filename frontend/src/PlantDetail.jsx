import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

function PlantDetail() {
    const {id} =useParams();
    const [plant, setPlant] =useState(null);

    useEffect(()=> {
        fetch(`http://localhost:8080/api/plants/${id}`)
            .then(res => res.json())
            .then(data => setPlant(data));
    },[id]);

    if(!plant) return <p>Loading...</p>;

    return(
        <div>
            <h1>{plant.name}</h1>
            <img src={plant.imageUrl} alt={plant.name}/>
            <p>{plant.description}</p>
        </div>
    );
}

export default PlantDetail;