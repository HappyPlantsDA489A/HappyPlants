import { useEffect, useState } from "react";
import { usePageContext } from "vike-react/usePageContext";
import { API_BASE_URL } from "@/config";

import "@/css/index.css";
import "@/css/style.css";
import "@/css/App.css";

export default function Page() {
  const pageContext = usePageContext();
  const { id } = pageContext.routeParams;

  const [plant, setPlant] = useState(null);

  useEffect(() => {
    if (!id) return;

    fetch(`${API_BASE_URL}/plants/${id}`)
      .then((res) => res.json())
      .then((data) => {
        console.log("Received plant detail:", data);
        console.log("Plant imageUrl:", data.imageUrl);
        setPlant(data);
      })
      .catch((err) => console.error("Error fetching plant details:", err));
  }, [id]);

  if (!plant) return <p>Loading...</p>;

  return (
    <div>
      <h1>{plant.common_name}</h1>
      <img src={plant.imageUrl} alt={plant.name} />
      <p>{plant.description}</p>
    </div>
  );
}