package com.happyplants.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.happyplants.dto.PerenualPlantDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class PerenualCacheService {

    private final PerenualApiService perenualApiService;
    private final WikipediaService wikipediaService;

    /**
     * Cache: perenualId -> Optional<imageUrl>
     *
     * Optional is used as the value type so that null results (plants with no image) are also cached.
     * This prevents repeated API calls for plants that genuinely have no image. TTL 6 hours, max 500 entries (LRU eviction).
     */
    private final Cache<Integer, Optional<String>> imageCache = Caffeine.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .maximumSize(500)
            .build();

    public PerenualCacheService(PerenualApiService perenualApiService, WikipediaService wikipediaService) {
        this.perenualApiService = perenualApiService;
        this.wikipediaService = wikipediaService;
    }

    /**
     * Returns a fresh image URL for the given perenualId.
     * Tries Perenual detail endpoint first, then Wikipedia as fallback using
     * scientific name and common name.
     * Result is cached for 6 hours. Returns null if no image is available.
     */
    public String getFreshImageUrl(Integer perenualId, String scientificName, String commonName) {
        if (perenualId == null) return null;

        return imageCache.get(perenualId, id -> {
            try {
                PerenualPlantDTO dto = perenualApiService.getPlantById(id);
                if (dto != null && dto.defaultImage() != null) {
                    String url = dto.defaultImage().originalUrl();
                    if (url == null || url.isBlank()) url = dto.defaultImage().regularUrl();
                    if (url == null || url.isBlank()) url = dto.defaultImage().mediumUrl();
                    if (url == null || url.isBlank()) url = dto.defaultImage().smallUrl();
                    if (url == null || url.isBlank()) url = dto.defaultImage().thumbnail();
                    if (url != null && !url.isBlank()) return Optional.of(url);
                }
            } catch (Exception e) {
                // Perenual unavailable or quota exceeded — fall through to Wikipedia
            }
            String wikiUrl = wikipediaService.getPlantImageUrl(scientificName, commonName);
            return Optional.ofNullable(wikiUrl);
        }).orElse(null);
    }

    /**
     * Convenience overload when scientific_name comes as a List (from search results).
     */
    public String getFreshImageUrl(Integer perenualId, List<String> scientificNames, String commonName) {
        String scientificName = (scientificNames != null && !scientificNames.isEmpty())
                ? scientificNames.get(0)
                : null;
        return getFreshImageUrl(perenualId, scientificName, commonName);
    }
}
