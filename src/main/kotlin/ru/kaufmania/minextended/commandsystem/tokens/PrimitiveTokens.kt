package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class ExactString(public var str: String) : Token {
    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        return parseItem(reader, str)
    }
    open fun parseItem(reader: TextReader, item: String): ParseResult {
        if (reader.isEof(str.length-1))
            return ParseResult.Maybe(listOf(item), "Unknown symbol: \"${reader[reader.cursor, reader.content.length]}\"")
        if (reader.next(item.length) != item)
            return ParseResult.Wrong("Unknown symbol: \"${reader[reader.cursor, reader.content.length]}\"")
        return ParseResult.Success
    }
}
open class OneOfStrings(vararg var strings: String) : ExactString(strings[0]) {
    protected var argName: String? = null
    fun store(argName: String): OneOfStrings {this.argName = argName; return this}

    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        for ((i, it) in strings.withIndex()) {
            reader.push()
            if (parseItem(reader, it) is ParseResult.Success) {
                commandArguments?.set(argName, it)
                commandArguments?.set(argName, i)
                return ParseResult.Success
            }
            reader.pop()
        }
        return ParseResult.Maybe(strings.toList(), "Value should be one of next values: ${strings.joinToString(", ")}")
    }
}