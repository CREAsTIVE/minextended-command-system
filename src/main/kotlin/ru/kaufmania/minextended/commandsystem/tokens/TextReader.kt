package ru.kaufmania.minextended.commandsystem.tokens

public fun TextReader(other: TextReader): TextReader = TextReader(other.content, other.cursor)

public class TextReader(val content: String, var cursor: Int = 0) {
    private val cursorStack = mutableListOf<Int>()

    fun push() = cursorStack.add(cursor)
    fun pop(){
        cursor = cursorStack.last()
        cursorStack.removeLast()
    }
    fun rem() = cursorStack.removeLast()

    operator fun get(i: Int): Char = content[i]

    operator fun get(a: Int, b: Int): String = content.substring(a, b)

    fun unext(): Char? = if (isEof()) null else next()

    fun next(): Char = this[cursor++]
    fun next(count: Int): String {
        cursor += count
        return this[cursor - count, cursor]
    }

    fun current(): Char = this[cursor]
    fun current(count: Int): String = this[cursor, cursor + count]

    fun isEof(): Boolean = cursor >= content.length
    fun isEof(len: Int) = cursor + len >= content.length

    infix fun apply(other: TextReader): TextReader {
        this.cursor = other.cursor
        return this
    }
}