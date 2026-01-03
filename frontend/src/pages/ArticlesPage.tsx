import { useState, useMemo, useEffect } from 'react';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { articleService } from '../services/article';
import ArticleCard from '../components/articles/ArticleCard';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import styles from './articles.module.css';
import { PaginatedArticles } from "../types/article";
import {Link, useNavigate} from "react-router-dom";
import {useAuth} from "../hooks/useAuth";

function ArticlesPage() {
    const [page, setPage] = useState(0);
    const { user } = useAuth();
    const navigate = useNavigate();     // ✔ 前端跳转
    const [tempKeyword, setTempKeyword] = useState(''); // 输入框临时值
    const [searchKeyword, setSearchKeyword] = useState(''); // 实际搜索关键词

    // 防抖
    // useEffect(() => {
    //     const handler = setTimeout(() => {
    //         setSearchKeyword(tempKeyword);
    //     }, 2000);
    //     return () => clearTimeout(handler);
    // }, [tempKeyword]);

    const query = useQuery<PaginatedArticles, Error>({
        queryKey: ['articles', { page, keyword: searchKeyword }], // 使用 searchKeyword
        queryFn: () => articleService.list({ page, keyword: searchKeyword }),
        placeholderData: keepPreviousData,
    });

    const totalPages = query.data?.totalPages ?? 0;
    const pagination = useMemo(() =>
            Array.from({ length: totalPages }, (_, i) => i),
        [totalPages]
    );

    // 动态标题
    const headerTitle = searchKeyword
        ? `搜索到文章${query.data?.content?.length ?? 0} 条`
        : '全部文章';

    const noResults = searchKeyword && (query.data?.content?.length ?? 0) === 0;

    const handleSearch = () => {
        setSearchKeyword(tempKeyword);
        setPage(0);
    };

    const handleCreate = () => {
        if (user) {
            navigate('/articles/create');   // ✔ 已登录 → 去创作页面
        } else {
            navigate('/login');             // ✔ 未登录 → 去登录页面
        }
    };

    return (
        <>
            <section className={styles.hero}>
                <div>
                    <p className={styles.badge}>智能投研 · 数据驱动</p>
                    <h1>专业金融内容创作平台</h1>
                    <p>基于 React 与 Spring Boot，为你提供高效、安全的财经写作与观点发布体验。</p>

                    <div className={styles.actions}>
                        <Link to="/articles" className={styles.primaryBtn}>
                            浏览最新文章
                        </Link>
                        <button onClick={handleCreate} className={styles.secondaryBtn}>
                            发布您的见解
                        </button>
                    </div>
                </div>

                <div className={styles.heroCard}>
                    <h3>实时数据</h3>
                    <p>文章：{query.data?.totalElements ?? '--'}</p>
                    <p>互动：高质量点赞与评论</p>
                    <p>AI 工具：摘要、标签、SEO</p>
                </div>
            </section>

            <div className={styles.container}>
                <div className={styles.header}>
                    <h1>{headerTitle}</h1>
                    <div className={styles.searchBox}>
                        <input
                            type="search"
                            placeholder="搜索标题 / 标签"
                            value={tempKeyword}
                            onChange={(e) => setTempKeyword(e.target.value)}
                            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                        />
                        <button onClick={handleSearch} className={styles.searchButton}>
                            搜索
                        </button>
                    </div>
                </div>

                {query.isLoading && <Loading />}
                {query.error && (
                    <ErrorState
                        message={query.error.message}
                        retry={() => query.refetch()}
                    />
                )}

                {noResults ? (
                    <p style={{ textAlign: 'center', marginTop: '2rem', color: '#64748b' }}>
                        未搜索到相关内容
                    </p>
                ) : (
                    <div className={styles.list}>
                        {query.data?.content?.map((article) => (
                            <ArticleCard article={article} key={article.id} />
                        ))}
                    </div>
                )}

                {!noResults && totalPages > 1 && (
                    <div className={styles.pagination}>
                        {pagination.map((item) => (
                            <button
                                key={item}
                                onClick={() => setPage(item)}
                                className={item === page ? styles.active : undefined}
                            >
                                {item + 1}
                            </button>
                        ))}
                    </div>
                )}
            </div>
        </>
    );
}

export default ArticlesPage;