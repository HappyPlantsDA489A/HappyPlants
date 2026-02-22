import { ArrowRight, Droplets, Search, Library, BookOpen } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useData } from "vike-react/useData";
import type { Data } from "./+data";

const features = [
  {
    icon: Library,
    title: "Plant Library",
    description:
      "Build your personal collection by adding every plant in your home.",
  },
  {
    icon: Droplets,
    title: "Watering History",
    description:
      "Mark plants as watered and keep a log of their hydration history.",
  },
  {
    icon: BookOpen,
    title: "Care Guides",
    description:
      "Access essential tips and instructions to keep your plants healthy.",
  },
];

export default function Home() {
  const data = useData<Data>();

  return (
    <main className="min-h-screen">
      <section className="flex flex-col items-center justify-center px-6 pt-48 pb-8 text-center gap-7">
        <div>
          <h1 className="max-w-xl text-5xl leading-tight text-foreground text-balance md:text-6xl">
            Happy Plants
          </h1>
        </div>
        <p className="max-w-md text-lg leading-relaxed text-muted-foreground text-pretty">
          Simple management for all your plants.
        </p>
        <div className="flex flex-row gap-4">
          {data.isAuthenticated ? (
            <Button asChild className="gap-2" size="lg">
              <a href="/library">
                View your library <ArrowRight className="h-4 w-4" />
              </a>
            </Button>
          ) : (
            <Button asChild className="gap-2" size="lg">
              <a href="/auth/login">
                Get started <ArrowRight className="h-4 w-4" />
              </a>
            </Button>
          )}

          <Button
            asChild
            variant="outline"
            className="bg-primary/10 hover:bg-primary/10 border-primary/50 gap-2"
            size="lg"
          >
            <a href="/search" rel="external">
              Search available plants <Search className="h-4 w-4" />
            </a>
          </Button>
        </div>
      </section>

      <section className="mx-auto max-w-3xl px-6 py-20">
        <div className="grid gap-12 md:grid-cols-3">
          {features.map((feature) => (
            <div
              key={feature.title}
              className="flex flex-col items-start gap-3 rounded-xl border border-primary/40 bg-primary/3 p-6"
            >
              <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10">
                <feature.icon className="h-5 w-5 text-primary" />
              </div>
              <h3 className="text-lg text-card-foreground">{feature.title}</h3>
              <p className="text-sm leading-relaxed text-muted-foreground">
                {feature.description}
              </p>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}
