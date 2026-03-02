import type {UserPlantDTO} from "@/types/UserPlantDTO";
import {useEffect, useState} from "react";
import {API_BASE_URL} from "@/config";
import {toast} from "sonner";
import SpinningLoader from "@/components/SpinningLoader";
import {Leaf, Droplets, ArrowLeft} from "lucide-react";
import {formatDistanceToNow} from "date-fns";
import {Badge} from "@/components/ui/badge";
import {Card, CardContent} from "@/components/ui/card";
import {usePageContext} from "vike-react/usePageContext";
import {Button} from "@/components/ui/button";
import {PlantNotFound} from "./PlantNotFound";
import WateringBadge from "@/components/WateringBadge";
import MarkAsWateredButton from "./MarkAsWateredButton";
import ConfigButton from "./ConfigButton";

import {
    Accordion,
    AccordionContent,
    AccordionItem,
    AccordionTrigger,
} from "@/components/ui/accordion"

import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
} from "@/components/ui/tooltip"

export default function Page() {
    const pageContext = usePageContext();
    const {id} = pageContext.routeParams;

    const [userPlant, setUserPlant] = useState<UserPlantDTO | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [isWatering, setIsWatering] = useState<boolean>(false);

    const sunColor: Record<string, string> = {
        "Full Shade": "bg-neutral-300 text-neutral-800",
        "Part Shade": "bg-violet-200 text-violet-900",
        "Part Sun / Part Shade": "bg-amber-200 text-amber-900",
        "Full Sun": "bg-yellow-300 text-yellow-900"
    };


    async function getPlant(silent?: boolean) {
        try {
            if (!silent) {
                setIsLoading(true);
            }
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

    async function markAsWatered() {
        try {
            setIsWatering(true);
            const response = await fetch(
                `${API_BASE_URL}/user/plants/${userPlant?.id}/water`,
                {
                    method: "POST",
                    credentials: "include",
                    headers: {
                        "Content-Type": "application/json",
                    },
                },
            );

            if (response.ok) {
                toast.success("Plant watered");
                updateLastWatered();
            } else {
                const json = await response.json();
                throw new Error(json.message);
            }
        } catch (error: any) {
            toast.error(error.message);
            console.log(error);
        }
        setIsWatering(false);
    }

    function normalizeSunDescription(value: string): string {
        return value
            .toLowerCase()
            .replace(/[_-]/g, " ")
            .replace(/\s*\/\s*/g, " / ")
            .replace(/\b\w/g, (c) => c.toUpperCase());
    }

    const updateLastWatered = () => {
        setUserPlant((prev) => {
            if (!prev) return null;

            return {
                ...prev,
                lastWateredAt: new Date().toISOString(),
                timesWatered: prev.timesWatered + 1,
            };
        });
    };

    useEffect(() => {
        getPlant();
    }, []);

    if (isLoading) {
        return (
            <div className="flex h-full flex-col items-center justify-center">
                <SpinningLoader/>
            </div>
        );
    }

    if (!userPlant) {
        return (
            <div className="flex h-full flex-col items-center justify-center space-y-4">
                <PlantNotFound/>
            </div>
        );
    }

    const displayName = userPlant.nickname || userPlant.plant.commonName;

    return (
        <div className="mx-auto w-full max-w-3xl px-4 py-8">
            <div className="flex flex-row justify-between mb-3">
                <Button asChild variant="ghost" className="gap-2" size="lg">
                    <a href="/library">
                        <ArrowLeft className="h-4 w-4"/>
                        Back to library
                    </a>
                </Button>
                <div className="flex flex-row gap-2">
                    <MarkAsWateredButton
                        onClick={markAsWatered}
                        userPlant={userPlant}
                        isWatering={isWatering}
                    />
                    <ConfigButton userPlant={userPlant} onReload={() => getPlant(true)}/>
                </div>
            </div>
            <Card className="overflow-hidden py-0">
                <div className="relative aspect-[4/3] w-full overflow-hidden bg-muted flex items-center justify-center">
                    {(() => {
                        const imageToShow = userPlant.imageUrl ?? userPlant.plant.imageUrl;
                        return imageToShow ? (
                            <img
                                src={imageToShow}
                                alt={displayName}
                                className="h-full w-full object-cover"
                                onError={(e) => {
                                    e.currentTarget.style.display = "none";
                                }}
                            />
                        ) : (
                            <div className="flex h-full w-full items-center justify-center bg-primary/10">
                                <Leaf className="h-16 w-16 text-primary/70"/>
                            </div>
                        );
                    })()}

                    <div className="absolute bottom-3 right-3 z-10">
                        <WateringBadge userPlant={userPlant}/>
                    </div>
                </div>

                <CardContent className="flex flex-col gap-3 p-6">
                    <div className="flex flex-wrap items-baseline gap-2">
                        <h1 className="text-2xl font-bold tracking-tight text-foreground">
                            {displayName}
                        </h1>

                        {userPlant.plant.scientificName && (
                            <span className="text-xl italic text-muted-foreground">
                ({userPlant.plant.scientificName})
              </span>
                        )}
                    </div>

                    <TooltipProvider>
                        <div className="flex flex-row gap-2">

                            {userPlant.plant.sunDescription && (() => {
                                const normalized = normalizeSunDescription(userPlant.plant.sunDescription);
                                const color = sunColor[normalized] ?? "bg-primary/20";

                                return (
                                    <Tooltip>
                                        <TooltipTrigger asChild>
                                            <Badge className={`w-fit ${color} text-black text-md p-3`}>
                                                {normalized}
                                            </Badge>
                                        </TooltipTrigger>
                                        <TooltipContent>
                                            <p>Sun exposure</p>
                                        </TooltipContent>
                                    </Tooltip>
                                );
                            })()}

                            {userPlant.wateringFrequencyDays && (
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Badge className="w-fit bg-blue-500/20 text-black p-3 text-md">
                                            Needs water{" "}
                                            {userPlant.wateringFrequencyDays == 1
                                                ? "once per day"
                                                : `every ${userPlant.wateringFrequencyDays} days`}
                                        </Badge>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Watering frequency</p>
                                    </TooltipContent>
                                </Tooltip>
                            )}

                            {userPlant.plant.familyName && (
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Badge className="w-fit bg-primary/20 text-black p-3 text-md">
                                            {userPlant.plant.familyName}
                                        </Badge>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Family</p>
                                    </TooltipContent>
                                </Tooltip>
                            )}

                            {userPlant.plant.genus && (
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Badge className="w-fit bg-green-200 text-black p-3 text-md">
                                            {userPlant.plant.genus}
                                        </Badge>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Genus</p>
                                    </TooltipContent>
                                </Tooltip>
                            )}

                            {userPlant.plant.speciesEpithet && (
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Badge className="w-fit bg-green-100 text-black p-3 text-md">
                                            {userPlant.plant.speciesEpithet}
                                        </Badge>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Species epithet</p>
                                    </TooltipContent>
                                </Tooltip>
                            )}

                            {userPlant.plant.cultivar && (
                                <Tooltip>
                                    <TooltipTrigger asChild>
                                        <Badge className="w-fit bg-purple-200 text-black p-3 text-md">
                                            {userPlant.plant.cultivar}
                                        </Badge>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                        <p>Cultivar</p>
                                    </TooltipContent>
                                </Tooltip>
                            )}
                        </div>
                    </TooltipProvider>


                    <Accordion type="single" collapsible className="w-full">
                        {userPlant.plant.plantDescription && (
                            <AccordionItem value="plant-description">
                                <AccordionTrigger>Plant Description</AccordionTrigger>
                                <AccordionContent>
                                    {userPlant.plant.plantDescription}
                                </AccordionContent>
                            </AccordionItem>
                        )}

                        {userPlant.plant.wateringDescription && (
                            <AccordionItem value="watering-description">
                                <AccordionTrigger>Watering Instructions</AccordionTrigger>
                                <AccordionContent>
                                    {userPlant.plant.wateringDescription}
                                </AccordionContent>
                            </AccordionItem>
                        )}
                    </Accordion>

                    <div className="flex flex-col gap-2 border-t pt-4">
                        {userPlant.createdAt && (
                            <div className="flex items-center gap-2 text-sm text-muted-foreground">
                                <Leaf className="h-4 w-4"/>
                                <span>
                  Added{" "}
                                    {formatDistanceToNow(userPlant.createdAt, {
                                        addSuffix: true,
                                    })}
                </span>
                            </div>
                        )}
                        <div className="flex items-center gap-2 text-sm text-muted-foreground">
                            <Droplets className="h-4 w-4"/>
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
