import { Button } from "@/components/ui/button";

export default function Page() {
  return (
    <div>
      <h1 className="font-bold text-xl">Library</h1>
      <h1>You can only view this page if logged in</h1>
      <Button asChild>
        <a href="/profile">Go to profile</a>
      </Button>
    </div>
  );
}
