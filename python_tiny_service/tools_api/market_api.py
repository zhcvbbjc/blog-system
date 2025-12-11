import yfinance as yf

# 获取苹果公司的股票数据
apple = yf.Ticker("AAPL")
# 获取实时行情
print(apple.info)