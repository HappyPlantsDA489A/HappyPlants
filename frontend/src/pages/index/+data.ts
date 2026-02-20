export { data };
export type Data = Awaited<ReturnType<typeof data>>;
import type { PageContextServer } from "vike/types";
import { API_BASE_URL } from "@/config";

const data = async (pageContext: PageContextServer) => {
  const cookieHeader = pageContext.headers["cookie"];

  const sessionCookie = cookieHeader?.match(/HAPPY_COOKIE=([^;]+)/)?.[1];

  try {
    const response = await fetch(`${API_BASE_URL}/auth/check-auth`, {
      headers: { Cookie: `HAPPY_COOKIE=${sessionCookie}` },
    });

    if (response.ok) {
      return {
        isAuthenticated: true,
      };
    } else {
      throw new Error();
    }
  } catch (err) {
    return {
      isAuthenticated: false,
    };
  }
};
