import request from "./request";
import type { Article, PaginatedArticles } from "../types/article";

export interface ArticleQuery {
    page?: number;
    size?: number;
    tag?: string;
    keyword?: string;
}

export const articleService = {
    async list(params: ArticleQuery = {}) {
        const { keyword, ...rest } = params;

        if (keyword && keyword.trim().length > 0) {
            const res = await request.get<{ data: PaginatedArticles }>(
                "/articles/search",
                { params: { keyword, ...rest } }
            );
            return res.data.data;
        }

        const res = await request.get<{ data: PaginatedArticles }>("/articles", {
            params: rest,
        });

        return res.data.data;
    },

    async detail(slug: string) {
        const res = await request.get<{ data: Article }>(
            `/articles/slug/${slug}`
        );
        return res.data.data;
    },

    async like(articleId: number) {
        await request.post(`/likes/article/${articleId}`);
    },

    async unlike(articleId: number) {
        await request.delete(`/likes/article/${articleId}`);
    },

    async create(payload: { title: string; content: string; tags: string[] }) {
        const res = await request.post<{ data: Article }>("/articles", payload);
        return res.data.data;
    },
};
