package com.example.vibestudycoach;

public record StudyLogRequest(
        String subject,
        int minutes,
        int difficulty,
        int confidence,
        String note
) {}
