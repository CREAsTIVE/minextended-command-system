package ru.kaufmania.minextended.commandsystem
import ru.kaufmania.minextended.commandsystem.tokens.Token
import org.bukkit.command.CommandSender

typealias CommandAction = (token: Token, sender: CommandSender, args: CommandArguments) -> CommandExecutionResult

class CustomCommand(
    val syntaxes: MutableMap<Token, CommandAction> = mutableMapOf()
) {
    fun syntax(token: Token, action: CommandAction): CustomCommand {
        syntaxes[token] = action
        return this
    }
}