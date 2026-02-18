import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Leaf } from "lucide-react";
import { navigate, reload } from "vike/client/router";
import { API_BASE_URL } from "@/config";
import { toast } from "sonner";

export default function Page() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  async function handleLogin() {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_BASE_URL}/auth/log-in`, {
        method: "POST",
        credentials: "include",
        body: JSON.stringify({ email: email, password: password }),
        headers: {
          "Content-Type": "application/json",
        },
      });

      const json = await response.json();

      if (response.ok) {
        await reload();
        navigate("/library");
      } else if (response.status == 401) {
        throw new Error(json.detail);
      } else {
        const json = await response.json();
        throw new Error(json.message);
      }
    } catch (error: any) {
      toast.error(error.message);
      console.log(error);
    }
    setIsLoading(false);
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <Card className="w-full max-w-md border-0 shadow-2xl">
        <CardHeader className="text-center space-y-3">
          <div className="mx-auto w-16 h-16 bg-primary/15 rounded-full flex items-center justify-center">
            <Leaf className="w-8 h-8 text-primary" />
          </div>
          <div>
            <CardTitle className="text-2xl font-semibold text-foreground">
              Happy Plants
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
              <Label htmlFor="password">Password</Label>
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <Button
              onClick={handleLogin}
              disabled={isLoading}
              type="submit"
              className="w-full"
              size="lg"
            >
              Log in
            </Button>
            <Button asChild variant="secondary" className="w-full" size="lg">
              <a href="/auth/register">Create account</a>
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
