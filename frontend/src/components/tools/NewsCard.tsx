// src/components/tools/NewsCard.tsx
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import styles from "./news-card.module.css";

interface NewsCardProps {
    style?: React.CSSProperties;
}

interface NewsItem {
    title: string;
    pubDate: string;
}

export default function NewsCard({ style }: NewsCardProps) {
    const [news, setNews] = useState<NewsItem[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchNews() {
            try {
                const url =
                    "https://newsdata.io/api/1/news?apikey=pub_1c4af5dca3644a64932f1d7db00a3083"
                    + "&category=business&country=cn&language=zh";

                const res = await fetch(url);
                const data = await res.json();

                const list = (data.results || [])
                    .slice(0, 30)  // 最多 30 条
                    .map((n: any) => ({
                        title: n.title || "无标题新闻",
                        pubDate: n.pubDate || n.published_at || "时间未知"
                    }));

                setNews(list);
            } catch (err) {
                console.error("NewsData API 错误：", err);
                setNews([{ title: "新闻加载失败", pubDate: "" }]);
            } finally {
                setLoading(false);
            }
        }

        fetchNews();
    }, []);

    return (
        <div className={styles.card} style={style}>
            <div className={styles.sectionHeader}>
                <h2 className={styles.sectionTitle}>📢 新闻资讯</h2>
                <Link to="/tools/news" className={styles.viewAll}>查看全部 →</Link>
            </div>

            <ul className={styles.newsList}>
                {loading ? (
                    <li>加载中...</li>
                ) : (
                    news.map((item, index) => (
                        <li key={index}>
                            {index + 1}. {item.title} <span style={{ color: "#9ca3af" }}>({item.pubDate})</span>
                        </li>
                    ))
                )}
            </ul>
        </div>
    );
}
