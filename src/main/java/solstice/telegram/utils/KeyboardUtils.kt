package solstice.telegram.utils

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object KeyboardUtils {

    fun getKeyboard(buttons: List<String>): ReplyKeyboardMarkup {
        val keyboard = ReplyKeyboardMarkup(getKeyboardRowList(buttons))
        keyboard.selective = true
        keyboard.resizeKeyboard = true
        keyboard.oneTimeKeyboard = false
        return keyboard
    }

    fun getInlineKeyboard(buttons: List<InlineKeyboardButton>): InlineKeyboardMarkup {
        return InlineKeyboardMarkup(getInlineKeyboardRowList(buttons))
    }

    private fun getKeyboardRowList(buttons: List<String>): List<KeyboardRow> {
        val list = ArrayList<KeyboardRow>()
        for (button in buttons) {
            list.add(KeyboardRow(button))
        }
        return list
    }

    private fun getInlineKeyboardRowList(buttons: List<InlineKeyboardButton>): List<InlineKeyboardRow> {
        val list = ArrayList<InlineKeyboardRow>()
        for (button in buttons) {
            list.add(InlineKeyboardRow(button))
        }
        return list
    }
}
