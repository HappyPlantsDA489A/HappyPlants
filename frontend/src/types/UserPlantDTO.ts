import type { PlantDTO } from "./PlantDTO";

export interface UserPlantDTO {
  id: string;
  nickname?: string;
  imageUrl?: string;
  wateringFrequencyDays?: number;
  createdAt?: string;
  diedAt?: string;
  plant: PlantDTO;
}
