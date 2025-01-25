package solstice.telegram.bot

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import solstice.telegram.commands.data.CommandContext
import solstice.telegram.commands.interfaces.TCommand

interface CommandConsume {
    fun onCommand(command: TCommand, context: CommandContext, telegramClient: OkHttpTelegramClient, args: List<String>)
    fun onMessage(context: CommandContext, telegramClient: OkHttpTelegramClient)
}