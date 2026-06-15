# Vibe Study Coach

一個適合計概 Hackathon 的全端小專案：使用者輸入讀書紀錄，後端把資料存進 SQLite，並用簡單 DM/ML 規則分析「專注分數、風險等級、下一步建議」。

## 系統架構

Frontend `HTML/CSS/JS` → REST API `Spring Boot` → Database `SQLite` → DM/ML Module `MLService`

## 功能

- 新增讀書紀錄：科目、分鐘、難度、信心、備註
- Java 後端 API 驗證資料並存入 SQLite
- MLService 計算 focus score、risk level、suggestion
- 前端即時顯示最新分析、科目統計、最近紀錄

## 本機執行

```bash
mvn spring-boot:run
```

打開：`http://localhost:8080`

## API

- `GET /api/health`
- `POST /api/logs`
- `GET /api/logs`
- `GET /api/summary`

## 部署到 Render

1. 把專案推到 GitHub Public Repo
2. Render → New → Web Service → Connect GitHub Repo
3. Build Command: `mvn clean package -DskipTests`
4. Start Command: `java -jar target/vibe-study-coach-0.0.1-SNAPSHOT.jar`
5. 部署完成後，打開 Render 給的網址即可操作

## 可加分方向

- 新增刪除/編輯紀錄 API
- 用 Chart.js 畫科目長條圖
- 把 MLService 改成串接 Gemini / OpenAI API 產生更自然的建議
- 加入登入功能
