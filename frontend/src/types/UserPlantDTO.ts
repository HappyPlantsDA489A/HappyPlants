import type { PlantDTO } from "./PlantDTO";

export interface UserPlantDTO {
  id: string;
  nickname?: string;
  imageUrl?: string;
  wateringFrequencyDays?: number;
  createdAt?: string;
  diedAt?: string;
  lastWateredAt?: string;
  timesWatered: number;
  plant: PlantDTO;
}
