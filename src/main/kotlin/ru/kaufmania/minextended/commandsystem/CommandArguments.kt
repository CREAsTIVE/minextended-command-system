package ru.kaufmania.minextended.commandsystem

class CommandArguments {
    val strArgs = mutableMapOf<String, String>()
    fun set(arg: String?, value: String) { arg?.let { strArgs[it] = value } }

    val intArgs = mutableMapOf<String, Int>()
    fun set(arg: String?, value: Int) { arg?.let { intArgs[it] = value } }

    val objectArgs = mutableMapOf<String, Any>()
    fun set(arg: String?, value: Any) { arg?.let { objectArgs[it] = value } }
}