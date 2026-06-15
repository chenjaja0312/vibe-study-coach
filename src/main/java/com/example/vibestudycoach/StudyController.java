package com.example.vibestudycoach;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class StudyController {
    private final JdbcTemplate jdbcTemplate;
    private final MLService mlService;

    public StudyController(JdbcTemplate jdbcTemplate, MLService mlService) {
        this.jdbcTemplate = jdbcTemplate;
        this.mlService = mlService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/logs")
    public ResponseEntity<?> createLog(@RequestBody StudyLogRequest request) {
        if (request.subject() == null || request.subject().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "subject is required"));
        }
        if (request.minutes() <= 0 || request.difficulty() < 1 || request.difficulty() > 5 || request.confidence() < 1 || request.confidence() > 5) {
            return ResponseEntity.badRequest().body(Map.of("error", "minutes must be positive; difficulty/confidence must be 1-5"));
        }

        MLService.Analysis analysis = mlService.analyze(request);
        jdbcTemplate.update("""
            INSERT INTO study_logs(subject, minutes, difficulty, confidence, note, focus_score, risk_level, suggestion)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """, request.subject(), request.minutes(), request.difficulty(), request.confidence(), request.note(),
                analysis.focusScore(), analysis.riskLevel(), analysis.suggestion());

        StudyLog latest = jdbcTemplate.queryForObject("""
            SELECT * FROM study_logs ORDER BY id DESC LIMIT 1
        """, (rs, rowNum) -> mapLog(rs));

        return ResponseEntity.ok(latest);
    }

    @GetMapping("/logs")
    public List<StudyLog> listLogs() {
        return jdbcTemplate.query("SELECT * FROM study_logs ORDER BY id DESC LIMIT 20", (rs, rowNum) -> mapLog(rs));
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        Integer totalMinutes = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(minutes), 0) FROM study_logs", Integer.class);
        Double avgScore = jdbcTemplate.queryForObject("SELECT COALESCE(AVG(focus_score), 0) FROM study_logs", Double.class);
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM study_logs", Integer.class);
        List<Map<String, Object>> bySubject = jdbcTemplate.queryForList("""
            SELECT subject, SUM(minutes) AS totalMinutes, ROUND(AVG(focus_score), 1) AS avgScore
            FROM study_logs
            GROUP BY subject
            ORDER BY totalMinutes DESC
        """);
        return Map.of(
                "count", count,
                "totalMinutes", totalMinutes,
                "avgScore", Math.round(avgScore * 10.0) / 10.0,
                "bySubject", bySubject
        );
    }

    private StudyLog mapLog(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new StudyLog(
                rs.getLong("id"),
                rs.getString("subject"),
                rs.getInt("minutes"),
                rs.getInt("difficulty"),
                rs.getInt("confidence"),
                rs.getString("note"),
                rs.getInt("focus_score"),
                rs.getString("risk_level"),
                rs.getString("suggestion"),
                rs.getString("created_at")
        );
    }
}
