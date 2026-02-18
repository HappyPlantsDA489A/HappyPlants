import { Button } from "@/components/ui/button";

export default function Page() {
  return (
    <div className="flex h-full flex-col items-center justify-center space-y-4">
      <div className="text-center">
        <h1 className="font-bold text-3xl tracking-tight">Library</h1>
        <p className="text-muted-foreground text-lg mt-2">
          Your plants will be shown here.
        </p>
      </div>

      <Button asChild size="lg">
        <a href="/profile">Go to profile</a>
      </Button>
    </div>
  );
}
