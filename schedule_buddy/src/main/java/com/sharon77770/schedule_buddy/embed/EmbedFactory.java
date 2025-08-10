package com.sharon77770.schedule_buddy.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.Color;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * EmbedFactory: 일정 봇에서 사용하는 임베드들을 생성하는 유틸 클래스
 */
public final class EmbedFactory {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private EmbedFactory() {}

    private static Instant toInstant(LocalDateTime ldt, ZoneId zone) {
        return ldt.atZone(zone).toInstant();
    }

    /**
     * 일정 추가 성공 응답 임베드
     */
    public static MessageEmbed scheduleAddedEmbed(
            String title,
            String description,
            LocalDateTime dateTime,
            String category,
            String creatorTag,
            ZoneId zone
    ) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("✅ 일정 등록 완료");
        eb.setDescription(String.format("**%s** 일정이 등록되었습니다.", title));
        eb.addField("제목", title, true);
        eb.addField("카테고리", category == null || category.isBlank() ? "기본" : category, true);

        String dtStr = dateTime.format(dtf) + " (" + zone.getId() + ")";
        eb.addField("일시", dtStr, false);

        if (description != null && !description.isBlank()) {
            eb.addField("설명", description, false);
        }

        eb.addField("작성자", creatorTag, true);
        eb.setTimestamp(toInstant(dateTime, zone));
        eb.setFooter("ScheduleBuddy", null);
        eb.setColor(new Color(0x2ECC71)); // 녹색
        return eb.build();
    }

    /**
     * 일정 목록 임베드 (여러 일정을 한 임베드에 담을 때)
     * 최대 필드 수(Discord 제한)를 고려해 적절히 잘라 쓰세요.
     */
    public static MessageEmbed scheduleListEmbed(
            List<ScheduleSummary> schedules,
            ZoneId zone,
            String titleOfEmbed
    ) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(titleOfEmbed == null ? "📅 일정 목록" : titleOfEmbed);
        eb.setDescription("다음은 등록된 일정들입니다. (최근 10개 표시)");
        eb.setColor(new Color(0x3498DB)); // 파랑

        int count = 0;
        for (ScheduleSummary s : schedules) {
            if (count++ >= 10) break; // embed 필드 제한을 고려한 샘플 컷오프
            String when = s.dateTime.format(dtf) + " (" + zone.getId() + ")";
            String value = String.format("카테고리: %s\n작성자: %s\nID: `%d`",
                    s.category == null ? "기본" : s.category,
                    s.creatorTag,
                    s.id);
            eb.addField(s.title + " — " + when, value, false);
        }

        eb.setFooter("use /schedule list [category] to filter", null);
        return eb.build();
    }

    /**
     * 알림(리마인드) 임베드
     */
    public static MessageEmbed reminderEmbed(
            long scheduleId,
            String title,
            LocalDateTime dateTime,
            String guildName,
            ZoneId zone
    ) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("⏰ 일정 알림");
        eb.setDescription(String.format("`%s` 일정이 곧 시작합니다.", title));
        eb.addField("일정 ID", String.valueOf(scheduleId), true);
        eb.addField("서버", guildName, true);
        eb.addField("일시", dateTime.format(dtf) + " (" + zone.getId() + ")", false);
        eb.setTimestamp(Instant.now());
        eb.setFooter("ScheduleBuddy • reminder", null);
        eb.setColor(new Color(0xF1C40F)); // 노랑
        return eb.build();
    }

    /**
     * 오류/잘못된 사용 응답 임베드
     */
    public static MessageEmbed errorEmbed(String title, String details) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("❗ 오류");
        eb.setDescription(title);
        if (details != null && !details.isBlank()) eb.addField("상세", details, false);
        eb.setColor(new Color(0xE74C3C)); // 빨강
        return eb.build();
    }

    /**
     * 도움말/사용법 임베드
     */
    public static MessageEmbed helpEmbed() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("📘 ScheduleBuddy 사용법");
        eb.setDescription("기본 명령어 목록:");
        eb.addField("/schedule add <제목> <YYYY-MM-DD> <HH:mm> [카테고리]", "일정 추가", false);
        eb.addField("/schedule list [카테고리]", "일정 목록 조회 (오늘/이번주)", false);
        eb.addField("/schedule edit <ID> <필드> <값>", "일정 수정", false);
        eb.addField("/schedule delete <ID>", "일정 삭제", false);
        eb.setFooter("버그 제보 및 기여 환영 • GitHub: <repo-url>", null);
        eb.setColor(new Color(0x9B59B6)); // 보라
        return eb.build();
    }

    // --- 간단한 스케줄 요약 DTO (임베드에 쓸 요약 정보)
    public static class ScheduleSummary {
        public long id;
        public String title;
        public LocalDateTime dateTime;
        public String category;
        public String creatorTag;

        public ScheduleSummary(long id, String title, LocalDateTime dateTime, String category, String creatorTag) {
            this.id = id;
            this.title = title;
            this.dateTime = dateTime;
            this.category = category;
            this.creatorTag = creatorTag;
        }
    }
}