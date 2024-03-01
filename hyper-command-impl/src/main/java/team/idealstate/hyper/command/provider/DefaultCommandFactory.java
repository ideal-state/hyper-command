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

package team.idealstate.hyper.command.provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.command.api.Command;
import team.idealstate.hyper.command.api.FastCommand;
import team.idealstate.hyper.command.api.framework.ArgumentConvertor;
import team.idealstate.hyper.command.api.framework.CommandHandler;
import team.idealstate.hyper.command.api.framework.annotation.RootCommand;
import team.idealstate.hyper.command.api.framework.annotation.SubCommand;
import team.idealstate.hyper.command.impl.CommandImpl;
import team.idealstate.hyper.command.impl.argument.AcceptorUtils;
import team.idealstate.hyper.command.impl.example.ExampleUtils;
import team.idealstate.hyper.command.spi.CommandFactory;
import team.idealstate.hyper.commons.base.AssertUtils;
import team.idealstate.hyper.commons.base.StringUtils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>DefaultCommandFactory</p>
 *
 * <p>创建于 2024/2/16 20:21</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class DefaultCommandFactory implements CommandFactory {

    private static final Logger logger = LogManager.getLogger(DefaultCommandFactory.class);

    @Override
    public @NotNull Command createRootCommand(String description) {
        CommandImpl command = new CommandImpl(description);
        command.setParent(null);
        return command;
    }

    @Override
    public @NotNull Command createCommand(String description) {
        return new CommandImpl(description);
    }

    private static boolean isToken(@NotNull String token) {
        return token.startsWith("${") && token.endsWith("}");
    }

    private static String token(@NotNull String tokenName) {
        return "${" + tokenName + "}";
    }

    private static String tokenName(@NotNull String token) {
        return token.substring(2, token.length() - 1);
    }

    @Override
    public @NotNull Command createCommand(@NotNull Class<? extends CommandHandler> commandHandlerClass) {
        AssertUtils.notNull(commandHandlerClass, "无效的命令处理器类型");
        RootCommand rootCommand = commandHandlerClass.getDeclaredAnnotation(RootCommand.class);
        String commandHandlerClassName = commandHandlerClass.getName();
        if (rootCommand == null) {
            throw new IllegalStateException("未标记 @RootCommand 的命令处理器类型 " + commandHandlerClassName);
        }

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CommandHandler commandHandler;
        try {
            MethodHandle constructor = lookup.findConstructor(commandHandlerClass, MethodType.methodType(void.class));
            commandHandler = (CommandHandler) constructor.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        String rootDescription = rootCommand.value();
        Command root = FastCommand.command(rootDescription)
                .exampleProvider(ExampleUtils.singleton(rootDescription))
                .argumentAcceptor(AcceptorUtils.isEquals(rootDescription));
        for (Method method : commandHandlerClass.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            SubCommand subCommand = method.getDeclaredAnnotation(SubCommand.class);
            if (subCommand == null) {
                continue;
            }
            String expression = subCommand.value();
            if (StringUtils.isBlank(expression)) {
                continue;
            }
            if (!boolean.class.equals(method.getReturnType())) {
                logger.error("无效的子命令方法返回值 {}#{}", commandHandlerClassName, method.getName());
                continue;
            }
            Parameter[] parameters = method.getParameters();
            Map<String, Parameter> parameterMap = new HashMap<>(parameters.length);
            for (Parameter parameter : parameters) {
                parameterMap.put(parameter.getName(), parameter);
            }

            Command parent = null;
            String[] arguments = expression.split(" ", -1);
            for (String token : arguments) {
                Command command = FastCommand.command(token);
                if (parent == null) {
                    root.subCommand(command);
                } else {
                    parent.subCommand(command);
                }
                parent = command;
                if (isToken(token)) {
                    String tokenName = tokenName(token);
                    command.argumentAcceptor(context -> {
                        String argument = context.getArgument();
                        Parameter parameter = parameterMap.get(tokenName);
                        if (parameter != null) {
                            Class<?> type = parameter.getType();
                            ArgumentConvertor<?> argumentConvertor = commandHandler.findArgumentConvertor(type);
                            String typeName = type.getName();
                            if (argumentConvertor == null) {
                                logger.debug("[Command]({}) 参数转换：未找到与参数类型 {} 匹配的转换器", token, typeName);
                                return false;
                            }
                            Object converted;
                            try {
                                converted = argumentConvertor.convert(context, argument);
                            } catch (Throwable e) {
                                logger.debug("[Command]({}) 参数转换：无法将参数值 '{}' 转换为 {} 类型值", token, argument, typeName);
                                logger.debug("catching", e);
                                return false;
                            }
                            context.put(token, converted);
                            return true;
                        }
                        logger.debug("[Command]({}) 参数转换：未找到与命令参数 '{}' 匹配的方法参数", token, tokenName);
                        return false;
                    });
                } else {
                    command.exampleProvider(ExampleUtils.singleton(token))
                            .argumentAcceptor(AcceptorUtils.isEquals(token));
                }
            }
            if (parent == null) {
                throw new IllegalStateException("无效的子命令表达式 " + expression);
            }
            MethodHandle methodHandle;
            try {
                methodHandle = lookup.unreflect(method).bindTo(commandHandler);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            parent.commandExecutor(context -> {
                Object[] argumentObjects = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    argumentObjects[i] = context.getValue(token(parameter.getName()), parameter.getType());
                }
                try {
                    return (boolean) methodHandle.invokeWithArguments(argumentObjects);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return root;
    }
}
