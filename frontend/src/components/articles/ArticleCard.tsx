import { Link } from 'react-router-dom';
import type { Article } from '../../types/article';
import { formatDate } from '../../utils/date';
import styles from './article-card.module.css';

interface ArticleCardProps {
  article: Article;
}

function ArticleCard({ article }: ArticleCardProps) {
  return (
    <article className={styles.card}>
      <div className={styles.header}>
        <div>
          <p className={styles.author}>{article.authorName}</p>
          <p className={styles.date}>{formatDate(article.createdAt)}</p>
        </div>
        <p className={styles.meta}>
          👍 {article.likeCount} · 💬 {article.commentCount}
        </p>
      </div>
      <Link to={`/articles/${article.slug}`} className={styles.title}>
        {article.title}
      </Link>
      <p className={styles.summary}>{article.summary}</p>
      <div className={styles.tags}>
        {article.tags.map((tag) => (
          <span key={tag}>#{tag}</span>
        ))}
      </div>
    </article>
  );
}

export default ArticleCard;

