import type {UserPlantDTO} from "@/types/UserPlantDTO";
import {useEffect, useState} from "react";
import {API_BASE_URL} from "@/config";
import {toast} from "sonner";
import {NoPlants} from "./NoPlants";
import SpinningLoader from "@/components/SpinningLoader";
import {UserPlantCard} from "./UserPlantCard";
import {Button} from "@/components/ui/button";
import {Plus} from "lucide-react";

export default function Page() {
  const [userPlants, setUserPlants] = useState<UserPlantDTO[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  async function getPlants() {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/user/plants`, {
        method: "GET",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        const json = await response.json();
        setUserPlants(json);
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
  }, []);

  if (isLoading) {
    return (
      <div className="flex h-full flex-col items-center justify-center space-y-4">
        <SpinningLoader />
      </div>
    );
  }

  if (userPlants.length < 1) {
    return (
      <div className="flex h-full flex-col items-center justify-center space-y-4">
        <NoPlants />
      </div>
    );
  }

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

        <Button asChild disabled className="py-5 px-4">
          <a href="/library/new">
            <Plus /> Add new plant
          </a>
        </Button>
      </div>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {userPlants.map((userPlant) => (
          <UserPlantCard key={userPlant.id} userPlant={userPlant} />
        ))}
      </div>
    </div>
  );
}
