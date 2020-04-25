package com.keuin.kbackupfabric.command;

import com.keuin.kbackupfabric.suggestion.BackupNameSuggestionProvider;
import com.keuin.kbackupfabric.util.PermissionValidator;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public final class CommandRegister {
    // First make method to register
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        // register /kb and /kb help for help menu

        dispatcher.register(CommandManager.literal("kb").executes(CommandMain::kb));
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("help").executes(CommandMain::help)));

        // register /kb list for showing the backup list. OP is required.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("list").requires(PermissionValidator::op).executes(CommandMain::list)));

        // register /kb backup [name] for performing backup. OP is required.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("backup").then(CommandManager.argument("backupName", StringArgumentType.greedyString()).requires(PermissionValidator::op).executes(CommandMain::backup)).requires(PermissionValidator::op).executes(CommandMain::backupWithDefaultName)));

        // register /kb restore <name> for performing restore. OP is required.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("restore").then(CommandManager.argument("backupName", StringArgumentType.greedyString()).suggests(BackupNameSuggestionProvider.getProvider()).requires(PermissionValidator::op).executes(CommandMain::restore)).executes(CommandMain::list)));

        // register /kb delete [name] for deleting an existing backup. OP is required.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("delete").then(CommandManager.argument("backupName", StringArgumentType.greedyString()).suggests(BackupNameSuggestionProvider.getProvider()).requires(PermissionValidator::op).executes(CommandMain::delete))));

        // register /kb confirm for confirming the execution. OP is required.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("confirm").requires(PermissionValidator::op).executes(CommandMain::confirm)));

        // register /kb cancel for cancelling the execution to be confirmed. OP is required.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("cancel").requires(PermissionValidator::op).executes(CommandMain::cancel)));

        // register /kb prev for showing the latest backup.
        dispatcher.register(CommandManager.literal("kb").then(CommandManager.literal("prev").requires(PermissionValidator::op).executes(CommandMain::prev)));
    }
}
