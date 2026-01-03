import React, { useState, useEffect, useRef } from "react";
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
    const [indexSymbol, setIndexSymbol] = useState(
        COUNTRIES.china.indices[0].symbol
    );
    const [indexName, setIndexName] = useState(
        COUNTRIES.china.indices[0].name
    );

    const chartRef = useRef(null);

    const onCountryChange = (key) => {
        setCountry(key);
        setIndexSymbol(COUNTRIES[key].indices[0].symbol);
        setIndexName(COUNTRIES[key].indices[0].name);
    };

    const onIndexChange = (item) => {
        setIndexSymbol(item.symbol);
        setIndexName(item.name);
    };

    /** ✅ TradingView 官方方式 1 */
    useEffect(() => {
        if (!chartRef.current) return;

        // 清空旧图表
        chartRef.current.innerHTML = "";

        // 如果 tv.js 还没加载，先加载
        if (!window.TradingView) {
            const script = document.createElement("script");
            script.src = "https://s3.tradingview.com/tv.js";
            script.async = true;

            script.onload = () => {
                createWidget();
            };

            document.body.appendChild(script);
        } else {
            createWidget();
        }

        function createWidget() {
            new window.TradingView.widget({
                container_id: chartRef.current.id,
                symbol: indexSymbol,
                interval: "D",
                timezone: "Asia/Shanghai",
                theme: "light",
                style: "1",
                locale: "zh_CN",
                width: "100%",
                height: 480,
                toolbar_bg: "#ffffff",
                hide_top_toolbar: false,
                hide_legend: false,
                enable_publishing: false,
                allow_symbol_change: false
            });
        }
    }, [indexSymbol]);

    return (
        <div className={styles.container} aria-label="market-index-board">
            {/* 国家切换 */}
            <div className={styles.countryTabs}>
                {Object.keys(COUNTRIES).map((key) => (
                    <button
                        key={key}
                        onClick={() => onCountryChange(key)}
                        className={
                            country === key
                                ? `${styles.countryTab} ${styles.countryActive}`
                                : styles.countryTab
                        }
                    >
                        {COUNTRIES[key].name}
                    </button>
                ))}
            </div>

            {/* 指数切换 */}
            <div className={styles.indexTabs}>
                {COUNTRIES[country].indices.map((item) => (
                    <button
                        key={item.symbol}
                        onClick={() => onIndexChange(item)}
                        className={
                            indexSymbol === item.symbol
                                ? `${styles.indexTab} ${styles.indexActive}`
                                : styles.indexTab
                        }
                    >
                        {item.name}
                    </button>
                ))}
            </div>

            {/* 指标数据 */}
            <div className={styles.statGrid}>
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

            {/* ✅ TradingView 图表容器 */}
            <div className={styles.chartWrapper}>
                <div
                    id="tradingview_chart"
                    ref={chartRef}
                    style={{ width: "100%", height: "100%" }}
                />
            </div>
        </div>
    );
}

export default MarketIndexBoard;
