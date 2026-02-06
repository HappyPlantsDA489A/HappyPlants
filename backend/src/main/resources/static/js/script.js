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
            const data = await response.json();

            console.log("Data från API:", data);
            displayResults(data);


        } catch (error) {
            console.error("Kunde inte hämta data:", error);
            alert("could not reload")
        }
    });
});

function displayResults(data) {
    // Här kan du senare bygga logik för att visa växterna i HTML:en
    alert("Found " + data.data.length + "plants.");
}