import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';  // ✔ 获取登录状态
import { useQuery } from '@tanstack/react-query';
import { articleService } from '../services/api';
import ArticleCard from '../components/articles/ArticleCard';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
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
            <section className={styles.hero}>
                <div>
                    <p className={styles.badge}>AI 驱动 · 高效写作</p>
                    <h1>智能博客系统</h1>
                    <p>使用 React 与 Spring Boot，享受现代化的创作体验。</p>

                    <div className={styles.actions}>
                        <Link to="/articles" className={styles.primaryBtn}>
                            浏览最新文章
                        </Link>

                        {/* !!! 修改了这里 !!! */}
                        <button onClick={handleCreate} className={styles.secondaryBtn}>
                            立即创作
                        </button>
                    </div>
                </div>

                <div className={styles.heroCard}>
                    <h3>实时数据</h3>
                    <p>文章：{data?.totalElements ?? '--'}</p>
                    <p>互动：高质量点赞与评论</p>
                    <p>AI 工具：摘要、标签、SEO</p>
                </div>
            </section>

            <section className={styles.listSection}>
                <div className={styles.sectionHeader}>
                    <h2>最新文章</h2>
                    <Link to="/articles">查看全部 →</Link>
                </div>
                {/*/!* 添加调试信息 *!/*/}
                {/*<div style={{ background: '#f5f5f5', padding: '10px', margin: '10px 0' }}>*/}
                {/*    <p>isLoading: {isLoading ? 'true' : 'false'}</p>*/}
                {/*    <p>error: {error ? error.message : 'null'}</p>*/}
                {/*    <p>数据条数: {data?.content?.length || 0}</p>*/}
                {/*    <p>完整数据: {JSON.stringify(data, null, 2)}</p>*/}
                {/*</div>*/}

                {/*{isLoading && <Loading />}*/}
                {/*{error && <ErrorState message={error.message} retry={refetch} />}*/}
                {/*<div className={styles.list}>*/}
                {/*    {data?.content?.map((article) => (*/}
                {/*        <ArticleCard article={article} key={article.id} />*/}
                {/*    ))}*/}
                {/*</div>*/}
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
