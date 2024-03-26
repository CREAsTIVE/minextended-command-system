# General
The **Minextended CommandSystem** library is one of the powerful **Minextended** tools that greatly extends existed Spigot API

_This tool was written for Kotlin. You can still use it with Java, but it can be extremely inconvenient_

This library allow you to create you're own command with a complex syntax kinda similar to regex. 

Every command, created with this library have **parse error output** and **tab auto-completion**

**For example:**
```kotlin
override fun onEnable() {
    commandManager.command("test", CustomCommand()
        .syntax(
            TokenList( // "/test path1 [any]"
                ExactString("path1"),
                AnyContinuesString().store("text")
            )
        ) {
            _, _, args -> // For "/test path1 a bc d" reply by "a bc d"
            return@syntax CommandExecutionResult(true)
                .reply(args.strArgs["text"]!!)
        }
        .syntax(
            TokenList( // "/test path2 (var1|var 2|something else) <anyPlayer>"
                ExactString("path2"),
                OneOfStrings("var1", "var 2", "something else").store("var"), // also supports spaces
                AnyPlayer().store("player")
            )
        ) {
            _, _, args ->
            return@syntax CommandExecutionResult(true)
                .reply("Selected variant ${args.strArgs["var"]} at index ${args.intArgs["var"]} with player ${args.strArgs["player"]}")
        }
    )

    commandManager.register(this)
}
```

# Installation
For the last version use:

**Maven:**
```xml
<repository>
    ...
    <id>kaufmania</id>
    <url>http://kaufmania.ru:8080/snapshots</url>
    ...
</repository>

<dependency>
    ...
    <groupId>ru.kaufmania.minextended</groupId>
    <artifactId>command-system</artifactId>
    <version>1.0.1</version>
    ...
</dependency>
```

# Planned
Currently, I'm focus my view on creating more token types for any possible thing, so you can request you're own token types in the **issues**

I'm also want to create something like parser for the command, allowing to define command by String like: `/test path2 <oneOf:var1|var 2|something else:variant> <anyPlayer:player>` 

Also, I'm want try to make **java** support and working on **documentation**