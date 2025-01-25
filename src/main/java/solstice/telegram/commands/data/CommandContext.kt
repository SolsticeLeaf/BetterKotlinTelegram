package solstice.telegram.commands.data

import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage

data class CommandContext(
    val chatId: Long,
    val stringChatId: String,
    val user: User?,
    val messageId: Int,
    val message: MaybeInaccessibleMessage
)
