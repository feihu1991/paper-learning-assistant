package com.paperlearning;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import javax.net.ssl.*;

public class PaperLearningApplication {
    static final String DB_URL = "jdbc:mysql://localhost:3306/paper_learning?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai";
    static final String DB_USER = "root";
    static final String DB_PASS = "root";
    static final String MINIMAX_API_KEY = "sk-cp-Rgw5YmlI_h9Z9cB29MEIstSKEdpCvKMNBP6lHL5pgS2dwvNDSEURnTBl_YoGnw0pEL1kdy119Y24kF7HxmFFpoFyvoenVliBO6SQq0O1sZWeg3ZJHAQWld8";
    static final int PORT = 8080;
    
    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
    
    public static void main(String[] args) throws IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL driver not found");
        }
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // 论文搜索
        server.createContext("/api/papers/search", exchange -> {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                String query = extractJsonString(body, "query");
                String json = searchPapers(query);
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // AI分析论文
        server.createContext("/api/papers/analyze", exchange -> {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                String paperId = extractJsonString(body, "paperId");
                String title = extractJsonString(body, "title");
                String abstractText = extractJsonString(body, "abstract");
                String json = analyzePaper(paperId, title, abstractText);
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // 论文详情
        server.createContext("/api/papers/detail", exchange -> {
            try {
                String query = exchange.getRequestURI().getQuery();
                String id = "";
                if (query != null && query.contains("id=")) {
                    id = query.split("id=")[1].split("&")[0];
                }
                String json = getPaperDetail(id);
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // 任务列表 - 待分析
        server.createContext("/api/analysis/tasks/pending", exchange -> {
            try {
                String json = getTasksByStatus("pending");
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // 任务列表 - 正在分析
        server.createContext("/api/analysis/tasks/processing", exchange -> {
            try {
                String json = getTasksByStatus("processing");
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // 任务列表 - 已完成
        server.createContext("/api/analysis/tasks/completed", exchange -> {
            try {
                String json = getTasksByStatus("completed");
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // 任务列表 - 全部
        server.createContext("/api/analysis/tasks/all", exchange -> {
            try {
                String json = getAllTasks();
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        // 添加论文
        server.createContext("/api/papers/add", exchange -> {
            try {
                String body = new String(exchange.getRequestBody().readAllBytes(), "UTF-8");
                String json = addPaper(body);
                sendJson(exchange, json);
            } catch (Exception e) {
                sendError(exchange, e.getMessage());
            }
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("========================================");
        System.out.println("Java Backend Started: http://localhost:" + PORT);
        System.out.println("========================================");
        System.out.println("API Endpoints:");
        System.out.println("  GET  /api/papers/search?query=xxx");
        System.out.println("  POST /api/papers/analyze");
        System.out.println("  GET  /api/papers/detail?id=xxx");
        System.out.println("  POST /api/papers/add");
        System.out.println("  GET  /api/analysis/tasks/pending");
        System.out.println("  GET  /api/analysis/tasks/processing");
        System.out.println("  GET  /api/analysis/tasks/completed");
        System.out.println("  GET  /api/analysis/tasks/all");
    }
    
    static void sendJson(HttpExchange exchange, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        byte[] bytes = json.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
    
    static void sendError(HttpExchange exchange, String error) throws IOException {
        String json = "{\"error\":\"" + error.replace("\"", "\\\"") + "\"}";
        sendJson(exchange, json);
    }
    
    static String extractJsonString(String json, String key) {
        try {
            if (json == null || json.isEmpty()) return "";
            if (!json.contains("\"" + key + "\"")) return "";
            String pattern = "\"" + key + "\"";
            int idx = json.indexOf(pattern);
            int colon = json.indexOf(":", idx);
            int start = json.indexOf("\"", colon) + 1;
            int end = json.indexOf("\"", start);
            if (start < 0 || end < 0) return "";
            return json.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
    
    // 搜索论文 (调用arXiv API)
    static String searchPapers(String query) {
        try {
            if (query == null || query.isEmpty()) {
                query = "machine learning";
            }
            URL url = new URL("http://export.arxiv.org/api/query?search_query=all:" 
                + URLEncoder.encode(query, "UTF-8") + "&max_results=10");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder xml = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                xml.append(line);
            }
            reader.close();
            
            // 解析XML
            String xmlStr = xml.toString();
            List<Map<String, String>> papers = new ArrayList<>();
            
            // 简单XML解析
            int entryStart = 0;
            while ((entryStart = xmlStr.indexOf("<entry>", entryStart)) != -1) {
                int entryEnd = xmlStr.indexOf("</entry>", entryStart);
                String entry = xmlStr.substring(entryStart, entryEnd);
                
                Map<String, String> paper = new HashMap<>();
                paper.put("id", extractXml(entry, "id"));
                paper.put("title", extractXml(entry, "title").replace("\n", " "));
                paper.put("abstract", extractXml(entry, "summary").replace("\n", " "));
                paper.put("authors", extractAuthors(entry));
                paper.put("published", extractXml(entry, "published"));
                
                papers.add(paper);
                entryStart = entryEnd;
            }
            
            return new com.google.gson.Gson().toJson(papers);
        } catch (Exception e) {
            return "[{\"error\":\"" + e.getMessage() + "\"}]";
        }
    }
    
    static String extractXml(String xml, String tag) {
        int start = xml.indexOf("<" + tag + ">");
        if (start == -1) return "";
        start += tag.length() + 2;
        int end = xml.indexOf("</" + tag + ">", start);
        if (end == -1) return "";
        return xml.substring(start, end);
    }
    
    static String extractAuthors(String entry) {
        StringBuilder authors = new StringBuilder();
        int pos = 0;
        while ((pos = entry.indexOf("<author><name>", pos)) != -1) {
            pos += 13;
            int end = entry.indexOf("</name>", pos);
            if (end == -1) break;
            if (authors.length() > 0) authors.append(", ");
            authors.append(entry.substring(pos, end));
        }
        return authors.toString();
    }
    
    // AI分析论文
    static String analyzePaper(String paperId, String title, String abstractText) {
        try {
            // 先保存或获取论文ID
            long dbPaperId = -1;
            try {
                Connection conn = getConnection();
                
                // 尝试查找已存在的论文
                PreparedStatement findPs = conn.prepareStatement(
                    "SELECT id FROM papers WHERE id = ? OR title = ? LIMIT 1");
                findPs.setString(1, paperId);
                findPs.setString(2, title);
                ResultSet rs = findPs.executeQuery();
                
                if (rs.next()) {
                    dbPaperId = rs.getLong("id");
                } else {
                    // 创建新论文
                    PreparedStatement insertPs = conn.prepareStatement(
                        "INSERT INTO papers (title, abstract, authors, pdf_url) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                    insertPs.setString(1, title != null ? title : "Unknown");
                    insertPs.setString(2, abstractText != null ? abstractText : "");
                    insertPs.setString(3, "");
                    insertPs.setString(4, paperId != null ? paperId : "");
                    insertPs.executeUpdate();
                    
                    ResultSet newRs = insertPs.getGeneratedKeys();
                    if (newRs.next()) {
                        dbPaperId = newRs.getLong(1);
                    }
                    insertPs.close();
                }
                findPs.close();
                conn.close();
            } catch (Exception e) {
                System.out.println("DB paper error: " + e.getMessage());
            }
            
            if (dbPaperId <= 0) {
                dbPaperId = 1; // 默认
            }
            
            // 保存任务到数据库
            long taskId = -1;
            try {
                Connection conn = getConnection();
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO analysis_tasks (paper_id, status) VALUES (?, 'processing')",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, dbPaperId);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    taskId = rs.getLong(1);
                }
                conn.close();
            } catch (Exception e) {
                System.out.println("DB insert task error: " + e.getMessage());
            }
            
            // 调用MiniMax API
            String prompt = "你是一位耐心的AI导师，专门帮助初学者学习论文。请对以下论文进行详细分析，用通俗易懂的语言解释复杂的概念。\n\n" +
                "论文信息：\n标题：" + (title != null ? title : "N/A") + "\n" +
                "摘要：" + (abstractText != null ? abstractText : "N/A") + "\n" +
                "Paper ID: " + (paperId != null ? paperId : "N/A") + "\n\n" +
                "请用JSON格式返回：\n" +
                "{\"summary\":\"...\",\"keyPoints\":[\"...\"],\"difficulty\":\"...\",\"prerequisites\":[\"...\"]}";
            
            String jsonBody = "{\"model\":\"MiniMax-M2.5\",\"max_tokens\":2048,\"messages\":[{\"role\":\"user\",\"content\":\"" + 
                prompt.replace("\"", "\\\"").replace("\n", "\\n") + "\"}]}";
            
            URL url = new URL("https://api.minimaxi.com/v1/text/chatcompletion_v2");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + MINIMAX_API_KEY);
            conn.setDoOutput(true);
            conn.setConnectTimeout(120000);
            conn.setReadTimeout(120000);
            
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8"));
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            String respStr = response.toString();
            System.out.println("MiniMax response: " + respStr.substring(0, Math.min(500, respStr.length())));
            
            // 解析JSON响应 - 改进的解析逻辑
            String content = "";
            try {
                // 使用Gson解析
                com.google.gson.JsonObject respJson = new com.google.gson.JsonParser().parse(respStr).getAsJsonObject();
                com.google.gson.JsonArray choices = respJson.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    com.google.gson.JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
                    if (message != null) {
                        content = message.get("content").getAsString();
                    }
                }
            } catch (Exception e) {
                System.out.println("JSON parse error: " + e.getMessage());
            }
            
            // 清理content中的markdown代码块标记
            content = content.replace("```json", "").replace("```", "").trim();
            
            // 提取JSON
            String resultJson = content;
            if (content.contains("{")) {
                int start = content.indexOf("{");
                int end = content.lastIndexOf("}");
                if (end > start) {
                    resultJson = content.substring(start, end + 1);
                }
            } else {
                // 如果没有JSON，返回简单格式
                resultJson = "{\"raw\":\"" + content.substring(0, Math.min(200, content.length())) + "\"}";
            }
            
            // 保存分析结果到数据库
            try {
                Connection conn2 = getConnection();
                PreparedStatement ps2 = conn2.prepareStatement(
                    "INSERT INTO paper_analysis (paper_id, background, coreConcepts, methodology, keyInnovations, learningPath, difficulty, estimated_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps2.setLong(1, dbPaperId);
                ps2.setString(2, resultJson);
                ps2.setString(3, "[]");
                ps2.setString(4, "{}");
                ps2.setString(5, "[]");
                ps2.setString(6, "[]");
                ps2.setInt(7, 2);
                ps2.setInt(8, 120);
                ps2.setString(9, "completed");
                ps2.executeUpdate();
                
                // 更新任务状态
                if (taskId > 0) {
                    PreparedStatement ps3 = conn2.prepareStatement(
                        "UPDATE analysis_tasks SET status = 'completed' WHERE id = ?");
                    ps3.setLong(1, taskId);
                    ps3.executeUpdate();
                    ps3.close();
                }
                
                conn2.close();
            } catch (Exception e) {
                System.out.println("DB save error: " + e.getMessage());
            }
            
            return "{\"paperId\":\"" + paperId + "\",\"analysis\":" + resultJson + ",\"model\":\"MiniMax-M2.5\"}";
            
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }
    
    // 获取论文详情
    static String getPaperDetail(String id) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT p.*, pa.background, pa.coreConcepts, pa.difficulty FROM papers p " +
                "LEFT JOIN paper_analysis pa ON p.id = pa.paper_id WHERE p.id = ?");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            
            List<Map<String, Object>> results = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> paper = new HashMap<>();
                paper.put("id", rs.getLong("id"));
                paper.put("title", rs.getString("title"));
                paper.put("abstract", rs.getString("abstract"));
                paper.put("authors", rs.getString("authors"));
                paper.put("pdf_url", rs.getString("pdf_url"));
                results.add(paper);
            }
            conn.close();
            
            return new com.google.gson.Gson().toJson(results);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    // 添加论文
    static String addPaper(String json) {
        try {
            String title = extractJsonString(json, "title");
            String abstractText = extractJsonString(json, "abstract");
            String authors = extractJsonString(json, "authors");
            String pdfUrl = extractJsonString(json, "pdf_url");
            
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO papers (title, abstract, authors, pdf_url) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, abstractText);
            ps.setString(3, authors);
            ps.setString(4, pdfUrl);
            ps.executeUpdate();
            
            ResultSet rs = ps.getGeneratedKeys();
            long id = -1;
            if (rs.next()) {
                id = rs.getLong(1);
            }
            conn.close();
            
            return "{\"success\":true,\"id\":" + id + "}";
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
    
    // 按状态获取任务
    static String getTasksByStatus(String status) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT t.*, p.title, p.abstract FROM analysis_tasks t " +
                "LEFT JOIN papers p ON t.paper_id = p.id WHERE t.status = ? ORDER BY t.created_at DESC");
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            
            List<Map<String, Object>> tasks = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> task = new HashMap<>();
                task.put("id", rs.getLong("id"));
                task.put("paper_id", rs.getString("paper_id"));
                task.put("title", rs.getString("title"));
                task.put("abstract", rs.getString("abstract"));
                task.put("status", rs.getString("status"));
                task.put("created_at", rs.getTimestamp("created_at").toString());
                tasks.add(task);
            }
            conn.close();
            
            return new com.google.gson.Gson().toJson(tasks);
        } catch (Exception e) {
            return "[]";
        }
    }
    
    // 获取所有任务
    static String getAllTasks() {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT t.*, p.title, p.abstract FROM analysis_tasks t " +
                "LEFT JOIN papers p ON t.paper_id = p.id ORDER BY t.created_at DESC LIMIT 100");
            ResultSet rs = ps.executeQuery();
            
            List<Map<String, Object>> tasks = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> task = new HashMap<>();
                task.put("id", rs.getLong("id"));
                task.put("paper_id", rs.getString("paper_id"));
                task.put("title", rs.getString("title"));
                task.put("abstract", rs.getString("abstract"));
                task.put("status", rs.getString("status"));
                task.put("created_at", rs.getTimestamp("created_at").toString());
                tasks.add(task);
            }
            conn.close();
            
            return new com.google.gson.Gson().toJson(tasks);
        } catch (Exception e) {
            return "[]";
        }
    }
}
