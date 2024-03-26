package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class AnyContinuesString : Token {
    protected var argName: String? = null
    fun store(argName: String): AnyContinuesString {this.argName = argName; return this}

    lateinit var str: String

    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        str = ""
        if (reader.isEof())
            return ParseResult.Wrong("String excepted")

        while (!reader.isEof())
            str += reader.next()

        commandArguments?.set(argName, str)

        return ParseResult.Success
    }
}