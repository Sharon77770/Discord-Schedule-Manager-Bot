package com.sharon77770.schedule_buddy.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ScheduleListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String cmd = event.getName();
        
        if (cmd.equals("도움") || cmd.equals("help")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("📘 탈론 봇 사용법");
            eb.setColor(new Color(0x9B59B6));
            eb.setDescription("탈론은 디스코드에서 간편하게 일정을 관리할 수 있는 봇입니다.\n\n"
                    + "**주요 명령어**\n"
                    + "`/일정등록` : 새 일정을 생성합니다.\n"
                    + "  - 옵션: 월, 일, 시, 분, 장소, 내용 (모두 필수)\n\n"
                    + "**참가하기/취소하기 버튼**\n"
                    + "일정 메시지 하단의 버튼으로 참가자를 등록하거나 취소할 수 있습니다.\n\n"
                    + "더 자세한 내용이나 문의는 GitHub 저장소를 확인하세요.");

            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        }

        if (!event.getName().equals("일정등록")) return;

        int month = (int) event.getOption("월").getAsLong();
        int day = (int) event.getOption("일").getAsLong();
        int hour = (int) event.getOption("시").getAsLong();
        int minute = (int) event.getOption("분").getAsLong();
        String place = event.getOption("장소").getAsString();
        String content = event.getOption("내용").getAsString();

        // 일정 날짜시간 생성 (올해 기준)
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        int year = now.getYear();
        LocalDateTime eventDateTime;
        try {
            eventDateTime = LocalDateTime.of(year, month, day, hour, minute);
        } 
        catch (DateTimeException e) {
            event.reply("⚠ 잘못된 날짜 또는 시간이 입력되었습니다.").setEphemeral(true).queue();
            return;
        }

        String dateTimeStr = eventDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("📅 일정: " + content + "\n🕒 시간: " + dateTimeStr + "\n📍 장소: " + place);
        eb.setColor(Color.CYAN);
        eb.setDescription("\n**참가자:**\n(없음)");

        event.replyEmbeds(eb.build())
                .addActionRow(
                        Button.primary("join", "참가하기"),
                        Button.danger("leave", "참가 취소")
                ).queue();
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        MessageEmbed embed = event.getMessage().getEmbeds().get(0);
        if (embed == null) return;

        String desc = embed.getDescription();
        if (desc == null) desc = "";

        // 참가자 목록 파싱
        String[] lines = desc.split("\n");
        List<String> participants = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("**참가자:**")) {
                for (int j = i + 1; j < lines.length; j++) {
                    String name = lines[j].trim();
                    if (!name.isEmpty() && !name.equals("(없음)")) {
                        participants.add(name);
                    }
                }
                break;
            }
        }

        String userMention = event.getUser().getAsMention();

        if (event.getComponentId().equals("join")) {
            if (!participants.contains(userMention)) {
                participants.add(userMention);
            }
        } 
        else if (event.getComponentId().equals("leave")) {
            participants.remove(userMention);
        }

        // 참가자 목록 문자열 만들기
        String participantStr;
        if (participants.isEmpty()) {
            participantStr = "(없음)";
        } 
        else {
            participantStr = String.join("\n", participants);
        }

        String newDesc = lines[0] + "\n" + participantStr;

        EmbedBuilder updated = new EmbedBuilder(embed)
                .setDescription(newDesc);

        event.editMessageEmbeds(updated.build()).queue();
        event.deferEdit().queue();
    }
}
