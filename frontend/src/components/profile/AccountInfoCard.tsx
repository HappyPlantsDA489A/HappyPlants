import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Skeleton } from "@/components/ui/skeleton";
import { UserRoundPen } from "lucide-react";
import type { UserInfo } from "../../pages/(auth-protected)/profile/types";

interface AccountInfoCardProps {
  userInfo: UserInfo | null;
  isLoading: boolean;
  error: string | null;
  onRetry: () => void;
  onDisplayNameChange: (displayName: string) => Promise<void>;
}

export function AccountInfoCard({
  userInfo,
  isLoading,
  error,
  onRetry,
  onDisplayNameChange,
}: AccountInfoCardProps) {
  const [isEditingDisplayName, setIsEditingDisplayName] = useState(false);
  const [displayNameDraft, setDisplayNameDraft] = useState("");
  const [displayNameError, setDisplayNameError] = useState<string | null>(null);
  const [isSavingDisplayName, setIsSavingDisplayName] = useState(false);

  useEffect(() => {
    if (!isEditingDisplayName && userInfo) {
      setDisplayNameDraft(userInfo.displayName);
      setDisplayNameError(null);
    }
  }, [isEditingDisplayName, userInfo]);

  function startEditingDisplayName() {
    if (!userInfo) return;
    setDisplayNameDraft(userInfo.displayName);
    setDisplayNameError(null);
    setIsEditingDisplayName(true);
  }

  function cancelEditingDisplayName() {
    setIsEditingDisplayName(false);
    setDisplayNameDraft(userInfo?.displayName ?? "");
    setDisplayNameError(null);
  }

  async function saveDisplayName() {
    const trimmedDisplayName = displayNameDraft.trim();

    if (!trimmedDisplayName) {
      setDisplayNameError("Display name cannot be empty.");
      return;
    }

    if (trimmedDisplayName === userInfo?.displayName) {
      setIsEditingDisplayName(false);
      setDisplayNameError(null);
      return;
    }

    try {
      setIsSavingDisplayName(true);
      setDisplayNameError(null);
      await onDisplayNameChange(trimmedDisplayName);
      setIsEditingDisplayName(false);
    } catch (saveError: any) {
      const message = saveError?.message || "Could not update display name.";
      setDisplayNameError(message);
    } finally {
      setIsSavingDisplayName(false);
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Account Information</CardTitle>
        <CardDescription>Basic account details linked to your profile.</CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        {isLoading && (
          <div className="space-y-4">
            <div className="space-y-2">
              <Skeleton className="h-4 w-20" />
              <Skeleton className="h-8 w-full" />
            </div>
            <div className="space-y-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-8 w-full" />
            </div>
          </div>
        )}

        {!isLoading && error && (
          <div className="rounded-lg border border-destructive/30 bg-destructive/5 p-3">
            <p className="text-sm text-destructive">{error}</p>
            <Button onClick={onRetry} variant="outline" className="mt-3">
              Retry
            </Button>
          </div>
        )}

        {!isLoading && !error && userInfo && (
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="profile-email">Email</Label>
              <p>{userInfo.email}</p>
            </div>
            <div className="flex flex-wrap items-end justify-between gap-3">
              <div className="min-w-0 flex-1 space-y-2">
                <Label htmlFor="profile-display-name">Display Name</Label>
                {isEditingDisplayName ? (
                  <div className="space-y-2">
                    <Input
                      autoFocus
                      id="profile-display-name"
                      value={displayNameDraft}
                      disabled={isSavingDisplayName}
                      maxLength={255}
                      onChange={(event) => setDisplayNameDraft(event.target.value)}
                      onBlur={() => {
                        if (!isSavingDisplayName) {
                          void saveDisplayName();
                        }
                      }}
                      onKeyDown={(event) => {
                        if (event.key === "Enter") {
                          event.preventDefault();
                          void saveDisplayName();
                        }

                        if (event.key === "Escape") {
                          event.preventDefault();
                          cancelEditingDisplayName();
                        }
                      }}
                    />
                    {displayNameError && (
                      <p className="text-sm text-destructive">{displayNameError}</p>
                    )}
                    {!displayNameError && isSavingDisplayName && (
                      <p className="text-sm text-muted-foreground">Saving display name...</p>
                    )}
                  </div>
                ) : (
                  <p>{userInfo.displayName}</p>
                )}
              </div>
              <Button
                type="button"
                variant="ghost"
                size="icon-sm"
                aria-label={
                  isEditingDisplayName ? "Cancel editing display name" : "Edit display name"
                }
                disabled={isSavingDisplayName}
                onMouseDown={(event) => event.preventDefault()}
                onClick={() => {
                  if (isEditingDisplayName) {
                    cancelEditingDisplayName();
                    return;
                  }

                  startEditingDisplayName();
                }}
              >
                <UserRoundPen className="size-8 text-primary/80" aria-hidden="true" />
              </Button>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
