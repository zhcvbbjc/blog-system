// src/pages/ToolPage.tsx

import React from "react";
import styles from "./toolPage.module.css";
import { Link } from "react-router-dom";

// 引入三个实时数据卡片
import NewsCard from "../components/tools/NewsCard";
import MarketIndexCard from "../components/tools/MarketIndexCard";
import HotStockCard from "../components/tools/HotStockCard";

const ToolPage: React.FC = () => {
    return (
        <div className={styles.container}>

            {/* 四大模块入口 */}
            <div className={styles.featureGrid}>
                <Link to="/tools/news" className={styles.featureCard}>
                    <h3>新闻资讯</h3>
                    <p>实时金融市场新闻，助你掌握市场动态。</p>
                </Link>

                <Link to="/tools/market" className={styles.featureCard}>
                    <h3>市场行情</h3>
                    <p>主要指数与板块行情，快速洞察市场趋势。</p>
                </Link>

                <Link to="/tools/knowledge" className={styles.featureCard}>
                    <h3>金融知识</h3>
                    <p>投资基础、专业解读，提升你的金融素养。</p>
                </Link>

                <Link to="/tools/analysis" className={styles.featureCard}>
                    <h3>理财分析</h3>
                    <p>个性化理财分析与投资策略建议。</p>
                </Link>
            </div>

            {/* 主体内容：左新闻 + 右行情 */}
            <div className={styles.mainContent}>

                {/* 左侧：新闻卡片（自动实时数据） */}
                <div className={styles.newsSection}>
                    <NewsCard />
                </div>

                {/* 右侧行情区：大盘行情 + 热门股票 */}
                <div className={styles.marketSection}>
                    <div className={styles.marketCard}>
                        <MarketIndexCard />
                    </div>

                    <div className={styles.marketCard}>
                        <HotStockCard />
                    </div>
                </div>

            </div>
        </div>
    );
};

export default ToolPage;
