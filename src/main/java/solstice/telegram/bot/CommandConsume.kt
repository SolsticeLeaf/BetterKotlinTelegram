package solstice.telegram.bot

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.objects.Update
import solstice.telegram.commands.data.CommandContext
import solstice.telegram.commands.interfaces.TCommand

interface CommandConsume {
    fun onCommand(command: TCommand, context: CommandContext, telegramClient: OkHttpTelegramClient, args: List<String>)
    fun onMessage(update: Update, context: CommandContext, telegramClient: OkHttpTelegramClient)
}