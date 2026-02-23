import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { useEffect, useState } from "react";
import { API_BASE_URL } from "@/config";
import { toast } from "sonner";
import SpinningLoader from "@/components/SpinningLoader";
import { Leaf, Droplets, ArrowLeft } from "lucide-react";
import { formatDistanceToNow } from "date-fns";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { usePageContext } from "vike-react/usePageContext";
import { Button } from "@/components/ui/button";
import { PlantNotFound } from "./PlantNotFound";
import WateringBadge from "@/components/WateringBadge";
import MarkAsWateredButton from "./MarkAsWateredButton";
import ConfigButton from "./ConfigButton";

export default function Page() {
  const pageContext = usePageContext();
  const { id } = pageContext.routeParams;

  const [userPlant, setUserPlant] = useState<UserPlantDTO | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  async function getPlant() {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/user/plants/${id}`, {
        method: "GET",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const json = await response.json();
        setUserPlant(json);
      } else {
        throw new Error("Could not get plant");
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    }
    setIsLoading(false);
  }

  useEffect(() => {
    getPlant();
  }, []);

  if (isLoading) {
    return (
      <div className="flex h-full flex-col items-center justify-center">
        <SpinningLoader />
      </div>
    );
  }

  if (!userPlant) {
    return (
      <div className="flex h-full flex-col items-center justify-center space-y-4">
        <PlantNotFound />
      </div>
    );
  }

  const displayName = userPlant.nickname || userPlant.plant.commonName;

  return (
    <div className="mx-auto w-full max-w-3xl px-4 py-8">
      <div className="flex flex-row justify-between mb-3">
        <Button asChild variant="ghost" className="gap-2" size="lg">
          <a href="/library">
            <ArrowLeft className="h-4 w-4" />
            Back to library
          </a>
        </Button>
        <div className="flex flex-row gap-2">
          <MarkAsWateredButton />
          <ConfigButton />
        </div>
      </div>

      <Card className="overflow-hidden py-0">
        <div className="relative aspect-[16/9] w-full overflow-hidden bg-muted">
          {userPlant.imageUrl ? (
            <img
              src={userPlant.imageUrl}
              alt={displayName}
              className="h-full w-full object-cover"
            />
          ) : (
            <div className="flex h-full w-full items-center justify-center bg-primary/10">
              <Leaf className="h-16 w-16 text-primary/70" />
            </div>
          )}

          <div className="absolute bottom-3 right-3 z-10">
            <WateringBadge userPlant={userPlant} />
          </div>
        </div>

        <CardContent className="flex flex-col gap-3 p-6">
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-foreground">
              {displayName}
            </h1>
            <p className="mt-0.5 text-base italic text-muted-foreground">
              {userPlant.plant.scientificName}
            </p>
          </div>

          <div className="flex flex-row gap-2">
            {userPlant.plant.familyName && (
              <Badge className="w-fit bg-primary/20 text-black p-3 text-md">
                {userPlant.plant.familyName}
              </Badge>
            )}

            {userPlant.wateringFrequencyDays && (
              <Badge className="w-fit bg-blue-500/20 text-black p-3 text-md">
                Needs water{" "}
                {userPlant.wateringFrequencyDays == 1
                  ? "once per day"
                  : `every ${userPlant.wateringFrequencyDays} days`}
              </Badge>
            )}
          </div>

          <div className="flex flex-col gap-2 border-t pt-4">
            {userPlant.createdAt && (
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <Leaf className="h-4 w-4" />
                <span>
                  Added{" "}
                  {formatDistanceToNow(userPlant.createdAt, {
                    addSuffix: true,
                  })}
                </span>
              </div>
            )}
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Droplets className="h-4 w-4" />
              <span>
                Watered {userPlant.timesWatered}{" "}
                {userPlant.timesWatered === 1 ? "time" : "times"}
              </span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
