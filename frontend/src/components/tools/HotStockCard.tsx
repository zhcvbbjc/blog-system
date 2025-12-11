// src/components/tools/HotStockCard.tsx
import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import styles from "./hot-stock-card.module.css";

interface HotStockCardProps {
    style?: React.CSSProperties;
}

interface StockData {
    symbol: string;
    price: string;
    change: string;
}

const HOT_STOCKS = ["AAPL", "MSFT", "AMZN", "GOOGL", "META", "NVDA", "TSLA"];

export default function HotStockCard({ style }: HotStockCardProps) {
    const [stocks, setStocks] = useState<StockData[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        async function fetchHotStocks() {
            try {
                // 方式一：逐个查询（与 MarketIndexCard 逻辑一致）
                const results = await Promise.all(
                    HOT_STOCKS.map(async (symbol) => {
                        try {
                            // 调用你自己的 Java 后端代理接口
                            const res = await fetch(`/api/tools/market/quote?symbol=${symbol}`);
                            const json = await res.json();

                            // Alpha Vantage 返回格式
                            const quote = json["Global Quote"];
                            if (quote) {
                                return {
                                    symbol: symbol,
                                    price: quote["05. price"] || "--",
                                    change: quote["10. change percent"] || "--"
                                };
                            }
                        } catch (e) {
                            console.error(`获取 ${symbol} 失败:`, e);
                        }
                        // 失败时返回默认值
                        return {
                            symbol: symbol,
                            price: "--",
                            change: "--"
                        };
                    })
                );

                setStocks(results);
            } catch (err) {
                console.error("热门股票接口错误：", err);
            } finally {
                setLoading(false);
            }
        }

        fetchHotStocks();
    }, []);

    return (
        <div className={styles.card} style={style}>
            <div className={styles.sectionHeader}>
                <h3 className={styles.title}>🔥 热门股票</h3>
                <Link to="/tools/market" className={styles.viewAll}>查看全部 →</Link>
            </div>

            <div className={styles.list}>
                {loading
                    ? "加载中..."
                    : stocks.map((s) => (
                        <div key={s.symbol} className={styles.item}>
                            <span>{s.symbol}</span>
                            <strong>{s.price}</strong>
                            <span className={s.change.startsWith("+") ? styles.up : styles.down}>
                                {s.change}
                            </span>
                        </div>
                    ))}
            </div>
        </div>
    );
}