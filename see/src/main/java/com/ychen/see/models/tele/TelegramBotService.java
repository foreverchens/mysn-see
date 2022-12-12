package com.ychen.see.models.tele;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yyy
 * @wx ychen5325
 * @email q1416349095@gmail.com
 */
@Slf4j
@Service
public class TelegramBotService implements UpdatesListener, ApplicationListener<ContextRefreshedEvent> {


	@Value("${telegram-bot.chatId}")
	private Long chatId;
	/**
	 * token
	 */
	@Value("${telegram-bot.token}")
	private String telegramBotToken;

	/**
	 * bot
	 */
	private TelegramBot bot;

	@Resource
	private Map<String, BotCmdHandleFunc> cmdHandleFuncMap;

	public static TelegramBotService create(long chatId, String token) {
		TelegramBotService telegramBotService = new TelegramBotService();
		telegramBotService.chatId = chatId;
		telegramBotService.bot = new TelegramBot(token);
		telegramBotService.bot.setUpdatesListener(telegramBotService);
		return telegramBotService;
	}

	@Override
	public int process(List<Update> updates) {
		updates.forEach(update -> {
			log.info("机器人收到消息 -> {}", update);
			Message message = update.message();
			String text = message.text();
			if (StringUtils.contains(text, "-")) {
				text = text.split("-")[0];
			}
			BotCmdHandleFunc func = cmdHandleFuncMap.get(text);
			if (Objects.isNull(func)) {
				this.sendMessage("未能识别的指令、可输入/help查看可用指令");
			} else {
				this.sendMessage(func.handle(text));
			}
		});
		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}

	/**
	 * 发送消息
	 *
	 * @param text 消息内容
	 */
	public Message sendMessage(String text) {
		// 	// 图片
		// 	response = bot.execute(new SendPhoto(chatId, text));
		// 文本
		SendResponse response = bot.execute(new SendMessage(chatId, text));
		// }
		log.info("发送消息 -> {}", response.isOk());
		return response.message();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// Create your bot passing the token received from @BotFather
		this.bot = new TelegramBot(this.telegramBotToken);
		// Register for updates
		this.bot.setUpdatesListener(this);
	}

}