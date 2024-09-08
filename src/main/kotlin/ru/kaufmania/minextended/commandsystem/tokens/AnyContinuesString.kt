package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class AnyContinuesString : Token() {
    lateinit var str: String

    override fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        str = ""
        if (reader.isEof())
            return ParseResult.Wrong("any string")

        while (!reader.isEof())
            str += reader.next()

        writeValueToStored(commandArguments.strings, str);

        return ParseResult.Success
    }
}