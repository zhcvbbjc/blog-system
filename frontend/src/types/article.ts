export interface Article {
  id: number;
  title: string;
  summary: string;
  content: string;
  slug: string;
  tags: string[];
  authorName: string;
  coverImage?: string;
  createdAt: string;
  updatedAt: string;
  likeCount: number;
  viewCount: number;
  commentCount: number;
}

export interface PaginatedArticles {
  content: Article[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

