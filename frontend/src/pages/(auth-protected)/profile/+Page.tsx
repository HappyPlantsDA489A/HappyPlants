import { Button } from "@/components/ui/button";


export default function Page() {


  return (
    <div className="flex h-full flex-col items-center justify-center space-y-4">
      <div className="text-center">
        <h1 className="font-bold text-3xl tracking-tight">Profile</h1>
        <p className="text-muted-foreground text-lg mt-2">
        </p>
      </div>

      <div className="flex flex-col space-y-3 max-w-sx">
        <Button asChild size="lg">
            <a href="/profile/change-password">Change password</a>
        </Button>
      </div>
      <Button asChild size="lg">
        <a href="/library">Go to library</a>
      </Button>


    </div>
  );
}
