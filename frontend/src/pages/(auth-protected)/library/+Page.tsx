import type {UserPlantDTO} from "@/types/UserPlantDTO";
import {useEffect, useState} from "react";
import {API_BASE_URL} from "@/config";
import {toast} from "sonner";
import SpinningLoader from "@/components/SpinningLoader";
import {UserPlantCard} from "./UserPlantCard";
import {Button} from "@/components/ui/button";
import {Plus} from "lucide-react";
import {NoPlants} from "@/pages/(auth-protected)/library/NoPlants.tsx";

export default function Page() {
  const [userPlants, setUserPlants] = useState<UserPlantDTO[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [family, setFamily] = useState<string>("");
  const [sortBy, setSortBy] = useState<string>("createdAt");
  const [direction, setDirection] = useState<string>("desc");
  const [allFamilies, setAllFamilies] = useState<string[]>([]);

  async function getPlants() {

    const queryParams = new URLSearchParams();


    queryParams.append("sortBy", sortBy);
    queryParams.append("direction", direction);

    if (family) {
      queryParams.append("family", family);
    }
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/user/plants?${queryParams.toString()}`, {
        method: "GET",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const data: UserPlantDTO[] = await response.json();

        setUserPlants(data);

        if (!family && allFamilies.length === 0) {
          const families = Array.from(new Set(data.map((up) => up.plant.familyName)))
              .filter(Boolean) as string[];
          setAllFamilies(families.sort());
        }
      } else {
        throw new Error("Could not get plants for user");
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    }
    setIsLoading(false);
  }

  useEffect(() => {
    getPlants();
  }, [family, sortBy, direction]);

  if (isLoading) {
    return (
      <div className="flex h-full flex-col items-center justify-center space-y-4">
        <SpinningLoader />
      </div>
    );
  }

  const uniqueFamilies = Array.from(new Set(userPlants.map(up => up.plant.familyName))).filter(Boolean).sort();

  return (
    <div className="mx-auto w-full max-w-7xl px-4 py-8">
      <div className="mb-8 border-b pb-4 flex flex-row items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-foreground">
            Library
          </h1>
          <p className="text-muted-foreground">
            Manage and view all your collected plants.
          </p>
        </div>

        <Button asChild className="py-5 px-4">
          <a href="/library/new">
            <Plus /> Add new plant
          </a>
        </Button>
      </div>

      <div className="mb-8 flex flex-wrap gap-6 items-end border-b pb-6">

        <div className="flex flex-col gap-2">
          <label className="text-sm font-semibold text-foreground">Filter Family</label>
          <select
              className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm focus:ring-2 focus:ring-ring"
              value={family}
              onChange={(e) => setFamily(e.target.value)}
          >
            <option value="">All Families</option>
            {uniqueFamilies.map((familyName) => (
                <option key={familyName} value={familyName}>
                  {familyName}
                </option>
            ))}
          </select>
        </div>

        <div className="flex flex-col gap-2">
          <label className="text-sm font-semibold text-foreground">Sort By</label>
          <select
              className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
          >
            <option value="createdAt">Date Added</option>
            <option value="nickname">Nickname</option>
          </select>
        </div>

        {/* Sorteringsordning */}
        <div className="flex flex-col gap-2">
          <label className="text-sm font-semibold text-foreground">Order</label>
          <select
              className="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
              value={direction}
              onChange={(e) => setDirection(e.target.value)}
          >
            <option value="desc">Newest/Z-A</option>
            <option value="asc">Oldest/A-Z</option>
          </select>
        </div>
      </div>

      {userPlants.length < 1 ? (
          <div className="flex flex-col items-center justify-center py-20">
            <NoPlants />
          </div>
      ) : (
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {userPlants.map((userPlant) => (
                <UserPlantCard key={userPlant.id} userPlant={userPlant} />
            ))}
          </div>
      )}
    </div>


  );
}
