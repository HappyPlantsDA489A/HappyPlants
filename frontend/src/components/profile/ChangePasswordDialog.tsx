import { useMemo, useState } from "react";
import { toast } from "sonner";
import { LockIcon } from "lucide-react";
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
import { Label } from "@/components/ui/label";
import { changePassword } from "@/pages/(auth-protected)/profile/api";

interface ChangePasswordDialogProps {
  isOpen: boolean;
  onClose: () => void;
}

interface PasswordRules {
  length: boolean;
  uppercase: boolean;
  lowercase: boolean;
  digit: boolean;
  special: boolean;
}

function PasswordRule({ label, valid }: { label: string; valid: boolean }) {
  return (
    <div className={`flex items-center gap-2 ${valid ? "text-green-600" : "text-muted-foreground"}`}>
      <span>{valid ? "✔" : "✖"}</span>
      <span>{label}</span>
    </div>
  );
}

function getPasswordRules(password: string): PasswordRules {
  return {
    length: password.length >= 12,
    uppercase: /[A-Z]/.test(password),
    lowercase: /[a-z]/.test(password),
    digit: /[0-9]/.test(password),
    special: /[^A-Za-z0-9\s]/.test(password),
  };
}

export function ChangePasswordDialog({
  isOpen,
  onClose,
}: ChangePasswordDialogProps) {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const passwordRules = useMemo(() => getPasswordRules(newPassword), [newPassword]);
  const allRulesValid = Object.values(passwordRules).every(Boolean);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);

    if (newPassword !== confirmPassword) {
      setError("New password and confirmation do not match.");
      return;
    }

    if (!allRulesValid) {
      setError("Your new password does not meet the required security rules.");
      return;
    }

    try {
      setIsLoading(true);
      await changePassword({
        currentPassword,
        newPassword,
      });

      toast.success("Password updated successfully");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
      onClose();
    } catch (submitError: any) {
      const message = submitError?.message || "Could not update password";
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }

  function closeDialog() {
    setCurrentPassword("");
    setNewPassword("");
    setConfirmPassword("");
    setError(null);
    onClose();
  }

  return (
    <Dialog open={isOpen} onOpenChange={(open) => !open && closeDialog()}>
      <DialogContent>
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Change password</DialogTitle>
            <DialogDescription>
              Enter your current password and choose a strong new password.
            </DialogDescription>
          </DialogHeader>

          <div className="mt-3 flex items-center gap-2 rounded-md border border-border/70 bg-muted/30 px-3 py-2 text-xs text-muted-foreground">
            <LockIcon className="h-4 w-4" aria-hidden="true" />
            Your password is encrypted and stored securely.
          </div>

          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="current-password">Current password</Label>
              <Input
                id="current-password"
                type="password"
                autoComplete="current-password"
                value={currentPassword}
                onChange={(event) => setCurrentPassword(event.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="new-password">New password</Label>
              <Input
                id="new-password"
                type="password"
                autoComplete="new-password"
                value={newPassword}
                onChange={(event) => setNewPassword(event.target.value)}
                required
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirm-password">Confirm new password</Label>
              <Input
                id="confirm-password"
                type="password"
                autoComplete="new-password"
                value={confirmPassword}
                onChange={(event) => setConfirmPassword(event.target.value)}
                required
              />
            </div>

            <div className="space-y-1 text-sm">
              <PasswordRule label="Minimum 12 characters" valid={passwordRules.length} />
              <PasswordRule
                label="At least one uppercase letter"
                valid={passwordRules.uppercase}
              />
              <PasswordRule
                label="At least one lowercase letter"
                valid={passwordRules.lowercase}
              />
              <PasswordRule label="At least one digit" valid={passwordRules.digit} />
              <PasswordRule
                label="At least one special character"
                valid={passwordRules.special}
              />
            </div>

            {error && (
              <p className="rounded-md border border-destructive/30 bg-destructive/5 p-2 text-sm text-destructive">
                {error}
              </p>
            )}
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={closeDialog}>
              Cancel
            </Button>
            <Button type="submit" disabled={isLoading}>
              Save password
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
