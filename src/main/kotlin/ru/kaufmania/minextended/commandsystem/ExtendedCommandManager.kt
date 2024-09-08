package ru.kaufmania.minextended.commandsystem

import org.bukkit.ChatColor
import ru.kaufmania.minextended.commandsystem.tokens.ParseResult
import ru.kaufmania.minextended.commandsystem.tokens.TextReader
import ru.kaufmania.minextended.commandsystem.tokens.TokenList
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class ExtendedCommandManager : CommandExecutor, TabCompleter {
    private val commands = mutableMapOf<String, CustomCommand>()

    fun command(name: String, customCommandAction: CustomCommand.() -> Unit) =
        command(name, CustomCommand(customCommandAction))
    fun command(name: String, command: CustomCommand){
        commands[name] = command
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val commandExecutor = commands[label] ?: return false

        var mostPossibleParse: TokenList? = null
        var mostPossibleParseResult: ParseResult? = null

        for ((syntax, executor) in commandExecutor.syntaxCommandMap) {
            val commandArgs = CommandArguments()
            val parseResult = syntax.parse(TextReader(args.joinToString(" "), 0), sender, commandArgs)

            if (syntax is TokenList){
                if (mostPossibleParse == null ||
                    (syntax.parsedCount.toFloat() / syntax.tokens.size) >
                    (mostPossibleParse.parsedCount.toFloat() / mostPossibleParse.tokens.size)
                ) {
                    mostPossibleParse = syntax
                    mostPossibleParseResult = parseResult
                }
            }

            if (parseResult is ParseResult.Success)
                return executor.finalize(CommandExecutionAction.ExecutionData(sender, commandArgs))
        }

        if (mostPossibleParseResult is ParseResult.Wrong)
            sender.sendMessage("${ChatColor.RED}${mostPossibleParseResult.expected} expected.")
        else
            sender.sendMessage("${ChatColor.RED}Internal error")
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        val commandExecutor = commands[label] ?: return null

        val completions = mutableListOf<String>()

        for (syntax in commandExecutor.syntaxCommandMap) {
            val parseResult = syntax.key.parse(TextReader(args.joinToString(" "), 0), sender, CommandArguments())

            if (parseResult is ParseResult.Maybe)
                completions.addAll(parseResult.possibleVariants)
        }

        return completions
    }

    fun register(plugin: JavaPlugin){
        commands.forEach {
            plugin.getCommand(it.key)?.setExecutor(this)
            plugin.getCommand(it.key)?.tabCompleter = this
        }
    }
}