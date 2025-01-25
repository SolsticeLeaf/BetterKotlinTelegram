package solstice.telegram

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import solstice.telegram.bot.CommandConsume
import solstice.telegram.bot.KotlinBot
import solstice.telegram.commands.CommandManager
import solstice.telegram.configuration.Config

class BetterKotlinTelegram(private val botBuilder: Builder) {

    private val logger: Logger = LoggerFactory.getLogger(BetterKotlinTelegram::class.java)

    fun run() {
        runBlocking {
            try {
                logger.info("Starting Telegram bot...")
                logger.info("Debug: {}", Config.isDebug)
                val token = getToken().await()
                val telegramClient = OkHttpTelegramClient(token)
                TelegramBotsLongPollingApplication().registerBot(
                    token,
                    KotlinBot(
                        KotlinBot.BotBuilder(
                            telegramClient,
                            botBuilder.commandManager,
                            botBuilder.deleteMessages,
                            botBuilder.commandConsume
                        ))
                )
                logger.info("Telegram bot started!")
            } catch (e: Exception) {
                logger.error(e.message, e)
            }
        }
    }

    private fun getToken(): Deferred<String> = CoroutineScope(Dispatchers.IO).async {
        return@async if (botBuilder.token != null) {
            botBuilder.token
        } else {
            val telegramToken = Config.get("telegram_token", "telegram bot token")
            if (telegramToken == "telegram bot token") {
                throw Exception("Bot token is empty! Please insert token in configs/config.toml!")
            }
            telegramToken
        }
    }

    data class Builder(val token: String? = null,
                       val commandManager: CommandManager,
                       val deleteMessages: Boolean,
                       val commandConsume: CommandConsume? = null)
}
