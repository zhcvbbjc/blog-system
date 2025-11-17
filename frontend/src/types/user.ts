export interface UserProfile {
    id: number;
    username: string;
    email: string;
    avatarUrl?: string | null;
    bio?: string | null;
    role?: string; // 后端没有 role，所以可选
    createdAt?: string | null;
    updatedAt?: string | null;

    articleCount?: number | null;
    likeCount?: number | null;
    commentCount?: number | null;
}


export interface AuthResponse {
    user: UserProfile;
    token: string;
}

export interface RegisterResponse extends UserProfile {}

