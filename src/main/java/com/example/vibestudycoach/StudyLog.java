package com.example.vibestudycoach;

public record StudyLog(
        Long id,
        String subject,
        int minutes,
        int difficulty,
        int confidence,
        String note,
        int focusScore,
        String riskLevel,
        String suggestion,
        String createdAt
) {}
