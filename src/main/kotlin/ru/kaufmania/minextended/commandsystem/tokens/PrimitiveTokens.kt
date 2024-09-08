package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class ExactString(private var str: String) : Token() {
    override fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        return parseItem(reader, str)
    }
    open fun parseItem(reader: TextReader, item: String): ParseResult {
        if (reader.isEof(item.length-1))
            return ParseResult.Maybe(listOf(item), "symbol \"${reader[reader.cursor, reader.content.length]}\"")
        if (reader.next(item.length) != item)
            return ParseResult.Wrong("symbol \"${reader[reader.cursor, reader.content.length]}\"")
        return ParseResult.Success
    }
}
open class OneOfStrings(vararg var strings: String) : ExactString("") {
    override fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        for ((i, str) in strings.withIndex()) {
            reader.push()
            if (parseItem(reader, str) is ParseResult.Success) {
                writeValueToStored(commandArguments.strings, str) // selected value
                writeValueToStored(commandArguments.ints, i) // index of selected value
                return ParseResult.Success
            }
            reader.pop()
        }
        return ParseResult.Maybe(strings.toList(), "one of values: ${strings.joinToString(", ")}")
    }
}

class EofRequired() : Token() {
    override fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        return if(reader.isEof()) ParseResult.Success else ParseResult.Wrong("end of command")
    }
}

class OneOfTokens(vararg val tokens: Token) : Token(){
    override fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        val expectations = mutableListOf<String>()
        val possibleContinues = mutableListOf<String>()
        tokens.forEach {
            val newReader = TextReader(reader)

            when (val parseResult = it.parse(reader, sender, commandArguments)) {
                is ParseResult.Success -> return ParseResult.Success
                is ParseResult.Maybe -> {
                    expectations.add(parseResult.expected)
                    possibleContinues.addAll(parseResult.possibleVariants)
                }
                is ParseResult.Wrong -> {
                    expectations.add(parseResult.expected)
                }
            }
        }

        if (possibleContinues.isNotEmpty())
            return ParseResult.Maybe(possibleContinues, expectations.joinToString(", or "))
        return ParseResult.Wrong(expectations.joinToString(", or "))
    }
}