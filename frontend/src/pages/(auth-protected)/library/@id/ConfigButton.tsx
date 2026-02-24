import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuPortal,
  DropdownMenuSeparator,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import type { UserPlantDTO } from "@/types/UserPlantDTO";
import { Droplet, Ellipsis, Image, Tag } from "lucide-react";
import { useState } from "react";
import { UpdateNicknameDialog } from "./UpdateNicknameDialog";
import { API_BASE_URL } from "@/config";
import { toast } from "sonner";

export default function ConfigButton({
  userPlant,
  onReload,
}: {
  userPlant: UserPlantDTO;
  onReload: () => void;
}) {
  type DialogType = "nickname" | "image" | "watering" | "delete" | null;
  const [activeDialog, setActiveDialog] = useState<DialogType>(null);
  const [isClearing, setIsClearing] = useState<boolean>(false);

  async function clearNickname() {
    try {
      setIsClearing(true);
      const response = await fetch(
        `${API_BASE_URL}/user/plants/${userPlant.id}/nickname`,
        {
          method: "PATCH",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ nickname: null }),
        },
      );

      if (response.ok) {
        toast.success("Nickname cleared");
        onReload();
      } else {
        const json = await response.json();
        throw new Error(json.message);
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    } finally {
      setIsClearing(false);
    }
  }

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button disabled={isClearing} variant="outline">
            <Ellipsis /> Options
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className="w-40" align="start">
          <DropdownMenuGroup>
            <DropdownMenuSub>
              <DropdownMenuSubTrigger>
                <Tag /> Nickname
              </DropdownMenuSubTrigger>
              <DropdownMenuPortal>
                <DropdownMenuSubContent>
                  <DropdownMenuItem
                    onSelect={() => setActiveDialog("nickname")}
                  >
                    Modify
                  </DropdownMenuItem>
                  <DropdownMenuItem onSelect={clearNickname}>
                    Clear
                  </DropdownMenuItem>
                </DropdownMenuSubContent>
              </DropdownMenuPortal>
            </DropdownMenuSub>
            <DropdownMenuSub>
              <DropdownMenuSubTrigger>
                <Image /> Image
              </DropdownMenuSubTrigger>
              <DropdownMenuPortal>
                <DropdownMenuSubContent>
                  <DropdownMenuItem>Modify</DropdownMenuItem>
                  <DropdownMenuItem>Clear</DropdownMenuItem>
                </DropdownMenuSubContent>
              </DropdownMenuPortal>
            </DropdownMenuSub>
            <DropdownMenuSub>
              <DropdownMenuSubTrigger>
                <Droplet /> Watering
              </DropdownMenuSubTrigger>
              <DropdownMenuPortal>
                <DropdownMenuSubContent>
                  <DropdownMenuItem>Change interval</DropdownMenuItem>
                  <DropdownMenuItem>View history</DropdownMenuItem>
                </DropdownMenuSubContent>
              </DropdownMenuPortal>
            </DropdownMenuSub>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem variant="destructive">
              Delete plant
            </DropdownMenuItem>
          </DropdownMenuGroup>
        </DropdownMenuContent>
      </DropdownMenu>

      <UpdateNicknameDialog
        isOpen={activeDialog === "nickname"}
        onClose={() => setActiveDialog(null)}
        userPlant={userPlant}
        onSuccess={onReload}
      />
    </>
  );
}
