import request from "./request";
import type { AuthResponse, RegisterResponse, UserProfile } from "../types/user";

export const authService = {
    async login(payload: { username: string; password: string }) {
        const res = await request.post<{ data: AuthResponse }>(
            "/auth/login",
            payload
        );
        return res.data.data;
    },

    async register(payload: {
        username: string;
        email: string;
        password: string;
    }) {
        const res = await request.post<{ data: RegisterResponse }>(
            "/auth/register",
            payload
        );
        return res.data.data;
    },

    async profile() {
        const res = await request.get<{ data: UserProfile }>("/auth/me");
        return res.data.data;
    },

    // 新增：根据 username 获取用户公开资料
    async getProfileByUsername(username: string) {
        const res = await request.get<{ data: UserProfile }>(`/auth/search-user/${username}`);
        return res.data.data;
    },
};
