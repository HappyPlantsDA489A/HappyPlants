document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('plantID');
    const spinner = document.getElementById('loading-spinner');


    searchForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Stoppa sidan från att laddas om

        const query = searchInput.value;
        if (!query) return;

        if (spinner) {
            spinner.style.display = 'block';
            //spinner.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        //if (plantContainer) plantContainer.style.display = 'none';

        //resetpanel();

        try {
            const response = await fetch(`/api/search?plantName=${encodeURIComponent(query)}`);
            const result = await response.json();
            console.log("JSON-data:", result); // Se exakt vad som kommer in

            console.log("Data från API:", result);
            displayResults(result);


        } catch (error) {
            console.error("Kunde inte hämta data:", error);
            alert("could not reload")
        } finally {
            if (spinner) spinner.style.display = 'none';
        }
    });
});

function displayResults(plants) {
    const resultsContainer = document.getElementById("plant-container");

    if (!resultsContainer) return;

    resultsContainer.style.display = "block";
    resultsContainer.innerHTML = ""; // Rensa containern först

    // Om backenden skickar null eller tom lista
    if (!Array.isArray(plants) || plants.length === 0) {
        const noResults = document.createElement("p");
        noResults.textContent = "Inga plantor hittades. Kontrollera din ApiResponse-klass i Java.";
        resultsContainer.appendChild(noResults);
        return;
    }

    plants.forEach(plant => {
        const plantCard = document.createElement("div");
        plantCard.className = "plant-card"; // Styla denna i din CSS

        const title = document.createElement("h3");
        title.textContent = plant.common_name || "Okänt namn";

        const sciNamePara = document.createElement("p");
        sciNamePara.textContent = plant.scientific_name || "unknown";

        const italicName = document.createElement("i");
        // Hämta första namnet i listan om den finns
        italicName.textContent = (plant.scientific_name && plant.scientific_name.length > 0)
            ? plant.scientific_name[0]
            : "N/A";

        sciNamePara.appendChild(italicName);

        plantCard.appendChild(title);
        plantCard.appendChild(sciNamePara);

        resultsContainer.appendChild(plantCard);
    });

    console.log("Antal plantor renderade:", plants.length);

}