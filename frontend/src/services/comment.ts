// src/services/comment.ts

import request from './request';
import type { Comment } from '../types/comment';

export interface CreateCommentPayload {
    content: string; // 注意：后端用 @RequestBody CommentRequest，其中只有 content
}

export const commentService = {
    /**
     * 获取文章评论列表
     */
    async listByArticle(articleId: number): Promise<Comment[]> {
        const res = await request.get<{ data: Comment[] }>(
            `/comments/article/${articleId}`
        );
        return res.data.data;
    },

    /**
     * 发表评论
     */
    async create(articleId: number, payload: CreateCommentPayload): Promise<Comment> {
        const res = await request.post<{ data: Comment }>(
            `/comments/article/${articleId}`,
            payload // { content: "..." }
        );
        return res.data.data;
    },
};