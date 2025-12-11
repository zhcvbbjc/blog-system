// src/components/tools/MarketIndexCard.tsx
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import styles from "./market-index-card.module.css";

interface MarketIndexCardProps {
    style?: React.CSSProperties;
}

interface IndexData {
    name: string;
    symbol: string;
    price: string;
    change: string;
}

const INDEX_MAP = [
    { name: "上证指数", symbol: "000001.SS" },
    { name: "深证成指", symbol: "399001.SZ" },
    { name: "创业板指", symbol: "399006.SZ" },
    { name: "道琼斯指数", symbol: "^DJI" },
    { name: "纳斯达克100", symbol: "^NDX" },
    { name: "标普500", symbol: "^GSPC" },
    { name: "恒生指数", symbol: "^HSI" },
];

export default function MarketIndexCard({ style }: MarketIndexCardProps) {
    const [indices, setIndices] = useState<IndexData[]>([]);
    const [loading, setLoading] = useState(true);

    // MarketIndexCard.tsx - 修复版本
    useEffect(() => {
        async function fetchIndices() {
            try {
                // 注意：Alpha Vantage 不支持批量查询，逐个查询
                const results = await Promise.all(
                    INDEX_MAP.map(async (idx) => {
                        try {
                            const res = await fetch(`/api/tools/market/quote?symbol=${idx.symbol}`);
                            const json = await res.json();

                            // Alpha Vantage 返回格式
                            const quote = json["Global Quote"];
                            if (quote) {
                                return {
                                    name: idx.name,
                                    symbol: idx.symbol,
                                    price: quote["05. price"] || "--",
                                    change: quote["10. change percent"] || "--"
                                };
                            }
                        } catch (e) {
                            console.error(`获取 ${idx.name} 失败:`, e);
                        }
                        // 失败时返回默认值
                        return {
                            name: idx.name,
                            symbol: idx.symbol,
                            price: "--",
                            change: "--"
                        };
                    })
                );

                setIndices(results);
            } catch (err) {
                console.error("行情接口错误:", err);
            } finally {
                setLoading(false);
            }
        }

        fetchIndices();
    }, []);

    return (
        <div className={styles.card} style={style}>
            <div className={styles.sectionHeader}>
                <h3 className={styles.title}>📈 大盘行情</h3>
                <Link to="/tools/market" className={styles.viewAll}>查看全部 →</Link>
            </div>

            <div className={styles.list}>
                {loading
                    ? "加载中..."
                    : indices.map((idx) => (
                        <div key={idx.symbol} className={styles.item}>
                            <span>{idx.name}</span>
                            <strong>{idx.price}</strong>
                            <span className={idx.change.startsWith("+") ? styles.up : styles.down}>
                                {idx.change}
                            </span>
                        </div>
                    ))}
            </div>
        </div>
    );
}
