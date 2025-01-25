package solstice.telegram.commands.interfaces

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import solstice.telegram.commands.data.CommandContext

abstract class TCommand {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun execute(command: String, context: CommandContext, client: OkHttpTelegramClient, args: List<String>) = CoroutineScope(Dispatchers.IO).async {
        try {
            onCommand(context, client, args)
        } catch (e: Exception) {
            logger.error("Exception while executing command '$command' on chat: ${context.chatId}", e)
        }
    }

    abstract fun onCommand(context: CommandContext, client: OkHttpTelegramClient, args: List<String>)
}
