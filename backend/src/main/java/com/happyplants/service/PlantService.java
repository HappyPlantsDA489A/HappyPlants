package com.happyplants.service;

import com.happyplants.model.Plant;
import com.happyplants.dto.internal.PerenualPlantData;
import com.happyplants.dto.response.PerenualSearchPlantResponse;
import com.happyplants.dto.response.PlantResponse;
import com.happyplants.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final PerenualApiService perenualApiService;
    private final WikipediaService wikipediaService;
    private final PerenualCacheService perenualCacheService;

    public PlantService(PlantRepository plantRepository, PerenualApiService perenualApiService, WikipediaService wikipediaService, PerenualCacheService perenualCacheService) {
        this.plantRepository = plantRepository;
        this.perenualApiService = perenualApiService;
        this.wikipediaService = wikipediaService;
        this.perenualCacheService = perenualCacheService;
    }

    public Plant getOrCreatePlant(int perenualId) {
        return plantRepository.findByPerenualId(perenualId)
                .map(existing -> {
                    if (!existing.isImageSearched()) {
                        String wikiUrl = wikipediaService.getPlantImageUrl(
                                existing.getScientificName(), existing.getCommonName()
                        );
                        existing.setWikipediaImageUrl(wikiUrl);
                        existing.setImageSearched(true);
                        return plantRepository.save(existing);
                    }
                    return existing;
                })
                .orElseGet(() -> {

                    PerenualPlantData plantDTO;

                    if (perenualId <= 3000) {
                        plantDTO = perenualApiService.getPlantById(perenualId);
                    } else {
                        plantDTO = perenualApiService.getPartialPlantById(perenualId);
                    }

                    Plant plant = convertDtoToPlant(plantDTO);

                    if (perenualId <= 3000) {
                        String careGuideWatering = perenualApiService.getWateringDescription(perenualId);
                        if (careGuideWatering != null) {
                            plant.setWateringDescription(careGuideWatering);
                        }
                    }

                    String wikiUrl = wikipediaService.getPlantImageUrl(
                            plant.getScientificName(), plant.getCommonName()
                    );
                    plant.setWikipediaImageUrl(wikiUrl);
                    plant.setImageSearched(true);

                    return plantRepository.save(plant);
                });
    }

    public PlantResponse convertToDto(Plant plant) {
        // Perenual fresh image (with Wikipedia fallback) takes priority over stored Wikipedia URL
        String perenualImageUrl = perenualCacheService.getFreshImageUrl(
                plant.getPerenualId(),
                plant.getScientificName(),
                plant.getCommonName()
        );
        String resolvedImageUrl = perenualImageUrl != null
                ? perenualImageUrl
                : plant.getWikipediaImageUrl();

        return new PlantResponse(
                plant.getId(),
                plant.getPerenualId(),
                plant.getCommonName(),
                plant.getScientificName(),
                plant.getFamilyName(),
                plant.getCultivar(),
                plant.getSpeciesEpithet(),
                plant.getGenus(),
                plant.getPlantDescription(),
                plant.getWateringDescription(),
                plant.getSunDescription(),
                resolvedImageUrl
        );
    }

    /**
     * Enriches search results with images.
     * For plants that have no image from the Perenual search response,
     * resolves via Perenual detail endpoint first, then Wikipedia fallback.
     * Uses parallelStream to avoid sequential latency for multiple Wikipedia lookups.
     */
    public List<PerenualSearchPlantResponse> enrichWithImages(List<PerenualSearchPlantResponse> results) {
        return results.parallelStream().map(p -> {
            if (p.imageUrl() != null && !p.imageUrl().isBlank()) return p;
            String resolved = perenualCacheService.getFreshImageUrl(
                    p.perenualId(),
                    p.scientificName(),
                    p.commonName()
            );
            if (resolved == null) return p;
            return new PerenualSearchPlantResponse(
                    p.perenualId(),
                    p.commonName(),
                    p.scientificName(),
                    p.familyName(),
                    p.cultivar(),
                    p.speciesEpithet(),
                    p.genus(),
                    resolved
            );
        }).toList();
    }

    private Plant convertDtoToPlant(PerenualPlantData plantDto) {
        Plant plant = new Plant();
        plant.setPerenualId(plantDto.perenualId());
        plant.setCommonName(plantDto.commonName());
        plant.setScientificName(plantDto.scientificName().isEmpty() ? null : plantDto.scientificName().get(0));
        plant.setFamilyName(plantDto.familyName());
        plant.setCultivar(plantDto.cultivar());
        plant.setSpeciesEpithet(plantDto.speciesEpithet());
        plant.setGenus(plantDto.genus());
        plant.setPlantDescription(plantDto.plantDescription());
        plant.setWateringDescription(plantDto.wateringDescription());
        plant.setSunDescription(
                plantDto.sunDescription() != null && !plantDto.sunDescription().isEmpty()
                        ? plantDto.sunDescription().get(0)
                        : null
        );
        return plant;
    }
}
