import { useState } from 'react'
import './style.css'

function App() {
    // State för att hålla koll på sökord, resultat och laddning
    const [query, setQuery] = useState('')
    const [plants, setPlants] = useState([])
    const [loading, setLoading] = useState(false)

    const handleSearch = async (event) => {
        event.preventDefault() // Stoppar sidan från att laddas om

        if (!query) return

        setLoading(true) //spinner
        setPlants([])    // rensar sökfältet

        try {
            // anorap backend från port 8080
            const response = await fetch(`/api/search?plantName=${encodeURIComponent(query)}`)

            if (!response.ok) {
                throw new Error('Kunde inte hämta data från servern')
            }

            const result = await response.json()

            setPlants(result.data || [])

        } catch (error) {
            console.error("Fel vid hämtning:", error)
            alert("Kunde inte hämta plantor. Kontrollera att Java-servern körs.")
        } finally {
            setLoading(false) // spinner stängs av oavsett result.
        }
    }

    return (
        <div id="content-box">
            <div className="center-wrapper">
                <h1>Happy Plants</h1>
                <p>Your personal online Plant caregiver</p>
                <hr />

                <div className="search-container">
                    <form id="search-form" onSubmit={handleSearch}>
                        <div className="textbox-button-field">
                            <input
                                type="text"
                                placeholder="eg. Philodendron"
                                value={query}
                                onChange={(e) => setQuery(e.target.value)}
                                id="plantID"
                            />
                            <button id="search-btn" type="submit">
                                <i className="fa fa-search"></i> Sök
                            </button>
                        </div>
                    </form>
                    <h3>Take care of your plants</h3>
                </div>

                {/* SPINNER: Visas bara när loading är true */}
                {loading && (
                    <div id="loading-spinner">
                        <i className="fa fa-spinner fa-spin"></i>
                        <p>Finding your plant...</p>
                    </div>
                )}

                {/* PLANT RESULTAT: Loopar igenom listan 'plants' och skapar kort */}
                <div id="plant-container" style={{ display: plants.length > 0 ? 'grid' : 'none' }}>
                    {plants.map((plant, index) => (
                        <div key={plant.id || index} className="plant-card">
                            <h3>{plant.common_name || "Okänt namn"}</h3>
                            <p>
                                Scientific name: <i>
                                {plant.scientific_name && plant.scientific_name.length > 0
                                    ? plant.scientific_name[0]
                                    : "N/A"}
                            </i>
                            </p>

                            {/* Extra knappar från din original-HTML */}
                            <div className="card-buttons">
                                <button className="btn-pill"><i className="fa fa-camera"></i> Sights</button>
                                <button className="btn-pill"><i className="fa fa-info-circle"></i> Facts</button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>

            <footer>
                <a className="footer-text" href="">About Us</a>
                <a className="footer-text" href="/">Home</a>
            </footer>
        </div>
    )
}

export default App