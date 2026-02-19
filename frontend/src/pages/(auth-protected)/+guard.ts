import { redirect } from "vike/abort";
import type { PageContextServer } from "vike/types";
import { API_BASE_URL } from "@/config";

export async function guard(pageContext: PageContextServer) {
  const cookieHeader = pageContext.headers["cookie"];

  const sessionCookie = cookieHeader?.match(/HAPPY_COOKIE=([^;]+)/)?.[1];

  if (!sessionCookie) {
    throw redirect("/auth/login");
  }

  try {
    const response = await fetch(`${API_BASE_URL}/auth/check-auth`, {
      headers: { Cookie: `HAPPY_COOKIE=${sessionCookie}` },
    });

    if (!response.ok) {
      throw redirect("/auth/login");
    }
  } catch (err) {
    throw redirect("/auth/login");
  }
}
