package solstice.telegram.utils

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.message.MaybeInaccessibleMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import solstice.telegram.bot.KotlinBot
import solstice.telegram.commands.data.CommandContext
import solstice.telegram.configuration.Config
import java.io.File
import java.util.*

class MessageUtils(private var client: OkHttpTelegramClient) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Throws(TelegramApiException::class)
    fun send(context: CommandContext, message: String) {
        send(context.chatId, message)
    }

    @Throws(TelegramApiException::class)
    fun send(from: Message, message: String) {
        send(from.chatId, message)
    }

    @Throws(TelegramApiException::class)
    fun send(chatId: Long, message: String) {
        Config.debug { logger.info("Sending message | $chatId: $message") }
        client.execute(getSendMessage(chatId, message))
    }

    @Throws(TelegramApiException::class)
    fun send(context: CommandContext, message: String, keyboard: ReplyKeyboardMarkup?) {
        send(context.chatId, message, keyboard)
    }

    @Throws(TelegramApiException::class)
    fun send(from: Message, message: String, keyboard: ReplyKeyboardMarkup?) {
        send(from.chatId, message, keyboard)
    }

    @Throws(TelegramApiException::class)
    fun send(chatId: Long, message: String, keyboard: ReplyKeyboardMarkup?) {
        Config.debug { logger.info("Sending message with keyboard | $chatId: $message") }
        client.execute(getSendMessage(chatId, message, keyboard))
    }

    @Throws(TelegramApiException::class)
    fun send(context: CommandContext, message: String, keyboard: InlineKeyboardMarkup) {
        send(context.chatId, message, keyboard)
    }

    @Throws(TelegramApiException::class)
    fun send(from: Message, message: String, keyboard: InlineKeyboardMarkup) {
        send(from.chatId, message, keyboard)
    }

    @Throws(TelegramApiException::class)
    fun send(chatId: Long, message: String, keyboard: InlineKeyboardMarkup) {
        Config.debug { logger.info("Sending message with inline keyboard | $chatId: $message") }
        client.execute(getSendMessage(chatId, message, keyboard))
    }

    @Throws(TelegramApiException::class)
    fun sendDocument(context: CommandContext, message: String?, document: File) {
        sendDocument(context.chatId, message, document)
    }

    @Throws(TelegramApiException::class)
    fun sendDocuments(from: Message, message: String?, document: File) {
        sendDocument(from.chatId, message, document)
    }

    @Throws(TelegramApiException::class)
    fun sendDocument(chatId: Long, message: String?, document: File) {
        Config.debug { logger.info("Sending document | $chatId: $message | ${document.name}") }
        client.execute(getSendDocument(chatId, message, document))
    }

    @Throws(TelegramApiException::class)
    fun sendPhoto(context: CommandContext, message: String?, photo: File, isSpoiler: Boolean) {
        sendPhoto(context.chatId, message, photo, isSpoiler)
    }

    @Throws(TelegramApiException::class)
    fun sendPhoto(from: Message, message: String?, photo: File, isSpoiler: Boolean) {
        sendPhoto(from.chatId, message, photo, isSpoiler)
    }

    @Throws(TelegramApiException::class)
    fun sendPhoto(chatId: Long, message: String?, photo: File, isSpoiler: Boolean) {
        Config.debug { logger.info("Sending photo | $chatId: $message | ${photo.name}") }
        client.execute(getSendPhoto(chatId, message, photo, isSpoiler))
    }

    private fun getSendPhoto(chatId: Long, message: String?, document: File, isSpoiler: Boolean): SendPhoto {
        val sendPhoto = SendPhoto(chatId.toString(), InputFile(document))
        sendPhoto.caption = message ?: ""
        sendPhoto.parseMode = Config.parseMode
        sendPhoto.hasSpoiler = isSpoiler
        return sendPhoto
    }

    private fun getSendDocument(chatId: Long, message: String?, document: File): SendDocument {
        val sendDocument = SendDocument(chatId.toString(), InputFile(document))
        sendDocument.caption = message ?: ""
        sendDocument.parseMode = Config.parseMode
        return sendDocument
    }

    private fun getSendMessage(chatId: Long, message: String): SendMessage {
        return SendMessage(chatId.toString(), message)
    }

    private fun getSendMessage(chatId: Long, message: String, keyboard: ReplyKeyboardMarkup?): SendMessage {
        val msg = SendMessage(chatId.toString(), message)
        msg.parseMode = Config.parseMode
        msg.replyMarkup = when(keyboard) {
            null -> ReplyKeyboardRemove(true)
            else -> keyboard
        }
        return msg
    }

    private fun getSendMessage(chatId: Long, message: String, keyboard: InlineKeyboardMarkup): SendMessage {
        val msg = SendMessage(chatId.toString(), message)
        msg.parseMode = Config.parseMode
        msg.replyMarkup = keyboard
        return msg
    }
}
