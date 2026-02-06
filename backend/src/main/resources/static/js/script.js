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
            spinner.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        //if (plantContainer) plantContainer.style.display = 'none';

        //resetpanel();

        try {
            const response = await fetch(`/api/search?plantName=${encodeURIComponent(query)}`);
            console.log("JSON-data:", response); // Se exakt vad som kommer in

            console.log("Data från API:", response);
            displayResults(response);


        } catch (error) {
            console.error("Kunde inte hämta data:", error);
            alert("could not reload")
        }
    });
});

function displayResults(response) {
    const resultsContainer = document.getElementById("plant-container");
    resultsContainer.style.display = "block";
    resultsContainer.innerHTML = "";

    const plants = response.data;

    if (!Array.isArray(plants)) {
        console.error("Vi ville ha en lista men det blev: ", plants);
        return;
    }

    plants.forEach(plant=> {
        const plantDiv = document.createElement("div");
        plantDiv.innerHTML = `
            <h3>${plant.common_name}</h3>
            <p>Scientific name: ${plant.scientific_name [0]}</p>
            `;
        resultsContainer.appendChild(plantDiv);
    });

    alert("Found " + data.data.length + "plants.");
}