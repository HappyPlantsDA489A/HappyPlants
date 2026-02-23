"use client";

import { useState, useCallback } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { ArrowLeft, Search } from "lucide-react";
import type { PerenualSearchPlantDTO } from "@/types/PerenualSearchPlantDTO";
import {
  SearchResultsEmpty,
  SearchResultsInitial,
  SearchResultsList,
  SearchResultsSkeleton,
} from "./SearchResults";

type SearchState =
  | { status: "idle" }
  | { status: "loading" }
  | { status: "error"; message: string }
  | { status: "success"; results: PerenualSearchPlantDTO[]; query: string };

export default function SearchPage() {
  const [query, setQuery] = useState("");
  const [searchState, setSearchState] = useState<SearchState>({
    status: "idle",
  });

  const handleSearch = useCallback(async () => {
    const trimmed = query.trim();
    if (trimmed.length === 0) return;

    setSearchState({ status: "loading" });

    try {
      const res = await fetch(
        `/api/plants/search?name=${encodeURIComponent(trimmed)}`,
      );

      if (!res.ok) {
        setSearchState({
          status: "error",
          message: "Failed to search. Please try again.",
        });
        return;
      }

      const data: PerenualSearchPlantDTO[] = await res.json();
      setSearchState({ status: "success", results: data, query: trimmed });
    } catch {
      setSearchState({
        status: "error",
        message: "Something went wrong. Please try again.",
      });
    }
  }, [query]);

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <div className="min-h-screen">
      <main className="max-w-3xl mx-auto px-4 py-12">
        <div className="text-center mb-10">
          <Button asChild variant="ghost" className="gap-2 mb-3" size="lg">
            <a href="/library">
              <ArrowLeft className="h-4 w-4" />
              Back to library
            </a>
          </Button>
          <h2 className="text-3xl font-bold text-foreground text-balance">
            Add a new plant to your library
          </h2>
          <p className="text-muted-foreground mt-2 text-lg">
            Search through thousands of plant species
          </p>
        </div>

        <div className="flex gap-3 mb-10">
          <Input
            type="text"
            placeholder="Search by plant name..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={handleKeyDown}
            className="h-14 text-lg px-5 bg-card border-border placeholder:text-muted-foreground/60 focus-visible:ring-primary"
          />
          <Button
            onClick={handleSearch}
            disabled={
              query.trim().length === 0 || searchState.status === "loading"
            }
            className="h-14 px-8 text-base gap-2"
            size="lg"
          >
            <Search className="w-5 h-5" />
            <span className="hidden sm:inline">Search</span>
          </Button>
        </div>

        {searchState.status === "idle" && <SearchResultsInitial />}

        {searchState.status === "loading" && <SearchResultsSkeleton />}

        {searchState.status === "error" && (
          <div className="text-center py-12">
            <p className="text-destructive font-medium">
              {searchState.message}
            </p>
          </div>
        )}

        {searchState.status === "success" &&
          (searchState.results.length === 0 ? (
            <SearchResultsEmpty query={searchState.query} />
          ) : (
            <SearchResultsList plants={searchState.results} />
          ))}
      </main>
    </div>
  );
}
