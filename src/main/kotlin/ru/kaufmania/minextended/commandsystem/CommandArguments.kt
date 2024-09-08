package ru.kaufmania.minextended.commandsystem

class CommandArguments {
    class ArgsMap<T : Any> {
        private val values = mutableMapOf<String, T>()
        operator fun get(value: String) =
            values[value]

        operator fun set(index: String, value: T) =
            values.set(index, value)
    }

    val strings = ArgsMap<String>()
    val ints = ArgsMap<Int>()
    var anys = ArgsMap<Any>()
}