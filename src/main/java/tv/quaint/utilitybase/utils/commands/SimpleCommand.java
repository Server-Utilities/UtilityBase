package tv.quaint.utilitybase.utils.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import tv.quaint.utilitybase.PluginBase;
import tv.quaint.utilitybase.utils.MainUtils;
import tv.quaint.utilitybase.utils.MessagingUtils;

import java.util.Locale;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;

public abstract class SimpleCommand implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
    public String baseBase = PluginBase.NAME.toLowerCase(Locale.ROOT) + ":";
    public String base;
    public String[] aliases;

    public SimpleCommand(String base, String... aliases) {
        this.base = base;
        this.aliases = aliases;
    }

    public abstract void registerAs(CommandDispatcher<ServerCommandSource> dispatcher);

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, LiteralCommandNode as) {
        for (String alias : aliases) {
            dispatcher.register(literal(alias).redirect(as));
        }
        dispatcher.register(literal(baseBase + base).redirect(as));
        for (String alias : aliases) {
            dispatcher.register(literal(baseBase + alias).redirect(as));
        }

        info("Registered " + getClass().getSimpleName() + "!");
    }

    public void info(String message) {
        MessagingUtils.info(getConsolePrefix() + message);
    }

    public String getConsolePrefix() {
        return this.getClass().getSimpleName() + " ><> ";
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        String input = context.getInput();
        String[] args = input.split(" ");

        TreeSet<String> suggestions = getSuggestion(args);

        return CommandSource.suggestMatching(suggestions, builder);
    }

    public TreeSet<String> getOnlineSuggestion() {
        return new TreeSet<>(MainUtils.getOnlinePlayerNames());
    }

    public abstract TreeSet<String> getSuggestion(String[] args);
}

