import { Button } from "@/components/ui/button";
import { Droplet } from "lucide-react";

export default function MarkAsWateredButton() {
  return (
    <Button
      disabled
      variant="outline"
      className="bg-primary/10 hover:bg-primary/10 border-primary/50"
    >
      <Droplet /> Mark as watered
    </Button>
  );
}
