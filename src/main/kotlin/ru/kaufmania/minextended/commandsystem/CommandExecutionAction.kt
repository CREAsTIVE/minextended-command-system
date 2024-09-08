package ru.kaufmania.minextended.commandsystem

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender


open class CommandExecutionAction {
    class ExecutionData(val sender: CommandSender, val arguments: CommandArguments) {
        fun reply(message: String) =
            sender.sendMessage(message)

        fun reply(message: String, color: ChatColor) =
            sender.sendMessage("$color$message")

        fun success(message: String? = null) {
            successfulness = true
            message?.let { reply(message, ChatColor.GREEN) }
        }

        fun fail(message: String? = null) {
            successfulness = false
            message?.let { reply(message, ChatColor.RED) }
        }

        var successfulness: Boolean = true
    }

    var finalizeAction: ExecutionData.() -> Unit = {}

    open fun finalize(arguments: ExecutionData) =
        arguments.apply(finalizeAction).successfulness
}