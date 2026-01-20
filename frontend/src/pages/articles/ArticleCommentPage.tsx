// src/pages/articles/ArticleCommentPage.tsx
import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import Loading from '../../components/common/Loading';
import ErrorState from '../../components/common/ErrorState';
import { articleService } from '../../services/article';
import { commentService } from '../../services/comment';
import { formatDate } from '../../utils/date';
import styles from './article-comment.module.css';

function ArticleCommentPage() {
    const { slug = '' } = useParams<{ slug: string }>();
    const [isAdding, setIsAdding] = useState(false);
    const queryClient = useQueryClient();

    const {
        data: article,
        isLoading: articleLoading,
        error: articleError,
        refetch: refetchArticle
    } = useQuery({
        queryKey: ['article', slug],
        queryFn: () => articleService.detail(slug),
        enabled: Boolean(slug)
    });

    const {
        data: comments,
        isLoading: commentsLoading,
        error: commentsError,
        refetch: refetchComments
    } = useQuery({
        queryKey: ['comments', article?.id],
        queryFn: () => commentService.listByArticle(article!.id),
        enabled: Boolean(article?.id)
    });

    const createCommentMutation = useMutation({
        mutationFn: (content: string) =>
            commentService.create(article!.id, { content }),
        onSuccess: () => {
            setIsAdding(false);
            refetchComments();
            queryClient.invalidateQueries({ queryKey: ['article', slug] });
        },
        onError: () => {
            alert('发表失败，请登录后重试');
        }
    });

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = e.target as HTMLFormElement;
        const textarea = form.elements.namedItem('content') as HTMLTextAreaElement;
        const content = textarea.value.trim();

        if (!content || !article?.id) return;

        createCommentMutation.mutate(content);
        textarea.value = '';
    };

    const isLoading = articleLoading || commentsLoading;
    const error = articleError || commentsError;

    if (isLoading) return <Loading />;
    if (error) return <ErrorState message="加载失败" retry={() => {
        refetchArticle();
        if (article?.id) refetchComments();
    }} />;
    if (!article) return null;

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.container}>
                {/* 返回顶部 */}
                <div className={styles.topBar}>
                    <Link to={`/articles/${slug}`} className={styles.backLink}>
                        ← 返回《{article.title}》
                    </Link>
                </div>

                {/* 标题区 */}
                <header className={styles.header}>
                    <h1 className={styles.title}>
                        评论 <span className={styles.commentCount}>({comments?.length || 0})</span>
                    </h1>
                </header>

                {/* 发表评论卡片 */}
                <section className={styles.addCommentCard}>
                    {!isAdding ? (
                        <button
                            onClick={() => setIsAdding(true)}
                            className={styles.addButton}
                        >
                            💬 发表你的看法
                        </button>
                    ) : (
                        <form onSubmit={handleSubmit} className={styles.commentForm}>
              <textarea
                  name="content"
                  placeholder="写下你的想法...（支持 Markdown）"
                  rows={4}
                  required
                  className={styles.textarea}
              />
                            <div className={styles.formActions}>
                                <button
                                    type="button"
                                    onClick={() => setIsAdding(false)}
                                    className={styles.cancelButton}
                                >
                                    取消
                                </button>
                                <button
                                    type="submit"
                                    disabled={createCommentMutation.isPending}
                                    className={styles.submitButton}
                                >
                                    {createCommentMutation.isPending ? '发布中...' : '发布评论'}
                                </button>
                            </div>
                        </form>
                    )}
                </section>

                {/* 评论列表 */}
                <section className={styles.commentsSection}>
                    {comments && comments.length > 0 ? (
                        <ul className={styles.commentList}>
                            {comments.map((comment) => (
                                <li key={comment.id} className={styles.commentItem}>
                                    <div className={styles.commentAuthor}>
                                        <span className={styles.authorName}>{comment.author.username}</span>
                                        <span className={styles.commentTime}>{formatDate(comment.createdAt)}</span>
                                    </div>
                                    <p className={styles.commentContent}>{comment.content}</p>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <div className={styles.emptyState}>
                            <div className={styles.emptyIcon}>💬</div>
                            <p className={styles.emptyText}>还没有人评论，快来成为第一个吧！</p>
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}

export default ArticleCommentPage;