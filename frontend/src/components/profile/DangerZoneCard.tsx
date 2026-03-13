import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

interface DangerZoneCardProps {
  onDeleteAccountClick: () => void;
}

export function DangerZoneCard({ onDeleteAccountClick }: DangerZoneCardProps) {
  return (
    <Card className="border-destructive/30">
      <CardHeader>
        <CardTitle className="text-destructive">Danger Zone</CardTitle>
        <CardDescription>
          Permanently delete your account and all associated data.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Button onClick={onDeleteAccountClick} variant="destructive" size="lg">
          Delete Account
        </Button>
      </CardContent>
    </Card>
  );
}
