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

package team.idealstate.hyper.command.impl.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import team.idealstate.hyper.command.api.Command;
import team.idealstate.hyper.command.api.CommandContext;
import team.idealstate.hyper.command.api.FastCommand;
import team.idealstate.hyper.command.impl.argument.AcceptorUtils;
import team.idealstate.hyper.command.impl.example.ExampleUtils;
import team.idealstate.hyper.command.impl.intercept.PreExecutionInterceptor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>HelloTest</p>
 *
 * <p>创建于 2024/2/16 19:24</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class HelloTest {

    private static final Logger logger = LogManager.getLogger(HelloTest.class);

    @Test
    public void helloCommand() {
        AtomicReference<String> result = new AtomicReference<>(null);
        Command root = FastCommand.root("root");
        root.subCommand(FastCommand.command("hello")
                .exampleProvider(ExampleUtils.singleton("hello"))
                .argumentAcceptor(AcceptorUtils.isEquals("hello"))
                .commandExecutor((context -> {
                    logger.info("hi");
                    result.set("hi");
                    return true;
                }))
                .subCommand(FastCommand.command("name")
                        .exampleProvider(ExampleUtils.collection(Arrays.asList("ketikai", "world", "fuckU")))
                        .argumentAcceptor(AcceptorUtils.isContains(Arrays.asList("ketikai", "world", "fuckU")))
                        .actionInterceptor(new PreExecutionInterceptor() {
                            @Override
                            public boolean doIntercept(@NotNull CommandContext context) {
                                String argument = context.getArgument();
                                if ("fuckU".equals(argument)) {
                                    logger.info("已拦截执行");
                                    result.set("已拦截执行");
                                    return true;
                                }
                                return false;
                            }
                        })
                        .commandExecutor((context -> {
                            logger.info("[{}]: hi", context.getArgument());
                            result.set("[" + context.getArgument() + "]: hi");
                            return true;
                        }))
                )
        );
        Iterator<String> scanner = Arrays.asList(
                "/hello", "/hello ketikai", "/hello world",
                "/hello fuckU", "/hello ", "/hello ketikai "
        ).iterator();
        Iterator<String> resultSet = Arrays.asList(
                "hi", "[ketikai]: hi", "[world]: hi", "已拦截执行", null, null
        ).iterator();
        while (scanner.hasNext()) {
            String input = scanner.next();
            logger.info("\n----------------------------");
            if (!input.startsWith("/") || input.length() == 1) {
                throw new IllegalArgumentException("无效的输入");
            }

            String validResult;
            try {
                validResult = resultSet.next();
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("测试用例的数量和有效结果的数量不相同");
            }

            String[] args = input.substring(1).split(" ", -1);
            logger.info("传入参数：{}", Arrays.toString(args));
            List<String> completed = FastCommand.complete(root, args);
            if (completed != null) {
                logger.info("补全示例：{}", completed);
            } else {
                logger.info("补全示例：无");
            }
            Boolean executed = FastCommand.execute(root, args);
            if (executed == null) {
                logger.warn("无效的命令");
                if (!Objects.equals(result.get(), validResult)) {
                    throw new IllegalStateException("预期结果应为 " + validResult);
                }
            } else {
                if (executed) {
                    logger.info("执行成功");
                    if (!Objects.equals(result.get(), validResult)) {
                        throw new IllegalStateException("预期结果应为 " + validResult);
                    }
                } else {
                    logger.warn("执行失败");
                    if (!Objects.equals(result.get(), validResult)) {
                        throw new IllegalStateException("预期结果应为 " + validResult);
                    }
                }
            }
            result.set(null);
        }
    }
}
