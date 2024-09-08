package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

abstract class Token {
    abstract fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult
    fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        return when (val result = tryParse(reader, sender, commandArguments)) {
            is ParseResult.Success -> result
            is ParseResult.Maybe -> ParseResult.Maybe(result.possibleVariants, overrideExpected ?: result.expected)
            is ParseResult.Wrong -> ParseResult.Wrong(overrideExpected ?: result.expected)
            else -> ParseResult.Success
        }
    }


    private var storeName: String? = null
    fun store(name: String) { storeName = name }

    private var overrideExpected: String? = null

    fun overrideExpected(value: String) =
        apply { overrideExpected = value }

    protected fun<T : Any> writeValueToStored(argumentsMap: CommandArguments.ArgsMap<T>, value: T) =
        storeName?.let { argumentsMap[it] = value }
}

interface ParseResult {
    class Success : ParseResult
    open class Wrong(val expected: String) : ParseResult
    class Maybe(val possibleVariants: List<String>, expected: String) : Wrong(expected)

    companion object{
        val Success = Success()
    }

}