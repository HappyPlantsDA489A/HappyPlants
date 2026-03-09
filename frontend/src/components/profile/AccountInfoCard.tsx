import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Skeleton } from "@/components/ui/skeleton";
import type { UserInfo } from "../../pages/(auth-protected)/profile/types";

interface AccountInfoCardProps {
  userInfo: UserInfo | null;
  isLoading: boolean;
  error: string | null;
  onRetry: () => void;
}

export function AccountInfoCard({
  userInfo,
  isLoading,
  error,
  onRetry,
}: AccountInfoCardProps) {
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
            <div className="space-y-2">
              <Label htmlFor="profile-display-name">Display Name</Label>
              <p>{userInfo.displayName}</p>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
