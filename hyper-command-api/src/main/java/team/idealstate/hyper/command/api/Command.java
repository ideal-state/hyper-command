/*
 * Copyright 2024 ideal-state
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package team.idealstate.hyper.command.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.hyper.command.api.action.ActionInterceptor;
import team.idealstate.hyper.command.api.argument.ArgumentAcceptor;
import team.idealstate.hyper.command.api.complete.CommandCompleter;
import team.idealstate.hyper.command.api.example.ExampleProvider;
import team.idealstate.hyper.command.api.execute.CommandExecutor;

import java.util.List;

/**
 * <p>Command</p>
 *
 * <p>创建于 2024/2/16 9:10</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Command {
    String[] EMPTY_ARGS = new String[0];

    @NotNull
    static String[] promise(@Nullable String[] args) {
        return args == null ? EMPTY_ARGS : args;
    }

    boolean isRoot();

    int getDepth();

    @NotNull
    String getDescription();

    @NotNull
    Command subCommand(@NotNull Command subCommand);

    @NotNull
    Command subCommands(@NotNull List<Command> subCommands);

    @Nullable ExampleProvider getExampleProvider();

    @NotNull
    Command exampleProvider(ExampleProvider exampleProvider);

    @Nullable ActionInterceptor getActionInterceptor();

    @NotNull
    Command actionInterceptor(ActionInterceptor actionInterceptor);

    @Nullable ArgumentAcceptor getArgumentAcceptor();

    @NotNull
    Command argumentAcceptor(ArgumentAcceptor argumentAcceptor);

    @Nullable CommandCompleter getCommandCompleter();

    @NotNull
    Command commandCompleter(CommandCompleter commandCompleter);

    @Nullable CommandExecutor getCommandExecutor();

    @NotNull
    Command commandExecutor(CommandExecutor commandExecutor);

    boolean acceptArgument(@NotNull CommandContext context, String[] args);

    @Nullable
    Command accept(@NotNull CommandContext context, String[] args);

    @Nullable
    List<String> complete(@NotNull CommandContext context, String[] args);

    @Nullable
    Boolean execute(@NotNull CommandContext context, String[] args);

    @Nullable
    Command getParent();

    void setParent(Command parent);
}
