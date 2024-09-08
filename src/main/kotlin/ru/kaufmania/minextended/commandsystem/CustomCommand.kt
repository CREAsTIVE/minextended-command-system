package ru.kaufmania.minextended.commandsystem
import ru.kaufmania.minextended.commandsystem.tokens.Token
import org.bukkit.command.CommandSender

fun CustomCommand(action: CustomCommand.() -> Unit) = CustomCommand().apply(action)

class CustomCommand(
    val syntaxCommandMap: MutableMap<Token, CommandExecutionAction> = mutableMapOf()
) {
    fun syntax(token: Token, action: CommandExecutionAction) = apply {
        syntaxCommandMap[token] = action
    }

    fun syntax(token: Token, action: CommandExecutionAction.ExecutionData.()->Unit) =
        syntax(token, CommandExecutionAction().apply { finalizeAction = action })
}