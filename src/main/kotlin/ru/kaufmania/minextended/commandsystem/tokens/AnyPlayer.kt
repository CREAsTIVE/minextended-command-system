package ru.kaufmania.minextended.commandsystem.tokens

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import ru.kaufmania.minextended.commandsystem.CommandArguments

open class AnyPlayer : OneOfStrings() {
    override fun tryParse(reader: TextReader, sender: CommandSender, commandArguments: CommandArguments): ParseResult {
        this.strings = Bukkit.getServer().onlinePlayers.map { it.name }.toTypedArray()
        return super.parse(reader, sender, commandArguments)
    }
}