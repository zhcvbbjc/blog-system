// src/types/comment.ts
import { ArticleAuthor } from './article';

export interface Comment {
    id: number;
    content: string;
    author: ArticleAuthor;
    articleId: number;
    createdAt: string;
    updatedAt: string;
}