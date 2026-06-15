package com.example.vibestudycoach;

import org.springframework.stereotype.Service;

@Service
public class MLService {
    public Analysis analyze(StudyLogRequest request) {
        int minutesPart = Math.min(request.minutes(), 120) * 35 / 120;
        int confidencePart = request.confidence() * 10;
        int difficultyPenalty = request.difficulty() * 5;
        int focusScore = clamp(minutesPart + confidencePart - difficultyPenalty + 20, 0, 100);

        String riskLevel;
        String suggestion;

        if (focusScore >= 75) {
            riskLevel = "穩定";
            suggestion = "狀態不錯，可以進入進階題或整理成一頁筆記。";
        } else if (focusScore >= 50) {
            riskLevel = "需要複習";
            suggestion = "建議先做 2 題基礎題，確認觀念後再繼續。";
        } else {
            riskLevel = "高風險";
            suggestion = "先不要硬讀，請把內容切成 25 分鐘小段，並找出最卡的一個觀念。";
        }

        return new Analysis(focusScore, riskLevel, suggestion);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public record Analysis(int focusScore, String riskLevel, String suggestion) {}
}
