import { Button } from "@/components/ui/button";

export default function Page() {
    const handleDeleteAccount = async () => {
        const confirmed = window.confirm("Are you sure you? Your account and data will be erased permanently");

        if (confirmed) {
            try {
                const response = await fetch("/api/auth/delete-account", {
                    method: "DELETE",
                });

                if (response.ok) {
                    window.location.href = "/";
                } else {
                    alert("Could not delete account");
                }
            } catch (error) {
                console.log("Error:" + error);
            }
        }

    }
        return (
            <div className="flex h-full flex-col items-center justify-center space-y-4">
                <div className="text-center">
                    <h1 className="font-bold text-3xl tracking-tight">Profile</h1>
                    <p className="text-muted-foreground text-lg mt-2">
                        You will view your profile info here.
                    </p>
                </div>

                <Button asChild size="lg">
                    <a href="/library">Go to library</a>
                </Button>

                <Button onClick={handleDeleteAccount} variant="destructive" size="lg">
                    Delete account
                </Button>

            </div>
        );


}