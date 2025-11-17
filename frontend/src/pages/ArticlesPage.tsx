import { useMemo, useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { articleService } from '../services/api';
import ArticleCard from '../components/articles/ArticleCard';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import styles from './articles.module.css';

function ArticlesPage() {
  const [page, setPage] = useState(0);
  const [keyword, setKeyword] = useState('');

  const query = useQuery({
    queryKey: ['articles', { page, keyword }],
    queryFn: () => articleService.list({ page, keyword })
  });

  const totalPages = query.data?.totalPages ?? 0;

  const pagination = useMemo(() => {
    return Array.from({ length: totalPages }, (_, index) => index);
  }, [totalPages]);

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>全部文章</h1>
        <input
          type="search"
          placeholder="搜索标题 / 标签"
          value={keyword}
          onChange={(event) => {
            setKeyword(event.target.value);
            setPage(0);
          }}
        />
      </div>

      {query.isLoading && <Loading />}
      {query.error && (
        <ErrorState message={query.error.message} retry={() => query.refetch()} />
      )}

      <div className={styles.list}>
        {query.data?.content.map((article) => (
          <ArticleCard article={article} key={article.id} />
        ))}
      </div>

      {totalPages > 1 && (
        <div className={styles.pagination}>
          {pagination.map((item) => (
            <button
              key={item}
              type="button"
              onClick={() => setPage(item)}
              className={item === page ? styles.active : undefined}
            >
              {item + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

export default ArticlesPage;

