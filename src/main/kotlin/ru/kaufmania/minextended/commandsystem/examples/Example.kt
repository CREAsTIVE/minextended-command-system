package ru.kaufmania.minextended.commandsystem.examples

import ru.kaufmania.minextended.commandsystem.ExtendedCommandManager
import ru.kaufmania.minextended.commandsystem.tokens.OneOfStrings
import ru.kaufmania.minextended.commandsystem.tokens.TokenList

fun TestCustomToken(count: Int) =
    OneOfStrings(*((IntArray(count+1){it}).map {it.toString()}.toTypedArray())).overrideExpected("value from 0 to $count")

class Example {
    fun setup() = ExtendedCommandManager().apply {
        command("test") {
            syntax(TokenList(OneOfStrings("ok", "fail").store("isFail"), TestCustomToken(5))) {
                reply("Test command has been invoked")
            }
        }
    }
}