package solstice.telegram.commands

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.objects.message.Message
import solstice.telegram.commands.annotations.Command
import solstice.telegram.commands.interfaces.TCommand
import java.util.*


class CommandManager {
    private val logger: Logger = LoggerFactory.getLogger(CommandManager::class.java)
    private val commands: HashMap<List<String>, TCommand> = HashMap<List<String>, TCommand>()

    fun registerCommand(command: TCommand): Boolean  {
        val annotation = command::class.java.getAnnotation(Command::class.java)
        if (annotation == null) {
            logger.info("Annotation not found! Class: {}", command::class.java.getSimpleName())
            return false
        }
        val regCommands = annotation.aliases
            .plusElement(annotation.command)
            .toList()
        if (hasCommand(command)) return false
        commands[regCommands] = command
        logger.info("Registered command: {}", command::class.java.getSimpleName())
        return true
    }

    fun getCommand(cmd: String): TCommand? {
        commands.forEach { (key1, value) ->
            key1.forEach { key ->
                if ((key.lowercase(Locale.getDefault()).replace(" ", "") == cmd.lowercase(Locale.getDefault()).replace(" ", ""))) {
                    return value
                }
            }
        }
        return null
    }

    private fun hasCommand(cmd: TCommand): Boolean {
        commands.values.forEach { command ->
            if (command.hashCode() == cmd.hashCode()) {
                return true
            }
        }
        return false
    }
}
