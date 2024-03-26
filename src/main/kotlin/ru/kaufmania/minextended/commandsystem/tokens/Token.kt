package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

interface Token {
    fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult

    fun<T> t() = this as T
}

interface ParseResult {
    class Success : ParseResult
    open class Wrong(val errorMessage: String) : ParseResult
    class Maybe(val possibleVariants: List<String>, errorMessage: String) : Wrong(errorMessage)

    companion object{
        val Success = Success()
    }

}