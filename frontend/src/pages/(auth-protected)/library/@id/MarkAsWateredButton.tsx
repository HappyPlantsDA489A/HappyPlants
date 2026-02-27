import { Button } from "@/components/ui/button";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { Droplet, Loader2 } from "lucide-react";
import { differenceInMinutes, parseISO } from "date-fns";

export default function MarkAsWateredButton({
  userPlant,
  isWatering,
  onClick,
}: {
  userPlant: UserPlantDTO;
  isWatering: boolean;
  onClick: () => void;
}) {
  const lastWateredDate = userPlant.lastWateredAt
    ? parseISO(userPlant.lastWateredAt)
    : null;
  const minutesSinceWatered = lastWateredDate
    ? differenceInMinutes(new Date(), lastWateredDate)
    : Infinity;

  const isRecentlyWatered = minutesSinceWatered < 1;

  if (isWatering) {
    return (
      <Button
        variant="outline"
        className="bg-primary/10 hover:bg-primary/10 border-primary/50"
        disabled={true}
      >
        <Loader2 className="animate-spin" /> Watering
      </Button>
    );
  }

  return (
    <Button
      variant="outline"
      className="bg-primary/10 hover:bg-primary/10 border-primary/50"
      disabled={isRecentlyWatered}
      onClick={onClick}
    >
      <Droplet />
      {isRecentlyWatered ? "Just watered" : "Mark as watered"}
    </Button>
  );
}
