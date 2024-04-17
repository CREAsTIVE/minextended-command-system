package ru.kaufmania.minextended.commandsystem.tokens
import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class TokenList(
    vararg var tokens: Token,
    private val eofOnly: Boolean = true,
    private val skipSpaces: Boolean = true
) : Token {
    var parsedCount: Int = 0
    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        tokens.forEachIndexed { index, token ->
            val newReader = TextReader(reader)
            val parseResult = token.parse(newReader, sender, commandArguments)

            if (parseResult !is ParseResult.Success)
                return parseResult

            if (newReader.isEof() && index < tokens.size-1)
                return ParseResult.Wrong("Not enough arguments")

            if (index < tokens.size-1 && skipSpaces && !newReader.isEof() && !newReader.next().isWhitespace())
                return ParseResult.Wrong("Space required")

            parsedCount = index

            reader apply newReader
        }
        if (eofOnly && !reader.isEof()) return ParseResult.Wrong("Too many symbols")
        return ParseResult.Success
    }
}