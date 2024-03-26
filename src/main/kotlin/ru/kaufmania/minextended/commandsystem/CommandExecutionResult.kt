package ru.kaufmania.minextended.commandsystem

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

class CommandExecutionResult(val succsessfull: Boolean){
    private var replyMessage: String? = null
    fun reply(message: String): CommandExecutionResult {replyMessage = message; return this}

    fun apply(sender: CommandSender): Boolean{
        replyMessage?.let {
            sender.sendMessage("${if (succsessfull) ChatColor.GREEN else ChatColor.RED}$it")
        }
        return succsessfull
    }
}