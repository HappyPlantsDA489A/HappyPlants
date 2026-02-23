import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { Badge } from "./ui/badge";
import { addDays, isPast, isToday, isTomorrow, parseISO } from "date-fns";

export default function WateringBadge({
  userPlant,
}: {
  userPlant: UserPlantDTO;
}) {
  const { lastWateredAt, createdAt, wateringFrequencyDays } = userPlant;

  if (!wateringFrequencyDays) return null;

  const referenceDateString = lastWateredAt || createdAt;

  if (!referenceDateString) return null;

  const referenceDate = parseISO(referenceDateString);

  const nextWateringDate = addDays(referenceDate, wateringFrequencyDays);

  let label = "";
  let urgency: "none" | "low" | "medium" | "high";

  if (isPast(nextWateringDate) && !isToday(nextWateringDate)) {
    label = "Watering overdue!";
    urgency = "high";
  } else if (isToday(nextWateringDate)) {
    label = "Needs watering today";
    urgency = "medium";
  } else if (isTomorrow(nextWateringDate)) {
    label = "Needs watering tomorrow";
    urgency = "low";
  } else {
    label = "Hydrated";
    urgency = "none";
  }

  switch (urgency) {
    case "none":
      break;

    case "low":
      return (
        <Badge className="p-3 text-sm" variant="secondary">
          {label}
        </Badge>
      );

    case "medium":
      return (
        <Badge className="p-3 text-sm" variant="default">
          {label}
        </Badge>
      );

    case "high":
      return (
        <Badge className="p-3 text-sm bg-red-500 text-white">{label}</Badge>
      );

    default:
      return (
        <Badge className="p-3 text-sm" variant="ghost">
          {label}
        </Badge>
      );
  }
}
