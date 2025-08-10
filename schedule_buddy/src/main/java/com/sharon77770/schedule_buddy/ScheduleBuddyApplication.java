package com.sharon77770.schedule_buddy;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.sharon77770.schedule_buddy.listener.ScheduleListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;

@SpringBootApplication
public class ScheduleBuddyApplication {

	private static String TOKEN = "";

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("YOUR DISCORD BOT TOKEN: ");
		TOKEN = scanner.nextLine();

		JDA jda = JDABuilder
				.createDefault(TOKEN)
				.build();
		
		jda.addEventListener(new ScheduleListener());
		
		jda.updateCommands().addCommands(				
				Commands.slash("도움", "도움말"),
				Commands.slash("help", "도움말"),
				
				Commands.slash("일정등록", "일정을 생성합니다.")
				.addOption(OptionType.INTEGER, "월", "날짜를 지정합니다.", true)
				.addOption(OptionType.INTEGER, "일", "날짜를 지정합니다.", true)
				.addOption(OptionType.INTEGER, "시", "시간을 지정합니다.", true)
				.addOption(OptionType.INTEGER, "분", "시간을 지정합니다.", true)
				.addOption(OptionType.STRING, "장소", "장소를 지정합니다.", true)
				.addOption(OptionType.STRING, "내용", "내용을 지정합니다.", true)
		).queue();
	}

}
