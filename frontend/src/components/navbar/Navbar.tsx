import { Button } from "@/components/ui/button";
import LogoutButton from "./LogoutButton";

export function Navbar() {
  return (
    <nav className="flex items-center bg-muted justify-between px-6 py-4 border-b border-border bg-background text-foreground">
      <span className="text-lg font-semibold font-sans">Happy Plants</span>
      <div className="flex flex-row gap-3">
        <Button size="lg" variant="outline" asChild>
          <a href="/profile">Profile</a>
        </Button>
        <LogoutButton />


      </div>
    </nav>
  );
}
