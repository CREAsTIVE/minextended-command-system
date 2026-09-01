# Minextended Command System

A powerful, type-safe command parsing and execution DSL for **Kotlin** on **Paper / Spigot / Bukkit** Minecraft servers.

## Features

- **Expressive DSL**: Declare complex, branching command grammars cleanly using token sequences (`TokenList`, `OneOfStrings`, `ExactString`, etc.).
- **Automatic Tab Completion**: Intelligent auto-completion suggestions generated automatically from the defined command syntax tree.
- **Typed Argument Extraction**: Extracted arguments are mapped and typed directly into the execution context (e.g. `args.strArgs["name"]`, `args.intArgs["index"]`).
- **Rich Error Handling**: Detailed syntax error reporting when command invocations fail to match required tokens.
- **Flexible Token Extensibility**: Create custom domain tokens for server-specific entities, locations, or enums.

## Usage Example

```kotlin
import ru.kaufmania.minextended.commandsystem.*
import ru.kaufmania.minextended.commandsystem.tokens.*
import org.bukkit.plugin.java.JavaPlugin

class MyPlugin : JavaPlugin() {
    private val commandManager = ExtendedCommandManager()

    override fun onEnable() {
        commandManager.command("test", CustomCommand()
            // Branch 1: /test path1 <text...>
            .syntax(
                TokenList(
                    ExactString("path1"),
                    AnyContinuesString().store("text")
                )
            ) { _, _, args ->
                val text = args.strArgs["text"] ?: ""
                CommandExecutionResult(true).reply("Received: $text")
            }
            
            // Branch 2: /test path2 <var1|var2|three> <player>
            .syntax(
                TokenList(
                    ExactString("path2"),
                    OneOfStrings("var1", "var2", "three").store("variant"),
                    AnyPlayer().store("target")
                )
            ) { _, _, args ->
                val variant = args.strArgs["variant"]
                val target = args.strArgs["target"]
                CommandExecutionResult(true).reply("Selected $variant for player $target")
            }
        )

        commandManager.register(this)
    }
}
```

## Installation

### Maven
```xml
<dependency>
    <groupId>ru.kaufmania.minextended</groupId>
    <artifactId>command-system</artifactId>
    <version>1.1.0-ALPHA</version>
</dependency>
```

### Gradle (Kotlin DSL)
```kotlin
implementation("ru.kaufmania.minextended:command-system:1.1.0-ALPHA")
```
