export interface PerenualPlantDTO {
  id?: number;
  common_name?: string;
  scientific_name?: Array<string>;
  family?: string;
  cultivar?: string;
  species_epithet?: string;
  genus?: string;
  description?: string;
  watering?: string;
  sunlight?: Array<string>;
}
