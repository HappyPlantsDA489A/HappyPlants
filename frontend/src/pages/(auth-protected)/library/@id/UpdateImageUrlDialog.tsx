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
import { InfoIcon } from "lucide-react";

interface UpdateImageUrlProps {
  isOpen: boolean;
  onClose: () => void;
  userPlant: UserPlantDTO;
  onSuccess: () => void;
}

export function UpdateImageUrlDialog({
  isOpen,
  onClose,
  userPlant,
  onSuccess,
}: UpdateImageUrlProps) {
  const [loading, setLoading] = useState(false);

  async function handleSubmit(formData: FormData) {
    setLoading(true);
    const imageUrl = formData.get("imageUrl") as string;

    try {
      const response = await fetch(
        `${API_BASE_URL}/user/plants/${userPlant.id}/image-url`,
        {
          method: "PATCH",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ imageUrl: imageUrl }),
        },
      );

      if (response.ok) {
        toast.success("Image updated");
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
            <DialogTitle>Modify plant image</DialogTitle>
            <DialogDescription>
              Provide a direct link to an image of your plant.
            </DialogDescription>
          </DialogHeader>

          <div className="py-6 space-y-4">
            <Input
              name="imageUrl"
              type="url"
              placeholder="https://i.imgur.com/happyplant.png"
              autoComplete="off"
              defaultValue={userPlant.imageUrl}
            />

            <div className="rounded-md bg-primary/5 border border-primary/50 p-3 text-sm text-muted-foreground flex gap-3">
              <InfoIcon className="h-5 w-5 shrink-0 text-primary" />
              <div className="space-y-1">
                <p className="font-medium text-foreground">
                  How to get an image link:
                </p>
                <ul className="list-disc pl-4 space-y-2 text-xs">
                  <li>
                    <strong>Someones else's image:</strong> Find an image online
                    and select "Copy Image Address".
                  </li>
                  <li>
                    <strong>Your own image:</strong> Upload to a service like
                    <span className="font-mono"> Imgur</span> or{" "}
                    <span className="font-mono">PostImages</span>, then copy the
                    direct link.
                  </li>
                </ul>
              </div>
            </div>
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
