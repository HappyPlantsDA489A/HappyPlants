import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Leaf } from "lucide-react";
import { navigate} from "vike/client/router";
import { API_BASE_URL } from "@/config";
import { toast } from "sonner";

export default function Page() {
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [password, setPassword] = useState("");
  const [passwordRules, setPasswordRules] = useState(getPasswordRules(""));
  const [isLoading, setIsLoading] = useState<boolean>(false);

  async function handleRegister() {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: "POST",
        credentials: "include",
        body: JSON.stringify({
          email: email,
          displayName: displayName,
          password: password,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      });

      const json = await response.json();

      if (response.ok) {
        toast.success("Account created", { description: "You may now log in" });
        navigate("/auth/login");
      } else if (response.status == 400 || response.status == 409) {
        throw new Error(json.detail);
      } else {
        throw new Error(json.message);
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    }
    setIsLoading(false);
  }

  function PasswordRule({label, valid} : {label:string; valid:boolean}) {
    return (
        <div className={`flex items-center gap-2 ${valid ? "text-green-600" : "text-red-500"}`}>
          <span>{valid ? "✔" : "✖"}</span>
          <span>{label}</span>
        </div>
    );
  }

  function getPasswordRules(password: string) {
    return {
      length: password.length >= 12,
      uppercase: /[A-Z]/.test(password),
      lowercase: /[a-z]/.test(password),
      digit: /[0-9]/.test(password),
      special: /[^A-Za-z0-9\s]/.test(password)
    };
  }
  const allValid = Object.values(passwordRules).every(Boolean);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4">
      <Card className="w-full max-w-md border-0 shadow-2xl">
        <CardHeader className="text-center space-y-3">
          <div className="mx-auto w-16 h-16 bg-primary/15 rounded-full flex items-center justify-center">
            <Leaf className="w-8 h-8 text-primary" />
          </div>
          <div>
            <CardTitle className="text-2xl font-semibold text-foreground">
              Create a new account
            </CardTitle>
          </div>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="name">Email</Label>
              <Input
                id="email"
                type="email"
                placeholder="happy@plant.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="name">Display name</Label>
              <Input
                id="name"
                type="text"
                placeholder="Happy Planter"
                value={displayName}
                onChange={(e) => setDisplayName(e.target.value)}
                required
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => {
                  const value = e.target.value;
                  setPassword(value);
                  setPasswordRules(getPasswordRules(value));
                }}
                required
              />

              <div className={"mt-2 space-y-1 text-sm"}>
                <PasswordRule label="Minimum 12 characters" valid={passwordRules.length}/>
                <PasswordRule label="Minimum one uppercase letter" valid={passwordRules.uppercase}/>
                <PasswordRule label="Minimum one lowercase letter" valid={passwordRules.lowercase}/>
                <PasswordRule label="Minimum one digit" valid={passwordRules.digit}/>
                <PasswordRule label="Minimum one special character" valid={passwordRules.special}/>
              </div>
            </div>

            <Button
              onClick={handleRegister}
              disabled={isLoading || !allValid}
              type="submit"
              className="w-full"
              size="lg"
            >
              Create account
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
