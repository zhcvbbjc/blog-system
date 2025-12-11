import React, { useState } from "react";
import styles from "./marketIndex-board.module.css";

const COUNTRIES = {
    china: {
        name: "中国",
        indices: [
            { name: "上证指数", symbol: "SSE:000001" },
            { name: "深证成指", symbol: "SZSE:399001" },
            { name: "创业板指", symbol: "SZSE:399006" }
        ]
    },
    usa: {
        name: "美国",
        indices: [
            { name: "道琼斯", symbol: "DJ:DJI" },
            { name: "纳斯达克100", symbol: "NASDAQ:NDX" },
            { name: "标普500", symbol: "INDEX:SPX" }
        ]
    },
    hk: {
        name: "香港",
        indices: [
            { name: "恒生指数", symbol: "HKEX:HSI" },
            { name: "国企指数", symbol: "HKEX:HSCEI" }
        ]
    },
    japan: {
        name: "日本",
        indices: [
            { name: "日经225", symbol: "INDEX:NKY" },
            { name: "东证指数", symbol: "INDEX:TOPX" }
        ]
    },
    uk: {
        name: "英国",
        indices: [{ name: "富时100", symbol: "INDEX:FTSE" }]
    }
};

function MarketIndexBoard() {
    const [country, setCountry] = useState("china");
    const [indexSymbol, setIndexSymbol] = useState(COUNTRIES.china.indices[0].symbol);
    const [indexName, setIndexName] = useState(COUNTRIES.china.indices[0].name);

    const onCountryChange = (key) => {
        setCountry(key);
        setIndexSymbol(COUNTRIES[key].indices[0].symbol);
        setIndexName(COUNTRIES[key].indices[0].name);
    };

    const onIndexChange = (item) => {
        setIndexSymbol(item.symbol);
        setIndexName(item.name);
    };

    return (
        <div className={styles.container} aria-label="market-index-board">
            {/* 国家切换 */}
            <div className={styles.countryTabs} role="tablist" aria-label="countries">
                {Object.keys(COUNTRIES).map((key) => (
                    <button
                        key={key}
                        type="button"
                        onClick={() => onCountryChange(key)}
                        className={
                            country === key
                                ? `${styles.countryTab} ${styles.countryActive}`
                                : styles.countryTab
                        }
                        aria-pressed={country === key}
                    >
                        {COUNTRIES[key].name}
                    </button>
                ))}
            </div>

            {/* 指数切换 */}
            <div className={styles.indexTabs} role="tablist" aria-label="indices">
                {COUNTRIES[country].indices.map((item) => (
                    <button
                        key={item.symbol}
                        type="button"
                        onClick={() => onIndexChange(item)}
                        className={
                            indexSymbol === item.symbol
                                ? `${styles.indexTab} ${styles.indexActive}`
                                : styles.indexTab
                        }
                        aria-pressed={indexSymbol === item.symbol}
                    >
                        {item.name}
                    </button>
                ))}
            </div>

            {/* 指标数据 */}
            <div className={styles.statGrid} aria-hidden="false">
                <div className={styles.statItem}>
                    <span className={styles.statLabel}>指数</span>
                    <span className={styles.statValue}>{indexName}</span>
                </div>

                <div className={styles.statItem}>
                    <span className={styles.statLabel}>涨跌幅</span>
                    <span className={styles.statValue}>--</span>
                </div>

                <div className={styles.statItem}>
                    <span className={styles.statLabel}>最高</span>
                    <span className={styles.statValue}>--</span>
                </div>

                <div className={styles.statItem}>
                    <span className={styles.statLabel}>最低</span>
                    <span className={styles.statValue}>--</span>
                </div>

                <div className={styles.statItem}>
                    <span className={styles.statLabel}>成交量</span>
                    <span className={styles.statValue}>--</span>
                </div>

                <div className={styles.statItem}>
                    <span className={styles.statLabel}>成交额</span>
                    <span className={styles.statValue}>--</span>
                </div>

                <div className={styles.statItem}>
                    <span className={styles.statLabel}>换手率</span>
                    <span className={styles.statValue}>--</span>
                </div>
            </div>

            {/* 图表 */}
            <div className={styles.chartWrapper}>
                <iframe
                    title={`tv-${indexSymbol}`}
                    src={`https://s.tradingview.com/widgetembed/?symbol=${encodeURIComponent(
                        indexSymbol
                    )}&interval=D&theme=light&style=1&locale=zh_CN`}
                    width="100%"
                    height="100%"
                    frameBorder="0"
                    scrolling="no"
                />
            </div>
        </div>
    );
}

export default MarketIndexBoard;
