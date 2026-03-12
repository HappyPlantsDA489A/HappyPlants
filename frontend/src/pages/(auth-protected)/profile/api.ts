import { API_BASE_URL } from "@/config";
import type { ApiProblem, ChangePasswordPayload, UserInfo } from "./types";

async function readErrorMessage(response: Response): Promise<string> {
  try {
    const json = (await response.json()) as ApiProblem;
    return (
      json.detail ||
      json.message ||
      json.title ||
      "Something went wrong. Please try again."
    );
  } catch {
    return "Something went wrong. Please try again.";
  }
}

export async function getUserInfo(): Promise<UserInfo> {
  const response = await fetch(`${API_BASE_URL}/user/user-info`, {
    method: "GET",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response));
  }

  return (await response.json()) as UserInfo;
}

export async function changePassword(payload: ChangePasswordPayload): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/user/change-password`, {
    method: "PATCH",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response));
  }
}

export async function deleteAccount(): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/user`, {
    method: "DELETE",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error(await readErrorMessage(response));
  }
}
