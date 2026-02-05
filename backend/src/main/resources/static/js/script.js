document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('plantID');

    searchForm.addEventListener('submit', async (event) => {
        event.preventDefault(); // Stoppa sidan från att laddas om

        const query = searchInput.value;
        if (!query) return;

        try {
            // Vi anropar din backend-endpoint: /api/search?plantName=...
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