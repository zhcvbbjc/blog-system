import { useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { articleService, authService } from '../services/api';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import styles from './dashboard.module.css';

function DashboardPage() {
  const { data: articles, isLoading, error, refetch } = useQuery({
    queryKey: ['dashboard', 'articles'],
    queryFn: () => articleService.list({ page: 0, size: 5 })
  });

  const { data: profile } = useQuery({
    queryKey: ['profile'],
    queryFn: () => authService.profile()
  });

  const stats = useMemo(() => {
    const total = articles?.totalElements ?? 0;
    const likes = articles?.content.reduce((sum, item) => sum + item.likeCount, 0) ?? 0;
    const comments =
      articles?.content.reduce((sum, item) => sum + item.commentCount, 0) ?? 0;
    return { total, likes, comments };
  }, [articles]);

  return (
    <div className={styles.container}>
      <section className={styles.hero}>
        <div>
          <p className={styles.subtitle}>欢迎回来</p>
          <h1>{profile?.username ?? '创作者'}</h1>
          <p>使用 AI 工具提升创作效率，实时掌握内容表现。</p>
        </div>
        <div className={styles.stats}>
          <div>
            <span>文章</span>
            <strong>{stats.total}</strong>
          </div>
          <div>
            <span>点赞</span>
            <strong>{stats.likes}</strong>
          </div>
          <div>
            <span>评论</span>
            <strong>{stats.comments}</strong>
          </div>
        </div>
      </section>

      <section className={styles.section}>
        <div className={styles.sectionHeader}>
          <h2>最近更新</h2>
        </div>
        {isLoading && <Loading />}
        {error && <ErrorState message={error.message} retry={refetch} />}
        <ul className={styles.timeline}>
          {articles?.content.map((article) => (
            <li key={article.id}>
              <div>
                <p>{article.title}</p>
                <span>{article.summary}</span>
              </div>
              <span>👍 {article.likeCount}</span>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}

export default DashboardPage;

