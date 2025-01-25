package solstice.telegram.bot

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import solstice.telegram.commands.CommandManager
import solstice.telegram.commands.data.CommandContext
import solstice.telegram.commands.interfaces.TCommand
import solstice.telegram.configuration.Config
import java.util.UUID

class KotlinBot(telegramBotBuilder: BotBuilder) : LongPollingSingleThreadUpdateConsumer {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val telegramClient: OkHttpTelegramClient = telegramBotBuilder.telegramClient
    private val commandManager: CommandManager = telegramBotBuilder.commandManager
    private val deleteMessages: Boolean = telegramBotBuilder.deleteMessages
    private val commandConsume: CommandConsume? = telegramBotBuilder.commandConsume

    override fun consume(update: Update?) {
        Thread {
            runBlocking {
                try {
                    val id = UUID.randomUUID().toString()
                    Config.debug { logger.info("($id) New command!") }
                    if (update == null) { return@runBlocking }
                    val args = getMessageArgs(update)
                    Config.debug { logger.info("($id) Args: ${args.joinToString(", ")}") }
                    val command = commandManager.getCommand(args[0])
                    Config.debug { logger.info("($id) Command: ${command?.javaClass?.simpleName}") }
                    val message = update.message ?: update.callbackQuery?.message
                    if (message == null) { throw TelegramApiException("Message is null!") }
                    val chatId = message.chatId
                    val messageId = message.messageId
                    val stringId = chatId.toString()
                    Config.debug { logger.info("($id) ChatID: $chatId | MessageID: $messageId") }
                    val context = async { CommandContext(chatId, stringId, update.message?.from, messageId, message) }
                    val argsWithoutCommand = async {
                        try {
                            args.drop(1)
                        } catch (ex: Exception) {
                            listOf()
                        }
                    }
                    commandConsume(id, command, context.await(), argsWithoutCommand.await()).start()
                    deleteMessage(stringId, messageId).start()
                } catch (e: Exception) {
                    logger.error("Error while processing update {}", update, e)
                }
            }
        }.start()
    }

    private fun getMessageArgs(update: Update): List<String> {
        if (update.hasCallbackQuery()) {
            val data = update.callbackQuery.data
            if (!data.isNullOrEmpty()) {
                val callback = update.callbackQuery.data.split(" ")
                if (callback.isNotEmpty() && commandManager.getCommand(callback[0]) != null) {
                    return callback
                }

            }
        }
        if (!update.hasMessage()) { return listOf("null") }
        val message = update.message
        val command: String? = if (message.hasText()) {
            message.text
        } else {
            message.caption
        }
        return when(command) {
            null -> listOf("null!")
            else -> {
                if (command.startsWith("/")) {
                    command.replaceFirst("/", "").split(" ")
                } else {
                    command.split(" ")
                }
            }
        }
    }

    private fun commandConsume(commandId: String, command: TCommand?, context: CommandContext, args: List<String>) = CoroutineScope(Dispatchers.IO).async {
        Config.debug { logger.info("($commandId) Executing command...") }
        if (commandConsume == null) {
            Config.debug { logger.info("($commandId) No found external consumers, using defaults...") }
            command?.execute(command.javaClass.simpleName, context, telegramClient, args)
        } else {
            Config.debug { logger.info("($commandId) Found external consumers") }
            if (command != null) {
                Config.debug { logger.info("($commandId) Using external onCommand method") }
                commandConsume.onCommand(command, context, telegramClient, args)
            } else {
                Config.debug { logger.info("($commandId) Using external onMessage method") }
                commandConsume.onMessage(context, telegramClient)
            }
        }
    }

    private fun deleteMessage(chatId: String, messageId: Int) = CoroutineScope(Dispatchers.IO).async {
        if(deleteMessages) { telegramClient.execute(DeleteMessage(chatId, messageId)) }
    }

    data class BotBuilder(val telegramClient: OkHttpTelegramClient,
                          val commandManager: CommandManager,
                          val deleteMessages: Boolean,
                          val commandConsume: CommandConsume?)
}
