import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { KeyRoundIcon } from "lucide-react";

interface SecurityCardProps {
  onChangePasswordClick: () => void;
}

export function SecurityCard({ onChangePasswordClick }: SecurityCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Security</CardTitle>
        <CardDescription>
          Keep your account secure by updating your password regularly.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Button onClick={onChangePasswordClick} variant="outline" size="lg">
          <KeyRoundIcon className="h-4 w-4" />
          Change Password
        </Button>
      </CardContent>
    </Card>
  );
}
