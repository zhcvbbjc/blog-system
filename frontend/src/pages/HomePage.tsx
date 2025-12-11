import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';  // ✔ 获取登录状态
import { useQuery } from '@tanstack/react-query';
import { articleService } from '../services/api';
import ArticleCard from '../components/articles/ArticleCard';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import MarketIndexBoard from '../components/marketData/MarketIndexBoard';
import styles from './home.module.css';

function HomePage() {
    const { user } = useAuth();         // ✔ 获取当前用户
    const navigate = useNavigate();     // ✔ 前端跳转

    const { data, isLoading, error, refetch } = useQuery({
        queryKey: ['articles', { page: 0, size: 6 }],
        queryFn: () => articleService.list({ page: 0, size: 6 })
    });

    const handleCreate = () => {
        if (user) {
            navigate('/articles/create');   // ✔ 已登录 → 去创作页面
        } else {
            navigate('/login');             // ✔ 未登录 → 去登录页面
        }
    };

    return (
        <div className={styles.container}>
            {/* 全球主要指数区块 */}
            <MarketIndexBoard />



            <section className={styles.listSection}>
                <div className={styles.sectionHeader}>
                    <h2>最新文章</h2>
                    <Link to="/articles">查看全部 →</Link>
                </div>

                {isLoading && <Loading />}
                {error && <ErrorState message={error.message} retry={refetch} />}
                <div className={styles.list}>
                    {data?.content.map((article) => (
                        <ArticleCard article={article} key={article.id} />
                    ))}
                </div>
            </section>
        </div>
    );
}

export default HomePage;
