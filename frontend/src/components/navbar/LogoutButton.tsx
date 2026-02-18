import { Button } from "@/components/ui/button";
import { useState } from "react";
import { API_BASE_URL } from "@/config";
import { reload } from "vike/client/router";

export default function LogoutButton() {
  const [isLoading, setIsLoading] = useState<boolean>(false);

  async function handleLogout() {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/auth/log-out`, {
        method: "DELETE",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok) {
        await reload();
      } else {
        const json = await response.json();
        throw new Error(json.title);
      }
    } catch (error: any) {
      console.log(error);
    }
    setIsLoading(false);
  }
  return (
    <Button
      onClick={handleLogout}
      disabled={isLoading}
      size="lg"
      variant="destructive"
    >
      Log out
    </Button>
  );
}
