import { Leaf } from "lucide-react";
import { usePageContext } from "vike-react/usePageContext";
export { Page };

function Page() {
  const pageContext = usePageContext();

  return (
    <div className="flex flex-col h-screen w-screen items-center justify-center gap-4">
      {pageContext.is404 ? (
        <div className="w-16 h-16 bg-yellow-500/30 rounded-full flex items-center justify-center shadow-lg">
          <Leaf className="w-8 h-8 text-yellow-500" />
        </div>
      ) : (
        <div className="w-16 h-16 bg-destructive/20 rounded-full flex items-center justify-center shadow-lg">
          <Leaf className="w-8 h-8 text-destructive/70" />
        </div>
      )}

      <div className="text-center flex flex-col gap-1">
        <h1 className="text-2xl font-bold text-gray-900">
          {pageContext.is404 ? "Page not found" : "Page could not be loaded"}
        </h1>
        <p className="text-gray-500 max-w-sm mx-auto">
          {pageContext.is404
            ? "This page does not exist, check and make sure that the URL is correct."
            : "Try to refresh. If that doesn't work, there's a risk our backend is offline or not operating correctly."}
        </p>
      </div>
    </div>
  );
}
