import axios from 'axios';
import type { Article, PaginatedArticles } from '../types/article';
import type { AuthResponse, RegisterResponse, UserProfile } from '../types/user';

const API_BASE = import.meta.env.VITE_API_BASE ?? '/api';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: false
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const message = error.response?.data?.message ?? '请求失败，请稍后重试';
    return Promise.reject(new Error(message));
  }
);

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
      const response = await api.get<{ data: PaginatedArticles }>('/articles/search', {
        params: { keyword, ...rest }
      });
      return response.data.data;
    }

    const response = await api.get<{ data: PaginatedArticles }>('/articles', {
      params: rest
    });
    return response.data.data;
  },
  async detail(slug: string) {
    const response = await api.get<{ data: Article }>(`/articles/slug/${slug}`);
    return response.data.data;
  },
  async like(articleId: number) {
    await api.post(`/likes/article/${articleId}`);
  },
  async unlike(articleId: number) {
    await api.delete(`/likes/article/${articleId}`);
  },
  async create(payload: { title: string; content: string; tags: string[] }) {
    const response = await api.post<{ data: Article }>('/articles', payload);
    return response.data.data;
  }
};

export const authService = {
  async login(payload: { username: string; password: string }) {
    const response = await api.post<{ data: AuthResponse }>('/auth/login', payload);
    return response.data.data;
  },
  async register(payload: { username: string; email: string; password: string }) {
    const response = await api.post<{ data: RegisterResponse }>('/auth/register', payload);
    return response.data.data;
  },
  async profile() {
    const response = await api.get<{ data: UserProfile }>('/auth/me');
    return response.data.data;
  }
};

