import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { useState } from "react";
import { toast } from "sonner";
import { API_BASE_URL } from "@/config";

interface UpdateWateringProps {
  isOpen: boolean;
  onClose: () => void;
  userPlant: UserPlantDTO;
  onSuccess: () => void;
}

export function UpdateWateringIntervalDialog({
  isOpen,
  onClose,
  userPlant,
  onSuccess,
}: UpdateWateringProps) {
  const [loading, setLoading] = useState(false);

  async function handleSubmit(formData: FormData) {
    setLoading(true);
    const wateringFrequencyDays = formData.get(
      "wateringFrequencyDays",
    ) as string;

    try {
      const response = await fetch(
        `${API_BASE_URL}/user/plants/${userPlant.id}/watering-frequency`,
        {
          method: "PATCH",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            wateringFrequencyDays: wateringFrequencyDays,
          }),
        },
      );

      if (response.ok) {
        toast.success("Watering interval updated");
        onSuccess();
        onClose();
      } else {
        const json = await response.json();
        throw new Error(json.message);
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    } finally {
      setLoading(false);
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <DialogContent className="sm:max-w-[500px]">
        <form action={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Modify watering interval</DialogTitle>
            <DialogDescription>
              The watering interval, measured in days, determines how often this
              plant needs to be watered. For example, if you enter "1" that
              means the plant requires watering once per day.
            </DialogDescription>
          </DialogHeader>
          <div className="py-4">
            <Input
              type="number"
              name="wateringFrequencyDays"
              autoComplete="off"
              defaultValue={userPlant.wateringFrequencyDays}
            />
            <p className="text-muted-foreground mt-1.5">
              Enter zero or leave blank to disable watering
            </p>
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose}>
              Cancel
            </Button>
            <Button type="submit" disabled={loading}>
              Save changes
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
