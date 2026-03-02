export interface PlantDTO {
  id: string;
  perenualId?: number;
  commonName: string;
  scientificName: string;
  familyName?: string;
  cultivar?: string;
  speciesEpithet?: string;
  genus?: string;
  plantDescription?: string;
  wateringDescription?: string;
  sunDescription?: string;
  imageUrl?: string;
}
