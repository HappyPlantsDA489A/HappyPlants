import {Button} from "@/components/ui/button";
import {toast} from "sonner";
import {useEffect, useState} from "react";


export default function Page() {

    const [user, setUser] = useState<{email: string} | null>(null);

    useEffect(() => {
        fetch("/api/user/user-info")
        .then(res => res.json())
        .then(data => setUser(data))
        .catch(err => console.log("Could not fetch user info", err));
    }, [])

    const handleDeleteAccount = async () => {
        const confirmed = window.confirm("Are you sure you? Your account and data will be erased permanently");

        if (confirmed) {
            try {
                const response = await fetch("/api/user", {
                    method: "DELETE",
                });

                if (response.ok) {
                    window.location.href = "/";
                } else {
                    toast.error("Could not delete account, Please try again later.");
                }
            } catch (error) {
                console.log("Error:" + error);
                toast.error("A network error occurred. Please try again later.");
            }
        }

    }



  return (
    <div className="flex h-full flex-col items-center justify-center space-y-4">
      <div className="text-center">
        <h1 className="font-bold text-3xl tracking-tight">Profile</h1>
        <p className="text-muted-foreground text-lg mt-2">
        {user ? user.email: "loading..."}
        </p>
      </div>

      <div className="flex flex-col space-y-3 max-w-xs w-full" >
        <Button asChild size="lg">
            <a href="/profile/change-password">Change password</a>
        </Button>
      
      <Button asChild size="lg" className="w-full">
        <a href="/library">Go to library</a>
      </Button>

	  <Button onClick={handleDeleteAccount} variant="destructive" size="lg" className="w-full">
	   Delete Account
	  </Button>
	</div>   
   </div>
  );
}
