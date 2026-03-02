import { useEffect, useState } from "react";
import { usePageContext } from "vike-react/usePageContext";
import { API_BASE_URL } from "@/config";

import "@/css/style.css";

export default function Page() {
  const pageContext = usePageContext();
  const { id } = pageContext.routeParams;

  const [plant, setPlant] = useState(null);

  useEffect(() => {
    if (!id) return;

    fetch(`${API_BASE_URL}/plants/${id}`)
      .then((res) => res.json())
      .then((data) => setPlant(data))
      .catch((err) => console.error("Error fetching plant details:", err));
  }, [id]);

  if (!plant) return <div className="plant-page"><p>Loading...</p></div>;

  return (
    <div className="plant-page">
      <a href="/search" className="back-btn">← Back to search</a>

      <div className="plant-detail-card">
        <div className="plant-detail-image-wrap">
          {plant.imageUrl ? (
            <img
              src={plant.imageUrl}
              alt={plant.commonName}
              onError={(e) => {
                e.currentTarget.style.display = "none";
                const fallback = e.currentTarget.parentElement?.querySelector(".no-image");
                if (fallback) fallback.style.display = "flex";
              }}
            />
          ) : null}
          <div
            className="no-image"
            style={{ display: plant.imageUrl ? "none" : "flex" }}
          >
            No image available
          </div>
        </div>

        <div className="plant-detail-body">
          <div>
            <h1>{plant.commonName}</h1>
            {plant.scientificName && (
              <p className="scientific-name">{plant.scientificName}</p>
            )}
          </div>

          {plant.plantDescription && (
            <div className="info-block">
              <p>{plant.plantDescription}</p>
            </div>
          )}

          {plant.wateringDescription && (
            <div className="info-block">
              <p><strong>Watering: </strong>{plant.wateringDescription}</p>
            </div>
          )}

          {plant.sunDescription && (
            <div className="info-block">
              <p><strong>Sunlight: </strong>{plant.sunDescription}</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}