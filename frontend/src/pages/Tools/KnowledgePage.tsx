import React from 'react';
import { Link } from 'react-router-dom';
import styles from './knowledge-page.module.css';

const KnowledgePage: React.FC = () => {
    return (
        <main className={styles.page}>
            {/* 每日知识卡片 —— 宽屏左对齐 */}
            <section className={styles.section}>
                <div className={styles.dailyCard}>
                    <div className={styles.badge}>Daily Insight</div>
                    <h1>今日金融小知识</h1>
                    <p>
                        复利的核心在于时间。只要持续投入，即使不高的收益率，
                        也会在长期中形成巨大的财富差距。
                    </p>
                    <Link to="/daily-knowledge" className={styles.linkButton}>
                        了解更多 →
                    </Link>
                </div>
            </section>

            {/* AI 入口 —— 与标题同行 */}
            <section className={styles.aiSection}>
                <div className={styles.aiContainer}>
                    <div>
                        <h2 className={styles.aiTitle}>有金融问题？</h2>
                        <p className={styles.aiSubtitle}>随时向 AI 助手提问，获取专业、清晰的解答</p>
                    </div>
                    <Link to="/messages" className={styles.aiButton}>
                        💬 立即咨询 AI 助手
                    </Link>
                </div>
            </section>

            {/* 四大模块 —— 宽屏 4 列 */}
            <section className={styles.section}>
                <h2>深入学习金融领域</h2>
                <p className={styles.sectionDesc}>系统化构建你的金融知识框架</p>
                <div className={styles.moduleGrid}>
                    {[
                        { icon: '📈', title: '投资基础', desc: '理解风险、收益与资产配置的基本原理。', path: '/modules/investing' },
                        { icon: '🏦', title: '金融市场', desc: '掌握股票、债券、外汇等市场的运作机制。', path: '/modules/markets' },
                        { icon: '💰', title: '个人理财', desc: '制定预算、储蓄、保险与退休规划策略。', path: '/tools/analysis' },
                        { icon: '🌍', title: '宏观经济', desc: '解读 GDP、通胀、利率与政策对市场的影响。', path: '/modules/macro' },
                    ].map((m) => (
                        <Link key={m.title} to={m.path} className={styles.moduleCard}>
                            <span className={styles.moduleIcon}>{m.icon}</span>
                            <h3>{m.title}</h3>
                            <p>{m.desc}</p>
                        </Link>
                    ))}
                </div>
            </section>

            {/* 推荐书籍 + 我的书架 —— 双栏：左书籍列表，右书架入口（或全宽书籍网格） */}
            <section className={styles.section}>
                <div className={styles.booksHeader}>
                    <h2>精选金融好书</h2>
                    <Link to="/bookshelf" className={styles.bookshelfLink}>📚 我的书架</Link>
                </div>
                <div className={styles.bookGrid}>
                    {[1, 2, 3, 4, 5, 6].map((i) => (
                        <div key={i} className={styles.bookCard}>
                            <div className={styles.bookCover} />
                            <h4>金融经典 {i}</h4>
                            <p className={styles.bookAuthor}>作者姓名</p>
                            <p className={styles.bookDesc}>适合建立长期金融认知体系的必读书籍。</p>
                        </div>
                    ))}
                </div>
            </section>

            {/* 视频讲解 —— 宽屏 3 列 */}
            <section className={styles.section}>
                <h2>专家视频讲解</h2>
                <div className={styles.videoGrid}>
                    {[1, 2, 3].map((v) => (
                        <Link key={v} to="/videos" className={styles.videoCard}>
                            <div className={styles.videoThumb}>▶ 视频 {v}</div>
                            <h3>金融趋势深度解析</h3>
                            <p>12 分钟 · 专家主讲</p>
                        </Link>
                    ))}
                </div>
            </section>
        </main>
    );
};

export default KnowledgePage;