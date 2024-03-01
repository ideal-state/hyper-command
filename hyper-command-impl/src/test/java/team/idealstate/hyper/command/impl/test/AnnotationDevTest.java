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
import team.idealstate.hyper.command.api.Command;
import team.idealstate.hyper.command.api.FastCommand;
import team.idealstate.hyper.command.api.framework.CommandHandler;
import team.idealstate.hyper.command.api.framework.annotation.RootCommand;
import team.idealstate.hyper.command.api.framework.annotation.SubCommand;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>AnnotationDevTest</p>
 *
 * <p>创建于 2024/2/29 14:57</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class AnnotationDevTest {

    private static final Logger logger = LogManager.getLogger(AnnotationDevTest.class);

    private static final AtomicReference<String> RESULT = new AtomicReference<>(null);

    public static void main(String[] args) {
        new AnnotationDevTest().testMyCommand();
    }

    //    @Test
    public void testMyCommand() {
        Command root = FastCommand.root("主命令");
        root.subCommand(FastCommand.command(MyCommand.class));
        Iterator<String> scanner = Arrays.asList(
                "/MyCommand say hello", "/MyCommand say", "/MyCommand say ",
                "/MyCommand say hi hi", "/MyCommand say hi", "/MyCommand sum 1 1",
                "/MyCommand sum 1 1 1", "/MyCommand sum 1 ", "/MyCommand sum"
        ).iterator();
        Iterator<String> resultSet = Arrays.asList(
                "hello", null, null, null, "hi", "2", null, null, null
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
                if (!Objects.equals(RESULT.get(), validResult)) {
                    throw new IllegalStateException("预期结果应为 " + validResult);
                }
            } else {
                if (executed) {
                    logger.info("执行成功");
                    if (!Objects.equals(RESULT.get(), validResult)) {
                        throw new IllegalStateException("预期结果应为 " + validResult);
                    }
                } else {
                    logger.warn("执行失败");
                    if (!Objects.equals(RESULT.get(), validResult)) {
                        throw new IllegalStateException("预期结果应为 " + validResult);
                    }
                }
            }
            RESULT.set(null);
        }
    }

    @RootCommand("MyCommand")
    public static class MyCommand implements CommandHandler {

        @SubCommand("say ${message}")
        public boolean say(String message) {
            logger.info("call say command: {}", message);
            RESULT.set(message);
            return true;
        }

        @SubCommand("sum ${first} ${second}")
        public boolean sum(Integer first, Integer second) {
            int result = first + second;
            logger.info("call sum command: {} + {} = {}", first, second, result);
            RESULT.set(String.valueOf(result));
            return true;
        }
    }
}
