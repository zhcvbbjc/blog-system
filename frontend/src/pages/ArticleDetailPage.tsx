import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { articleService } from '../services/api';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import { formatDate } from '../utils/date';
import styles from './article-detail.module.css';

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
    <article className={styles.article}>
      <header className={styles.header}>
        <div>
          <p className={styles.tagline}>#{data.tags.join(' · ')}</p>
          <h1>{data.title}</h1>
          <p className={styles.meta}>
            {data.authorName} · {formatDate(data.createdAt)}
          </p>
        </div>
        <div className={styles.stats}>
          <span>👍 {data.likeCount}</span>
          <span>👁️ {data.viewCount}</span>
          <span>💬 {data.commentCount}</span>
        </div>
      </header>
      <section className={styles.content} dangerouslySetInnerHTML={{ __html: data.content }} />
    </article>
  );
}

export default ArticleDetailPage;

