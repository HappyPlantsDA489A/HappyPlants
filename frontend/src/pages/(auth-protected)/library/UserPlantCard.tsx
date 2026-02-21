import { Leaf } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { formatDistanceToNow } from "date-fns";

interface UserPlantCardProps {
  userPlant: UserPlantDTO;
}

export function UserPlantCard({ userPlant }: UserPlantCardProps) {
  const displayName = userPlant.nickname || userPlant.plant.commonName;

  return (
    <Card className="group relative overflow-hidden py-0 transition border hover:bg-primary/5 hover:border-primary/50">
      <a
        href={`/library/${userPlant.id}`}
        className="absolute inset-0 z-10"
        aria-label={`View details for ${displayName}`}
      />

      <div className="relative aspect-[4/3] w-full overflow-hidden bg-muted">
        {userPlant.imageUrl ? (
          <img
            src={userPlant.imageUrl}
            alt={displayName}
            className="object-cover transition-transform"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center bg-primary/10">
            <Leaf className="w-8 h-18 text-primary/70" />
          </div>
        )}
      </div>

      <CardContent className="flex flex-col gap-2 p-4">
        <div className="flex items-start justify-between gap-2">
          <div className="min-w-0 flex-1">
            <h3 className="truncate text-base font-semibold leading-tight text-card-foreground">
              {displayName}
            </h3>
            <p className="mt-0.5 truncate text-sm italic text-muted-foreground">
              {userPlant.plant.scientificName}
            </p>
          </div>
        </div>

        <div className="flex flex-wrap items-center gap-1.5 text-xs text-muted-foreground">
          {userPlant.plant.familyName && (
            <Badge className="bg-primary/20 text-black">
              {userPlant.plant.familyName}
            </Badge>
          )}
        </div>

        {userPlant.createdAt && (
          <p className="mt-1 text-xs text-muted-foreground">
            Added{" "}
            {formatDistanceToNow(userPlant.createdAt, { addSuffix: true })}
          </p>
        )}
      </CardContent>
    </Card>
  );
}
