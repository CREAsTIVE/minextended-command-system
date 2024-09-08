package ru.kaufmania.minextended.commandsystem.examples

import ru.kaufmania.minextended.commandsystem.ExtendedCommandManager
import ru.kaufmania.minextended.commandsystem.tokens.OneOfStrings
import ru.kaufmania.minextended.commandsystem.tokens.TokenList

class Example {
    fun setup() = ExtendedCommandManager().apply {
        command("test") {
            syntax(TokenList(OneOfStrings("ok", "fail"))) {

                reply("Test command has been invoked")
            }
        }
    }
}