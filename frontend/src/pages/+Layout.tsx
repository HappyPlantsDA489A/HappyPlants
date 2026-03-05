export { Layout };

import React from "react";
import "@/css/shadcn.css";
import { Toaster } from "@/components/ui/sonner";


function Layout({ children }: { children: React.ReactNode }) {
  return (
    <>
      {children}
      <Toaster
        position="top-center"
        richColors
        theme="light"
        toastOptions={{
          style: {
            padding: 18,
            fontSize: 16,
          },
        }}
      />
    </>
  );
}
