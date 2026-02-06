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
    resultsContainer.innerHTML = "";

    if (!Array.isArray(plants) || plants.length === 0) {
        console.error("Vi ville ha en lista men det blev: ", plants);
        return;
    }

    plants.forEach(plant=> {
        const plantDiv = document.createElement("div");
        const sciName = (plant.scientific_name && plant.scientific_name.length > 0)
                        ? plant.scientific_name[0]
                        : "N/A";

        plantDiv.innerHTML = `
            <h3>${plant.common_name}</h3>
            <p>Scientific name: ${plant.scientific_name [0]}</p>
            `;
        resultsContainer.appendChild(plantDiv);
    });

    alert("Found " + plants.length + "plants.");
}