import { useState, type SyntheticEvent } from 'react';
import { toast } from 'sonner';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function ChangePasswordPage() {
    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isSuccess, setIsSuccess] = useState(false);

    const handleChangePassword = async (e: SyntheticEvent) => {
        e.preventDefault();

        if (newPassword !== confirmPassword) {
            toast.error('New passwords do not match');
            return;
        }

        try {
            const response = await fetch('/api/user/change-password', {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ currentPassword, newPassword }),
            });

            if (response.ok) {
                toast.success('Password changed successfully');
                setIsSuccess(true);
                setCurrentPassword('');
                setNewPassword('');
                setConfirmPassword('');


            } else {
                const errorData = await response.json();
                toast.error(errorData.message || 'Failed to change password');
            }
        } catch (err) {
            console.error("Change password error:", err);
            toast.error('An error occurred, please try again');
        }
    };

    return (
        <div className="flex h-full flex-col items-center justify-center space-y-6">
            {isSuccess ? (
                //(Visas när isSuccess är true)
                <div className="text-center space-y-6 animate-in fade-in zoom-in duration-300">
                    <div className="space-y-2">
                        <h1 className="font-bold text-3xl tracking-tight text-green-800">Password Updated!</h1>
                        <p className="text-muted-foreground text-lg">
                            Your security settings have been successfully updated.
                        </p>
                    </div>

                    <Button asChild size="lg" className="w-full">
                        <a href="/profile">Back to profile</a>
                    </Button>
                </div>
            ) : (
                /* --- FORMULÄRVY (Visas som standard) --- */
                <>
                    <div className="text-center">
                        <h1 className="font-bold text-3xl tracking-tight">Change Password</h1>
                        <p className="text-muted-foreground mt-2">Enter your details to update your security.</p>
                    </div>

                    <form onSubmit={handleChangePassword} className="w-full max-w-sm space-y-4">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Current Password</label>
                            <Input type="password" value={currentPassword} onChange={(e) => setCurrentPassword(e.target.value)} required />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">New Password</label>
                            <Input type="password" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} required />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Confirm New Password</label>
                            <Input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} required />
                        </div>

                        <div className="flex flex-col space-y-3 pt-4">
                            <Button type="submit" className="w-full">Update Password</Button>
                            <Button asChild size="lg" variant="ghost">
                                <a href="/profile">Cancel</a>
                            </Button>
                        </div>
                    </form>
                </>
            )}
        </div>
    );
}