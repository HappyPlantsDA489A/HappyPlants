import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { useEffect, useState } from "react";
import { API_BASE_URL } from "@/config";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { format, addDays } from "date-fns";
import SpinningLoader from "@/components/SpinningLoader";
import { Droplets, CalendarClock, Trash2 } from "lucide-react";
import { DialogDescription } from "@/components/ui/dialog";
import { toast } from "sonner";

interface WateringHistoryDialogProps {
    isOpen: boolean;
    onClose: () => void;
    userPlant: UserPlantDTO;
    onUpdate: () => void;
}

interface WateredPlantDTO {
    occuredAt: string;
}



export function WateringHistoryDialog({ isOpen, onClose, userPlant, onUpdate}: WateringHistoryDialogProps) {

    const [history, setHistory] = useState<WateredPlantDTO[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const nextWateringDate = userPlant.lastWateredAt && userPlant.wateringFrequencyDays
        ? addDays(new Date(userPlant.lastWateredAt), userPlant.wateringFrequencyDays)
        : null;

    useEffect(() => {
        if (isOpen) {
            fetchHistory();
        }
    }, [isOpen]);

    async function fetchHistory() {
        setIsLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/user/plants/${userPlant.id}/waterings`, {
                credentials: "include",
            });
            if (response.ok) {
                const data = await response.json();
                setHistory(data);
            }
        } catch (error) {
            console.error("Failed to fetch history", error);
        } finally {
            setIsLoading(false);
        }
    }

    async function handleDelete(occuredAt: string) {
        try {
            const response = await fetch(
                `${API_BASE_URL}/user/plants/${userPlant.id}/waterings?occuredAt=${encodeURIComponent(occuredAt)}`,
                {
                    method: "DELETE",
                    credentials: "include",
                }
            );

            if (!response.ok) {
                toast.error("Could not remove watering");
                return;
            }

            toast.success("Watering removed");

            try {
                await fetchHistory();
                if (onUpdate) onUpdate();
            } catch (refreshError) {
                console.error("Could not refresh UI, but data is deleted:", refreshError);
            }

        } catch (error) {
            console.error("Network or Server error:", error);
            toast.error("An error occurred");
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="sm:max-w-md">
                <DialogHeader>
                    <DialogTitle>Watering History</DialogTitle>
                    <DialogDescription className="text-sm text-muted-foreground italic">
                        {userPlant.nickname || userPlant.plant.commonName}
                    </DialogDescription>
                </DialogHeader>

                {nextWateringDate && (
                    <div className="mb-4 p-3 rounded-lg border-2 border-green-200 bg-green-50 flex items-center gap-3">
                        <CalendarClock className="h-5 w-5 text-green-600" />
                        <div className="flex flex-col">
                            <span className="text-xs text-green-700 uppercase font-bold">Next scheduled watering</span>
                            <span className="text-sm font-semibold text-green-900">
                                {format(new Date(nextWateringDate), "PPP p")}
                            </span>
                        </div>
                    </div>
                )}

                <div className="flex flex-col gap-4 py-4 max-h-[60vh] overflow-y-auto">
                    {isLoading ? (
                        <div className="flex justify-center py-8">
                            <SpinningLoader />
                        </div>
                    ) : history.length > 0 ? (
                        <div className="space-y-3">
                            {history.map((entry, index) => (
                                <div key={index} className="flex items-center justify-between p-3 rounded-lg border bg-card group">
                                    <div className="flex items-center gap-3">
                                        <Droplets className="h-4 w-4 text-blue-500" />
                                        <span className="text-sm font-medium">
                                            {format(new Date(entry.occuredAt), "PPP p")}
                                        </span>
                                    </div>
                                    <button
                                        onClick={() => handleDelete(entry.occuredAt)}
                                        className="text-muted-foreground hover:text-destructive transition-colors p-1"
                                        title="Undo this watering"
                                    >
                                        <Trash2 className="h-4 w-4" />
                                    </button>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="text-center py-8 text-muted-foreground border-2 border-dashed rounded-lg">
                            <p className="text-lg font-medium text-foreground">No history yet</p>
                            <p className="text-sm">This plant hasn't been logged as watered yet.</p>
                        </div>
                    )}
                </div>
            </DialogContent>
        </Dialog>
    );
}