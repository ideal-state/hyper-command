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
import team.idealstate.hyper.command.spi.CommandContextFactory;
import team.idealstate.hyper.command.spi.CommandFactory;
import team.idealstate.hyper.commons.base.AssertUtils;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * <p>FastCommand</p>
 *
 * <p>创建于 2024/2/16 18:32</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class FastCommand {

    private static final CommandContextFactory COMMAND_CONTEXT_FACTORY;
    private static final CommandFactory COMMAND_FACTORY;

    static {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ServiceLoader<CommandContextFactory> commandContextFactoryServiceLoader =
                ServiceLoader.load(CommandContextFactory.class, contextClassLoader);
        Iterator<CommandContextFactory> commandContextFactoryIterator =
                commandContextFactoryServiceLoader.iterator();
        if (commandContextFactoryIterator.hasNext()) {
            COMMAND_CONTEXT_FACTORY = commandContextFactoryIterator.next();
        } else {
            throw new IllegalStateException("未找到 CommandContextFactory 的实现");
        }
        ServiceLoader<CommandFactory> commandFactoryServiceLoader =
                ServiceLoader.load(CommandFactory.class, contextClassLoader);
        Iterator<CommandFactory> commandFactoryIterator = commandFactoryServiceLoader.iterator();
        if (commandFactoryIterator.hasNext()) {
            COMMAND_FACTORY = commandFactoryIterator.next();
        } else {
            throw new IllegalStateException("未找到 CommandFactory 的实现");
        }
    }

    @NotNull
    public static CommandContext currentContext() {
        return COMMAND_CONTEXT_FACTORY.createCommandContext();
    }

    @NotNull
    public static Command root(String description) {
        return COMMAND_FACTORY.createRootCommand(description);
    }

    @NotNull
    public static Command command(String description) {
        return COMMAND_FACTORY.createCommand(description);
    }

    @Nullable
    public static List<String> complete(@NotNull Command command, String[] args) {
        AssertUtils.notNull(command, "无效的命令");
        args = Command.promise(args);
        CommandContext currentContext = currentContext();
        currentContext.reset();
        for (int i = 0; i < args.length; i++) {
            Command acceptedCommand = command.accept(currentContext, args);
            if (acceptedCommand == null) {
                break;
            }
            command = acceptedCommand;
        }
        List<String> completed = command.complete(currentContext, args);
        currentContext.reset();
        return completed;
    }

    @Nullable
    public static Boolean execute(@NotNull Command command, String[] args) {
        AssertUtils.notNull(command, "无效的命令");
        args = Command.promise(args);
        CommandContext currentContext = currentContext();
        currentContext.reset();
        for (int i = 0; i < args.length; i++) {
            command = command.accept(currentContext, args);
            if (command == null) {
                break;
            }
        }
        if (command == null) {
            currentContext.reset();
            return null;
        }
        Boolean executed = command.execute(currentContext, args);
        currentContext.reset();
        return executed;
    }
}
