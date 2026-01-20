import { Link } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useState, useRef, useEffect } from 'react';
import Loading from '../../components/common/Loading';
import ErrorState from '../../components/common/ErrorState';
import { articleService } from '../../services/article';
import { formatDate } from '../../utils/date';
import styles from './article-detail.module.css';
import { marked } from 'marked';
import UserInfoCard from '../../components/user/UserInfoCard';
import { authService } from '../../services/auth';

marked.setOptions({
    breaks: true,
    gfm: true,
});

function ArticleDetailPage() {
    const { slug = '' } = useParams<{ slug: string }>();
    const queryClient = useQueryClient();

    const {
        data: articleData,
        isLoading,
        error,
        refetch,
    } = useQuery({
        queryKey: ['article', slug],
        queryFn: () => articleService.detail(slug),
        enabled: Boolean(slug),
    });

    // 提前安全提取 author username（避免在 queryFn 中访问可能 undefined 的 data）
    const authorUsername = articleData?.author?.username;

    const [showUserCard, setShowUserCard] = useState(false);

    // 只在点击后且有 username 时才查询完整作者信息
    const { data: fullAuthor } = useQuery({
        queryKey: ['public-user', authorUsername],
        queryFn: () => authService.getProfileByUsername(authorUsername!), // enabled 保证非空
        enabled: !!authorUsername && showUserCard,
    });

    const likeMutation = useMutation({
        mutationFn: (isCurrentlyLiked: boolean) =>
            isCurrentlyLiked
                ? articleService.unlike(articleData!.id)
                : articleService.like(articleData!.id),
        onMutate: async (newIsLiked) => {
            await queryClient.cancelQueries({ queryKey: ['article', slug] });
            const previousData = queryClient.getQueryData(['article', slug]);
            queryClient.setQueryData(['article', slug], (old: any) => ({
                ...old,
                likeCount: newIsLiked ? old.likeCount - 1 : old.likeCount + 1,
                liked: !newIsLiked,
            }));
            return { previousData };
        },
        onError: (err, variables, context) => {
            queryClient.setQueryData(['article', slug], context?.previousData);
        },
        onSettled: () => {
            queryClient.invalidateQueries({ queryKey: ['article', slug] });
        },
    });

    const authorRef = useRef<HTMLSpanElement>(null);
    const cardRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        const handleClickOutside = (e: MouseEvent) => {
            if (
                showUserCard &&
                authorRef.current &&
                cardRef.current &&
                !authorRef.current.contains(e.target as Node) &&
                !cardRef.current.contains(e.target as Node)
            ) {
                setShowUserCard(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, [showUserCard]);

    if (isLoading) return <Loading />;
    if (error) return <ErrorState message={error.message} retry={refetch} />;
    if (!articleData) return null;

    const handleLikeClick = () => {
        likeMutation.mutate(articleData.liked);
    };

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.container}>
                <nav className={styles.breadcrumb}>
                    <Link to="/articles" className={styles.breadcrumbLink}>
                        全部文章
                    </Link>
                    <span className={styles.breadcrumbSeparator}> / </span>
                    <span className={styles.breadcrumbCurrent}>阅读中</span>
                </nav>

                <article className={styles.article}>
                    <header className={styles.header}>
                        <h1 className={styles.title}>{articleData.title}</h1>

                        <div className={styles.metaSection}>
                            <div className={styles.authorInfo}>
                                {articleData.author ? (
                                    <>
                    <span
                        ref={authorRef}
                        className={`${styles.authorName} ${styles.clickableAuthor}`}
                        onClick={() => setShowUserCard(true)}
                    >
                      {articleData.author.username}
                    </span>
                                        <span className={styles.publishTime}>
                      {formatDate(articleData.createdAt)}
                    </span>

                                        {/* 用户卡片：使用 fullAuthor（带统计） */}
                                        {showUserCard && (
                                            <div ref={cardRef} className={styles.userCardOverlay}>
                                                {fullAuthor ? (
                                                    <UserInfoCard
                                                        username={fullAuthor.username}
                                                        bio={fullAuthor.bio || ''}
                                                        articleCount={fullAuthor.articleCount ?? 0}
                                                        likeCount={fullAuthor.likeCount ?? 0}
                                                        userId={fullAuthor.id.toString()}
                                                        onClose={() => setShowUserCard(false)}
                                                    />
                                                ) : (
                                                    <div className={styles.loadingCard}>加载中...</div>
                                                )}
                                            </div>
                                        )}
                                    </>
                                ) : (
                                    <span className={styles.authorName}>匿名用户</span>
                                )}
                            </div>

                            <div className={styles.tags}>
                                {articleData.tags.map((tag, index) => (
                                    <span key={index} className={styles.tag}>
                    #{tag}
                  </span>
                                ))}
                            </div>
                        </div>
                    </header>

                    <section
                        className={styles.content}
                        dangerouslySetInnerHTML={{
                            __html: marked.parse(articleData.content),
                        }}
                    />

                    <footer className={styles.actionBar}>
                        <button
                            onClick={handleLikeClick}
                            disabled={likeMutation.isPending}
                            className={`${styles.actionButton} ${
                                articleData.liked ? styles.liked : ''
                            }`}
                            aria-label={articleData.liked ? '取消点赞' : '点赞'}
                        >
                            <span className={styles.actionIcon}>👍</span>
                            <span className={styles.actionText}>{articleData.likeCount}</span>
                        </button>

                        <div className={styles.actionButton}>
                            <span className={styles.actionIcon}>👁️</span>
                            <span className={styles.actionText}>{articleData.viewCount}</span>
                        </div>

                        <Link
                            to={`/articles/${slug}/comment`}
                            className={styles.actionButton}
                        >
                            <span className={styles.actionIcon}>💬</span>
                            <span className={styles.actionText}>
                {articleData.commentCount}
              </span>
                        </Link>
                    </footer>
                </article>
            </div>
        </div>
    );
}

export default ArticleDetailPage;