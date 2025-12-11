export interface ArticleAuthor {
    id: number;
    username: string;
    email: string;
    avatarUrl: string | null;
    bio: string | null;
    createdAt: string;
    updatedAt: string;
}

export interface Article {
    id: number;
    title: string;
    content: string;
    summary: string | null;
    slug: string;
    status: string;
    tags: string[];
    author: ArticleAuthor;   // ← ★ 加上这个
    viewCount: number;
    likeCount: number;
    commentCount: number;
    aiSummary: string | null;
    aiTags: string[];
    createdAt: string;
    updatedAt: string;
    publishedAt: string | null;
    liked: boolean;
}

export interface PaginatedArticles {
  content: Article[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

