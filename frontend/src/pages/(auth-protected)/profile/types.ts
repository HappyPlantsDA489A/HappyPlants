export interface UserInfo {
  email: string;
  displayName: string;
}

export interface ChangePasswordPayload {
  currentPassword: string;
  newPassword: string;
}

export interface ApiProblem {
  detail?: string;
  message?: string;
  title?: string;
}
