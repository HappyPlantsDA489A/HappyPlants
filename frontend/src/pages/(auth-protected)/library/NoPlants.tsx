import { Button } from "@/components/ui/button";
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyTitle,
} from "@/components/ui/empty";
import { Leaf } from "lucide-react";

export function NoPlants() {
  return (
    <Empty>
      <EmptyHeader>
        <div className="mx-auto w-12 h-12 bg-primary/15 rounded-full flex items-center justify-center mb-2">
          <Leaf className="w-6 h-6 text-primary" />
        </div>
        <EmptyTitle className="text-lg">No Plants Yet</EmptyTitle>
        <EmptyDescription className="text-md">
          You haven't added any plants to your library.
        </EmptyDescription>
      </EmptyHeader>
      <EmptyContent className="flex-row justify-center gap-2">
        <Button disabled>Add your first plant</Button>
      </EmptyContent>
    </Empty>
  );
}
