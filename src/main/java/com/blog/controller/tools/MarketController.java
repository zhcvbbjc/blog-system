package com.blog.controller.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/api/tools/market")
public class MarketController {

    @Autowired  // 关键：必须添加这个注解
    private RestTemplate restTemplate;

    private final String API_KEY = "BVNWIIJNCFYO53EA";

    // 单个股票查询（保持原样）
    @GetMapping("/quote")
    public ResponseEntity<?> getQuote(@RequestParam String symbol) {
        String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + API_KEY;

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // 新增：批量查询接口（前端调用这个）
    @GetMapping("/indices")
    public ResponseEntity<?> getIndices(@RequestParam String symbols) {
        // 注意：Alpha Vantage 不支持批量查询，需要逐个查询
        String[] symbolArray = symbols.split(",");
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> quotes = new ArrayList<>();

        for (String symbol : symbolArray) {
            try {
                String url = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + API_KEY;
                Map<String, Object> response = restTemplate.getForObject(url, Map.class);

                // 提取关键数据
                if (response != null && response.containsKey("Global Quote")) {
                    Map<String, String> globalQuote = (Map<String, String>) response.get("Global Quote");

                    Map<String, Object> quote = new HashMap<>();
                    quote.put("symbol", symbol);
                    quote.put("price", globalQuote.get("05. price"));
                    quote.put("change", globalQuote.get("09. change"));
                    quote.put("changePercent", globalQuote.get("10. change percent"));
                    quotes.add(quote);
                }

                // Alpha Vantage 限制 5次/分钟，需要添加延迟
                Thread.sleep(1000); // 1秒延迟

            } catch (Exception e) {
                // 单个失败不影响其他
            }
        }

        result.put("data", Map.of("quoteResponse", Map.of("result", quotes)));
        return ResponseEntity.ok(result);
    }
}