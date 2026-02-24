import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogMedia,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Button } from "@/components/ui/button";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { Trash2Icon } from "lucide-react";
import { useState } from "react";
import { API_BASE_URL } from "@/config";
import { toast } from "sonner";
import { navigate } from "vike/client/router";

interface DeletePlantProps {
  isOpen: boolean;
  onClose: () => void;
  userPlant: UserPlantDTO;
  onSuccess: () => void;
}

export function DeletePlantAlertDialog({
  isOpen,
  onClose,
  userPlant,
  onSuccess,
}: DeletePlantProps) {
  const [loading, setLoading] = useState(false);

  async function handleSubmit(formData: FormData) {
    setLoading(true);
    const newName = formData.get("nickname") as string;

    try {
      const response = await fetch(
        `${API_BASE_URL}/user/plants/${userPlant.id}`,
        {
          method: "DELETE",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ nickname: newName }),
        },
      );

      if (response.ok) {
        toast.success("Plant deleted");
        navigate(`/library`);
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
    <AlertDialog open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <AlertDialogContent size="sm">
        <form action={handleSubmit}>
          <AlertDialogHeader>
            <AlertDialogMedia className="bg-destructive/10 text-destructive dark:bg-destructive/20 dark:text-destructive">
              <Trash2Icon />
            </AlertDialogMedia>
            <AlertDialogTitle>Delete plant?</AlertDialogTitle>
            <AlertDialogDescription className="mb-3">
              This will permanently delete this plant and it's watering history
              from your library.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel variant="outline">Cancel</AlertDialogCancel>
            <AlertDialogAction
              type="submit"
              disabled={loading}
              variant="destructive"
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </form>
      </AlertDialogContent>
    </AlertDialog>
  );
}
