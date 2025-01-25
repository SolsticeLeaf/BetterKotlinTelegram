package solstice.telegram.configuration

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.tomlj.Toml
import org.tomlj.TomlParseResult
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.util.*


object Config {
    private val logger: Logger = LoggerFactory.getLogger(Config::class.java)
    var config: TomlParseResult
    var isDebug: Boolean
    var parseMode: String = ParseMode.MARKDOWN

    init {
        config = getToml("config.toml")
        isDebug = get("debug", false)
        parseMode = get("parseMode", ParseMode.MARKDOWN)
    }

    fun debug(runnable: Runnable) {
        if (isDebug) {
            runnable.run()
        }
    }

    private fun getToml(fileName: String): TomlParseResult {
        val file = File("configs", fileName)
        if (!file.exists()) {
            val inputStream = accessFile(fileName)
            if (inputStream != null) {
                try {
                    FileUtils.copyInputStreamToFile(inputStream, file)
                } catch (e: Exception) {
                    logger.warn("Error while copying config file {}", fileName, e)
                }
            } else {
                logger.warn("File '{}' not found inside jar.", fileName)
            }
        }
        try {
            Files.newInputStream(file.toPath()).use { `is` ->
                val result = Toml.parse(`is`)
                for (error in result.errors()) {
                    logger.warn(error.message)
                }
                return result
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun <T> get(key: String, defaultValue: T): T {
        val value = config[key]
        if (value != null) {
            return try {
                value as T
            } catch (e: Exception) {
                defaultValue
            }
        }
        return defaultValue
    }

    fun <T> get(key: String): T? {
        val value = config[key]
        if (value != null) {
            return try {
                value as T
            } catch (e: Exception) {
                null
            }
        }
        return null
    }

    private fun accessFile(file: String): InputStream? {
        val stream = javaClass.getResourceAsStream(file)
        if (stream != null) {
            return stream
        }
        return javaClass.classLoader.getResourceAsStream(file)
    }
}
