import { Button } from "@/components/ui/button";
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyTitle,
} from "@/components/ui/empty";
import { ArrowLeft, Leaf } from "lucide-react";

export function PlantNotFound() {
  return (
    <Empty>
      <EmptyHeader>
        <div className="mx-auto w-12 h-12 bg-yellow-500/30 rounded-full flex items-center justify-center mb-2">
          <Leaf className="w-6 h-6 text-yellow-500" />
        </div>
        <EmptyTitle className="text-lg">Plant not found</EmptyTitle>
        <EmptyDescription className="text-md">
          This plant does not exist, make sure the URL is correct.
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent className="flex-row justify-center gap-2">
        <Button variant="secondary" asChild className="gap-2">
          <a href="/library">
            <ArrowLeft className="h-4 w-4" />
            Back to library
          </a>
        </Button>
      </EmptyContent>
    </Empty>
  );
}
