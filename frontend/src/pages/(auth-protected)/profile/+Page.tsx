import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { LeafIcon } from "lucide-react";
import { AccountInfoCard } from "../../../components/profile/AccountInfoCard";
import { SecurityCard } from "../../../components/profile/SecurityCard";
import { DangerZoneCard } from "../../../components/profile/DangerZoneCard";
import { ChangePasswordDialog } from "@/components/profile/ChangePasswordDialog";
import { DeleteAccountDialog } from "@/components/profile/DeleteAccountDialog";
import { getUserInfo } from "./api";
import type { UserInfo } from "./types";

export default function Page() {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isChangePasswordOpen, setIsChangePasswordOpen] = useState(false);
  const [isDeleteAccountOpen, setIsDeleteAccountOpen] = useState(false);

  async function loadUserInfo() {
    try {
      setIsLoading(true);
      setError(null);
      const data = await getUserInfo();
      setUserInfo(data);
    } catch (loadError: any) {
      const message =
        loadError?.message || "Could not load account information right now.";
      setError(message);
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadUserInfo();
  }, []);

  return (
    <div className="mx-auto w-full max-w-3xl px-4 py-8">
      <div className="mb-8 border-b pb-4">
        <div className="flex flex-wrap items-center justify-between gap-4">
            <div className="flex items-center gap-2">
              <h1 className="text-3xl font-bold tracking-tight text-foreground">
                Profile
              </h1>
              <LeafIcon className="h-5 w-5 text-primary/80" aria-hidden="true" />
          </div>

          <Button asChild size="lg" className="w-30">
            <a href="/library">Go to library</a>
          </Button>

        </div>
        <p className="mt-2 text-muted-foreground">
          Manage your account information, security, and sensitive actions.
        </p>
        <p className="mt-1 text-xs text-muted-foreground">
          Your account is safe. Our plants are guarding it.
        </p>
      </div>

      <div className="space-y-6">
        <AccountInfoCard
          userInfo={userInfo}
          isLoading={isLoading}
          error={error}
          onRetry={loadUserInfo}
        />

        <SecurityCard
          onChangePasswordClick={() => setIsChangePasswordOpen(true)}
        />

        <DangerZoneCard
          onDeleteAccountClick={() => setIsDeleteAccountOpen(true)}
        />
      </div>

      <ChangePasswordDialog
        isOpen={isChangePasswordOpen}
        onClose={() => setIsChangePasswordOpen(false)}
      />

      <DeleteAccountDialog
        isOpen={isDeleteAccountOpen}
        onClose={() => setIsDeleteAccountOpen(false)}
      />
    </div>
  );
}
