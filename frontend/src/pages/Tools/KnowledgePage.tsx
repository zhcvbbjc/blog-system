import React from "react";
import styles from "./knowledge-page.module.css";

export default function KnowledgePage() {
    // 示例数据
    const smallTips = [
        "定期储蓄有助于积累财富",
        "投资需分散风险",
        "了解金融术语可以更快理解市场",
    ];

    const modules = [
        { title: "投资基础" },
        { title: "风险管理" },
        { title: "个人理财" },
        { title: "金融术语库" },
    ];

    const books = [
        { title: "富爸爸穷爸爸", cover: "/books/book1.jpg" },
        { title: "聪明的投资者", cover: "/books/book2.jpg" },
        { title: "金融市场教程", cover: "/books/book3.jpg" },
        { title: "行为金融学", cover: "/books/book4.jpg" },
    ];

    const videos = [
        { title: "投资入门", url: "https://www.youtube.com/embed/VIDEO1" },
        { title: "风险管理技巧", url: "https://www.youtube.com/embed/VIDEO2" },
        { title: "个人理财规划", url: "https://www.youtube.com/embed/VIDEO3" },
    ];

    return (
        <div className={styles.container}>
            {/* 第一部分：标题 + 搜索框 */}
            <div className={styles.header}>
                <h1>知识学习</h1>
                <input
                    type="text"
                    placeholder="搜索金融知识..."
                    className={styles.searchInput}
                />
            </div>

            {/* 第二部分：左侧小卡片+模块，右侧推荐书籍 */}
            <div className={styles.middle}>
                <div className={styles.left}>
                    {/* 小卡片：金融小知识 */}
                    <div className={styles.smallCard}>
                        <h3>金融小知识</h3>
                        <p>{smallTips[Math.floor(Math.random() * smallTips.length)]}</p>
                    </div>

                    {/* 四个模块 2x2 */}
                    <div className={styles.moduleGrid}>
                        {modules.map((m, idx) => (
                            <div key={idx} className={styles.moduleCard}>
                                {m.title}
                            </div>
                        ))}
                    </div>
                </div>

                {/* 右侧推荐书籍 */}
                <div className={styles.right}>
                    <h3>推荐书籍</h3>
                    <div className={styles.booksGrid}>
                        {books.map((b, idx) => (
                            <div key={idx} className={styles.bookCard}>
                                <img src={b.cover} alt={b.title} />
                                <p>{b.title}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </div>

            {/* 第三部分：推荐学习视频 */}
            <div className={styles.videosSection}>
                <h3>推荐学习资料</h3>
                <div className={styles.videoGrid}>
                    {videos.map((v, idx) => (
                        <div key={idx} className={styles.videoCard}>
                            <iframe
                                src={v.url}
                                title={v.title}
                                allowFullScreen
                            ></iframe>
                            <p>{v.title}</p>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
