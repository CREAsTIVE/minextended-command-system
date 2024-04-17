package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class ExactString(protected var str: String) : Token {
    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        return parseItem(reader, str)
    }
    open fun parseItem(reader: TextReader, item: String): ParseResult {
        if (reader.isEof(item.length-1))
            return ParseResult.Maybe(listOf(item), "Unknown symbol: \"${reader[reader.cursor, reader.content.length]}\"")
        if (reader.next(item.length) != item)
            return ParseResult.Wrong("Unknown symbol: \"${reader[reader.cursor, reader.content.length]}\"")
        return ParseResult.Success
    }
}
open class OneOfStrings(vararg var strings: String) : ExactString("") {
    protected var argName: String? = null
    fun store(argName: String): OneOfStrings {this.argName = argName; return this}
    var result: String? = null

    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        for ((i, it) in strings.withIndex()) {
            reader.push()
            if (parseItem(reader, it) is ParseResult.Success) {
                result = it
                commandArguments?.set(argName, it)
                commandArguments?.set(argName, i)
                return ParseResult.Success
            }
            reader.pop()
        }
        return ParseResult.Maybe(strings.toList(), "Value should be one of next values: ${strings.joinToString(", ")}")
    }
}

private typealias FinalAction = (EntitySelectorToken.EntitySelector.(entities: List<Entity>) -> List<Entity>)

interface SelectorModifier {
    fun modify(selector: EntitySelectorToken.EntitySelector)
    fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult
}

class EntityTypeToken : OneOfStrings(), SelectorModifier {
    companion object {
        private val values = EntityType.values().filter { it != EntityType.UNKNOWN }
        private val strValues = values.map { it.key.toString() }.toSet()
    }
    val entityType get() = values[strValues.indexOf(result)]
    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        strings = strValues.toTypedArray()
        return super.parse(reader, sender, commandArguments)
    }

    override fun modify(selector: EntitySelectorToken.EntitySelector) {
        selector.apply { filter { it.type == entityType } }
    }

}
fun List<String>.attachStr(str: String) = this.map { str + it }

open class EntitySelectorToken : Token {
    open class EntitySelector {
        private val filters: MutableList<EntitySelector.(entity: Entity) -> Boolean> = mutableListOf()
        private var finalAction: FinalAction? = null

        private lateinit var sender: CommandSender

        fun select(entities: List<Entity>, sender: CommandSender): List<Entity> {
            this.sender = sender
            var newEntities = entities
            filters.forEach {
                newEntities = newEntities.filter { entity -> it(this, entity) }
            }
            return finalAction?.let { it(this, newEntities) } ?: newEntities
        }

        fun filter(action: EntitySelector.(entity: Entity) -> Boolean) {
            filters.add(action)
        }
        fun final(action: FinalAction){
            finalAction = action
        }
    }

    companion object {
        val defaultSelectors = mapOf(
            "e" to EntitySelector(),
            "a" to EntitySelector().apply {
                filter { it is Player }
            },
            "r" to EntitySelector().apply {
                filter { it is Player }
                final { listOf(it.random()) }
            },
        )

        val selectorMethods = mapOf<String, SelectorModifier>(
            "type" to EntityTypeToken()
        )
    }

    var selectorObject: EntitySelector? = null

    override fun parse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments?): ParseResult {
        val cursorStartPoint = reader.cursor
        var currentEndPoint: Int

        if (reader.isEof(1)) return ParseResult.Maybe(defaultSelectors.keys.map { "@$it" }, "No selector provided")
        if (reader.next() != '@') return ParseResult.Wrong("\"@\" Expected")
        val selector = reader.next(1)
        selectorObject = defaultSelectors[selector]
            ?: return ParseResult.Wrong("Unknown selector \"@$selector\"")

        if (reader.isEof()) return ParseResult.Success

        if (reader.next() == '['){
            while (true){
                currentEndPoint = reader.cursor

                val indexes = OneOfStrings(*selectorMethods.keys.toTypedArray())
                run {
                    val parseResult = indexes.parse(reader, sender, commandArguments)

                    if (parseResult is ParseResult.Maybe)
                        return ParseResult.Maybe(
                            parseResult.possibleVariants.attachStr(reader[cursorStartPoint, currentEndPoint]),
                            parseResult.errorMessage
                        )
                    if (parseResult is ParseResult.Wrong)
                        return parseResult
                }

                currentEndPoint = reader.cursor

                if (reader.isEof()) return ParseResult.Maybe(
                    listOf("=").attachStr(
                        reader[cursorStartPoint, currentEndPoint]
                    ),
                    "\"=\" expected"
                )
                if (reader.next() != '=') return ParseResult.Wrong("\"=\" expected")

                currentEndPoint = reader.cursor

                run {
                    val parsingToken = selectorMethods[indexes.result] ?: return ParseResult.Wrong("Unexpected error (report this issue)")
                    val parseResult = parsingToken.parse(reader, sender, commandArguments)

                    if (parseResult is ParseResult.Maybe)
                        return ParseResult.Maybe(
                            parseResult.possibleVariants.attachStr(reader[cursorStartPoint, currentEndPoint]),
                            parseResult.errorMessage
                        )
                    if (parseResult is ParseResult.Wrong)
                        return parseResult
                    parsingToken.modify(selectorObject!!)
                }

                currentEndPoint = reader.cursor

                if (reader.isEof()) return ParseResult.Maybe(listOf("]", ",").attachStr(reader[cursorStartPoint, currentEndPoint]), "\"]\" or \",\" expected")
                val next = reader.next()
                if (next == ',') continue

                if (next == ']') {
                    return ParseResult.Success
                }
            }
        }

        return ParseResult.Success
    }

}