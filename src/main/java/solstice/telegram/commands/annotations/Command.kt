package solstice.telegram.commands.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    val command: String,
    val aliases: Array<String>
)
