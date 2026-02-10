import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

function PlantDetail() {
    const {id} =useParams();
    const [plant, setPlant] =useState(null);

    useEffect(()=> {
        fetch(`/api/plants/${id}`)
            .then(res => res.json())
            .then(data => {
                console.log('Received plant detail:', data);
                console.log('Plant imageUrl:', data.imageUrl);
                setPlant(data);
            })
            .catch(err => console.error('Error fetching plant details:', err));
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