import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { useState } from "react";
import { toast } from "sonner";
import { API_BASE_URL } from "@/config";

interface UpdateNicknameProps {
  isOpen: boolean;
  onClose: () => void;
  userPlant: UserPlantDTO;
  onSuccess: () => void;
}

export function UpdateNicknameDialog({
  isOpen,
  onClose,
  userPlant,
  onSuccess,
}: UpdateNicknameProps) {
  const [loading, setLoading] = useState(false);

  async function handleSubmit(formData: FormData) {
    setLoading(true);
    const newName = formData.get("nickname") as string;

    try {
      const response = await fetch(
        `${API_BASE_URL}/user/plants/${userPlant.id}/nickname`,
        {
          method: "PATCH",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ nickname: newName }),
        },
      );

      if (response.ok) {
        toast.success("Nickname updated");
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
      <DialogContent>
        <form action={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Modify plant nickname</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <Input
              name="nickname"
              autoComplete="off"
              defaultValue={userPlant.nickname}
            />
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
