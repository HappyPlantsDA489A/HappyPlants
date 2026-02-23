"use client";

import { useState } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Sprout, Leaf, TreesIcon as Tree, Plus, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { API_BASE_URL } from "@/config";
import type { PerenualSearchPlantDTO } from "@/types/PerenualSearchPlantDTO";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { toast } from "sonner";
import { navigate } from "vike/client/router";

function PlantResultCard({ plant }: { plant: PerenualSearchPlantDTO }) {
  const [loading, setLoading] = useState(false);

  async function handleAdd() {
    if (!plant.id || loading) return;
    setLoading(true);
    try {
      const response = await fetch(`${API_BASE_URL}/user/plants/${plant.id}`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const json = (await response.json()) as UserPlantDTO;

        const navigationPromise = navigate(`/library/${json.id}`);
        await navigationPromise;
        toast.success("Plant added to your library", {
          description:
            "You can now proceed to setting a nickname and defining your plant's watering interval",
          duration: 5000,
        });
      } else {
        throw new Error("Could not get plants for user");
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    } finally {
      setLoading(false);
    }
  }

  return (
    <Card className="border-border/60 bg-card hover:border-primary/30 transition-colors">
      <CardContent className="p-5">
        <div className="flex items-center gap-4">
          <div className="w-11 h-11 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
            <Leaf className="w-5 h-5 text-primary" />
          </div>
          <div className="flex-1 min-w-0">
            <h3 className="font-semibold text-foreground text-base truncate">
              {plant.common_name || "Unknown plant"}
            </h3>
            {plant.scientific_name && plant.scientific_name.length > 0 && (
              <p className="text-sm text-muted-foreground italic mt-0.5 truncate">
                {plant.scientific_name.join(", ")}
              </p>
            )}
            <div className="flex flex-wrap gap-x-4 gap-y-1 mt-2.5">
              {plant.family && (
                <span className="text-xs text-muted-foreground">
                  <span className="font-medium text-foreground/70">
                    Family:
                  </span>{" "}
                  {plant.family}
                </span>
              )}
              {plant.genus && (
                <span className="text-xs text-muted-foreground">
                  <span className="font-medium text-foreground/70">Genus:</span>{" "}
                  {plant.genus}
                </span>
              )}
              {plant.species_epithet && (
                <span className="text-xs text-muted-foreground">
                  <span className="font-medium text-foreground/70">
                    Species:
                  </span>{" "}
                  {plant.species_epithet}
                </span>
              )}
              {plant.cultivar && (
                <span className="text-xs text-muted-foreground">
                  <span className="font-medium text-foreground/70">
                    Cultivar:
                  </span>{" "}
                  {plant.cultivar}
                </span>
              )}
            </div>
          </div>
          <Button
            size="sm"
            className="shrink-0"
            disabled={loading || !plant.id}
            onClick={handleAdd}
          >
            {loading ? (
              <Loader2 className="w-4 h-4 mr-1.5 animate-spin" />
            ) : (
              <Plus className="w-4 h-4 mr-1.5" />
            )}
            {loading ? "Adding..." : "Add"}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

export function SearchResultsSkeleton() {
  return (
    <div className="flex flex-col gap-3">
      {Array.from({ length: 4 }).map((_, i) => (
        <Card key={i} className="border-border/60">
          <CardContent className="p-5">
            <div className="flex items-start gap-4">
              <Skeleton className="w-11 h-11 rounded-full shrink-0" />
              <div className="flex-1">
                <Skeleton className="h-5 w-48 mb-1.5" />
                <Skeleton className="h-4 w-36 mb-3" />
                <div className="flex gap-4">
                  <Skeleton className="h-3 w-24" />
                  <Skeleton className="h-3 w-20" />
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

export function SearchResultsEmpty({ query }: { query: string }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-16 h-16 bg-muted rounded-full flex items-center justify-center mb-4">
        <Tree className="w-8 h-8 text-muted-foreground" />
      </div>
      <h3 className="font-medium text-foreground mb-1">No plants found</h3>
      <p className="text-sm text-muted-foreground max-w-xs">
        {"We couldn't find any plants matching \""}
        {query}
        {'". Try a different name.'}
      </p>
    </div>
  );
}

export function SearchResultsList({
  plants,
}: {
  plants: PerenualSearchPlantDTO[];
}) {
  return (
    <div className="flex flex-col gap-3">
      <p className="text-sm text-muted-foreground mb-1">
        {plants.length} {plants.length === 1 ? "result" : "results"} found
      </p>
      {plants.map((plant) => (
        <PlantResultCard key={plant.id ?? plant.common_name} plant={plant} />
      ))}
    </div>
  );
}

export function SearchResultsInitial() {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center">
      <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mb-4">
        <Sprout className="w-8 h-8 text-primary" />
      </div>
      <h3 className="font-medium text-foreground mb-1">Search for a plant</h3>
      <p className="text-sm text-muted-foreground max-w-xs">
        Type a plant name above and hit search to explore the plant database.
      </p>
    </div>
  );
}
