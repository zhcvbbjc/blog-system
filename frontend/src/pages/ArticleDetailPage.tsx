import { Link } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import { articleService } from '../services/api';
import { formatDate } from '../utils/date';
import styles from './article-detail.module.css';
import { marked } from 'marked';

function ArticleDetailPage() {
    const { slug = '' } = useParams<{ slug: string }>();

    const { data, isLoading, error, refetch } = useQuery({
        queryKey: ['article', slug],
        queryFn: () => articleService.detail(slug),
        enabled: Boolean(slug)
    });

    if (isLoading) return <Loading />;
    if (error) return <ErrorState message={error.message} retry={refetch} />;
    if (!data) return null;

    return (
        <div className={styles.container}>
            {/* 返回链接 */}
            <Link to="/articles" className={styles.backLink}>
                🔙 返回全部文章
            </Link>

            <article className={styles.article}>
                <header className={styles.header}>
                    <div>
                        <p className={styles.tagline}>#{data.tags.join(' · ')}</p>
                        <h1>{data.title}</h1>
                        <p className={styles.meta}>
                            {data.author?.username} · {formatDate(data.createdAt)}
                        </p>
                    </div>
                    <div className={styles.stats}>
                        <span>👍 {data.likeCount}</span>
                        <span>👁️ {data.viewCount}</span>
                        <span>💬 {data.commentCount}</span>
                    </div>
                </header>
                <section className={styles.content} dangerouslySetInnerHTML={{ __html: marked(data.content) }} />
            </article>
        </div>
    );
}

export default ArticleDetailPage;