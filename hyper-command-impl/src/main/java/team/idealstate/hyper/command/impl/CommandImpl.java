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

package team.idealstate.hyper.command.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.hyper.command.api.Command;
import team.idealstate.hyper.command.api.CommandContext;
import team.idealstate.hyper.command.api.action.ActionInterceptor;
import team.idealstate.hyper.command.api.action.CommandAction;
import team.idealstate.hyper.command.api.argument.ArgumentAcceptor;
import team.idealstate.hyper.command.api.complete.CommandCompleter;
import team.idealstate.hyper.command.api.example.ExampleProvider;
import team.idealstate.hyper.command.api.execute.CommandExecutor;
import team.idealstate.hyper.command.impl.complete.CompleterUtils;
import team.idealstate.hyper.commons.base.AssertUtils;
import team.idealstate.hyper.commons.base.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>CommandImpl</p>
 *
 * <p>创建于 2024/2/16 16:51</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandImpl implements Command {

    private static final Logger logger = LogManager.getLogger(CommandImpl.class);
    private final String description;
    private final List<Command> subCommands = new LinkedList<>();
    private Command parent = null;
    private boolean parentHasBeenSet = false;
    private ExampleProvider exampleProvider;
    private ActionInterceptor actionInterceptor;
    private ArgumentAcceptor argumentAcceptor;
    private CommandCompleter commandCompleter;
    private CommandExecutor commandExecutor;

    public CommandImpl(@NotNull String description) {
        this(description, true);
    }

    public CommandImpl(@NotNull String description, boolean useDefaultCompleter) {
        AssertUtils.notBlank(description, "无效的描述");
        this.description = description;
        if (useDefaultCompleter) {
            this.commandCompleter = CompleterUtils.defaultCompleter();
        }
    }

    protected static boolean intercept(@NotNull Command command, @NotNull CommandContext context, @NotNull CommandAction action, String[] args) {
        AssertUtils.notNull(command, "无效的命令");
        AssertUtils.notNull(context, "无效的命令上下文");
        AssertUtils.notNull(action, "无效的命令动作");
        args = Command.promise(args);
        boolean result = false;
        int depth = command.getDepth();
        if (args.length > depth) {
            ActionInterceptor actionInterceptor = command.getActionInterceptor();
            result = actionInterceptor != null && actionInterceptor.intercept(context, action);
        }
        if (result) {
            logger.trace("[Command]({}) 拦截操作：{}，参数长度：{}，命令深度：{}",
                    command.getDescription(), args[depth], args.length, depth);
        } else {
            logger.trace("[Command]({}) 放行操作：{}，参数长度：{}，命令深度：{}",
                    command.getDescription(), args[depth], args.length, depth);
        }
        return result;
    }

    @Override
    public @Nullable Command getParent() {
        return parent;
    }

    @Override
    public void setParent(Command parent) {
        if (parentHasBeenSet) {
            throw new IllegalStateException("parent 的值仅允许配置一次");
        }
        this.parentHasBeenSet = true;
        this.parent = parent;
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public int getDepth() {
        int depth = 0;
        Command command = parent;
        while (command != null) {
            if (command.isRoot()) {
                break;
            }
            depth++;
            command = command.getParent();
        }
        return depth;
    }

    @Override
    public @NotNull String getDescription() {
        return description;
    }

    @Override
    public @NotNull Command subCommand(@NotNull Command subCommand) {
        AssertUtils.notNull(subCommand, "无效的子命令");
        subCommand.setParent(this);
        subCommands.add(subCommand);
        return this;
    }

    @Override
    public @NotNull Command subCommands(@NotNull List<Command> subCommands) {
        AssertUtils.notNull(subCommands, "无效的子命令列表");
        subCommands.forEach(subCommand -> {
            AssertUtils.notNull(subCommand, "无效的子命令");
            subCommand.setParent(this);
            this.subCommands.add(subCommand);
        });
        return this;
    }

    @Nullable
    @Override
    public ExampleProvider getExampleProvider() {
        return exampleProvider;
    }

    @Override
    public @NotNull Command exampleProvider(ExampleProvider exampleProvider) {
        this.exampleProvider = exampleProvider;
        return this;
    }

    @Nullable
    @Override
    public ActionInterceptor getActionInterceptor() {
        return actionInterceptor;
    }

    @Override
    public @NotNull Command actionInterceptor(ActionInterceptor actionInterceptor) {
        this.actionInterceptor = actionInterceptor;
        return this;
    }

    @Nullable
    @Override
    public ArgumentAcceptor getArgumentAcceptor() {
        return argumentAcceptor;
    }

    @Override
    public @NotNull Command argumentAcceptor(ArgumentAcceptor argumentAcceptor) {
        this.argumentAcceptor = argumentAcceptor;
        return this;
    }

    @Nullable
    @Override
    public CommandCompleter getCommandCompleter() {
        return commandCompleter;
    }

    @Override
    public @NotNull Command commandCompleter(CommandCompleter commandCompleter) {
        this.commandCompleter = commandCompleter;
        return this;
    }

    @Nullable
    @Override
    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    @Override
    public @NotNull Command commandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    public boolean acceptArgument(@NotNull CommandContext context, String[] args) {
        AssertUtils.notNull(context, "无效的命令上下文");
        args = Command.promise(args);
        int depth = getDepth();
        if (args.length > depth && !StringUtils.isBlank(args[depth])) {
            context.setArguments(args);
            context.setDepth(depth);
            boolean result = argumentAcceptor != null && argumentAcceptor.acceptArgument(context);
            if (result) {
                logger.trace("[Command]({}) 参数命中：{}，参数长度：{}，命令深度：{}",
                        getDescription(), args[depth], args.length, depth);
                return true;
            }
            logger.trace("[Command]({}) 参数无效：{}，参数长度：{}，命令深度：{}",
                    getDescription(), args[depth], args.length, depth);
        }
        return false;
    }

    @Override
    public @Nullable Command accept(@NotNull CommandContext context, String[] args) {
        AssertUtils.notNull(context, "无效的命令上下文");
        args = Command.promise(args);
        if (args.length > getDepth()) {
            for (Command subCommand : subCommands) {
                if (subCommand.acceptArgument(context, args)) {
                    return subCommand;
                }
            }
        }
        return null;
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandContext context, String[] args) {
        AssertUtils.notNull(context, "无效的命令上下文");
        args = Command.promise(args);
        int depth = getDepth();
        if (!isRoot()) {
            depth += 1;
        }
        List<String> result = null;
        if (args.length - 1 == depth) {
            List<String> examples = null;
            context.setArguments(args);
            context.setDepth(depth);
            for (Command subCommand : subCommands) {
                ExampleProvider exampleProvider = subCommand.getExampleProvider();
                if (exampleProvider == null) {
                    continue;
                }
                if (intercept(subCommand, context, CommandAction.COMPLETE, args)) {
                    continue;
                }
                List<String> subExamples = exampleProvider.provideExample(context);
                AssertUtils.notNull(subExamples, "无效的示例列表");
                if (subExamples.isEmpty()) {
                    continue;
                }
                if (examples == null) {
                    examples = new LinkedList<>();
                }
                CommandCompleter completer = subCommand.getCommandCompleter();
                if (completer == null || !StringUtils.isBlank(args[depth])) {
                    examples.addAll(subExamples);
                    continue;
                }
                examples.addAll(completer.complete(context, subExamples));
            }
            if (examples != null) {
                logger.trace("[Command]({}) 补全命令：{} 项", getDescription(), examples.size());
                result = CompleterUtils.defaultCompleter().complete(context, examples);
            }
        }
        return result;
    }

    @Override
    public @Nullable Boolean execute(@NotNull CommandContext context, String[] args) {
        AssertUtils.notNull(context, "无效的命令上下文");
        args = Command.promise(args);
        int depth = getDepth();
        if (args.length - 1 == depth) {
            CommandExecutor executor = getCommandExecutor();
            if (executor != null) {
                context.setArguments(args);
                context.setDepth(depth);
                if (!intercept(this, context, CommandAction.EXECUTE, args)) {
                    logger.trace("[Command]({}) 执行命令", getDescription());
                    return executor.execute(context);
                }
            }
        }
        return null;
    }
}
