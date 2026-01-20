// src/pages/Tools/NewsPage.tsx
import React, { useEffect, useState } from "react";
import styles from "./news-page.module.css";

interface NewsItem {
    title: string;
    pubDate: string;
}

const PAGE_SIZE = 20;

const NewsPage: React.FC = () => {
    const [news, setNews] = useState<NewsItem[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(1);

    useEffect(() => {
        async function fetchNews() {
            try {
                const url =
                    "https://newsdata.io/api/1/news?apikey=pub_1c4af5dca3644a64932f1d7db00a3083" +
                    "&category=business&country=cn&language=zh";

                const res = await fetch(url);
                const data = await res.json();

                const list = (data.results || []).map((n: any) => ({
                    title: n.title || "无标题新闻",
                    pubDate: n.pubDate || n.published_at || "时间未知",
                }));

                setNews(list);
            } catch (err) {
                console.error("新闻加载失败：", err);
                setNews([{ title: "新闻加载失败", pubDate: "" }]);
            } finally {
                setLoading(false);
            }
        }

        fetchNews();
    }, []);

    const totalPages = Math.ceil(news.length / PAGE_SIZE);

    const pageNews = news.slice(
        (page - 1) * PAGE_SIZE,
        page * PAGE_SIZE
    );

    return (
        <div className={styles.page}>
            {/* 头部 */}
            <div className={styles.header}>
                <h1>📰 新闻资讯中心</h1>

                {/* 搜索占位 */}
                <div className={styles.searchBox} title="搜索功能即将上线">
                    🔍
                </div>
            </div>

            {/* 内容 */}
            {loading ? (
                <div className={styles.loading}>加载中...</div>
            ) : (
                <>
                    <ul className={styles.newsList}>
                        {pageNews.map((item, index) => (
                            <li className={styles.newsItem}>
                                <div className={styles.row}>
                                    <span className={styles.title}>{item.title}</span>
                                    <span className={styles.date}>{item.pubDate}</span>
                                </div>
                            </li>
                        ))}
                    </ul>

                    {/* 分页 */}
                    <div className={styles.pagination}>
                        <button
                            disabled={page === 1}
                            onClick={() => setPage((p) => p - 1)}
                        >
                            上一页
                        </button>

                        <span>
                            第 {page} / {totalPages} 页
                        </span>

                        <button
                            disabled={page === totalPages}
                            onClick={() => setPage((p) => p + 1)}
                        >
                            下一页
                        </button>
                    </div>
                </>
            )}
        </div>
    );
};

export default NewsPage;
