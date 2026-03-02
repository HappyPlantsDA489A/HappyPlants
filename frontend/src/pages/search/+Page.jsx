import { useState } from "react";
import { API_BASE_URL } from "@/config";

import "@/css/style.css";

export default function Page() {
  const [query, setQuery] = useState("");
  const [plants, setPlants] = useState([]);

  function searchPlants() {
    fetch(`${API_BASE_URL}/plants/search?name=${encodeURIComponent(query)}`)
      .then((res) => res.json())
      .then((data) => setPlants(data))
      .catch((err) => console.error("Error fetching plants:", err));
  }

  function handleKeyDown(e) {
    if (e.key === "Enter") searchPlants();
  }

  return (
    <div className="search-page">
      <h1>Search plants</h1>

      <div className="search-bar-row">
        <input
          className="search-bar"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Search for a plant..."
        />
        <button className="search-button" onClick={searchPlants}>
          Search
        </button>
      </div>

      <div className="grid">
        {plants.map((plant) => (
          <a href={`/plant/${plant.id}`} key={plant.id} className="card">
            <div className="card-image-wrap">
              {plant.imageUrl ? (
                <img
                  src={plant.imageUrl}
                  alt={plant.common_name}
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
            <div className="card-body">
              <h3>{plant.common_name || "Unknown plant"}</h3>
              {plant.scientific_name?.[0] && (
                <p>{plant.scientific_name[0]}</p>
              )}
            </div>
          </a>
        ))}
      </div>
    </div>
  );
}
