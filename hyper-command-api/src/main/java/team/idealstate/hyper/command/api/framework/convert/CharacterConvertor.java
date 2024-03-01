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

package team.idealstate.hyper.command.api.framework.convert;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.command.api.CommandContext;
import team.idealstate.hyper.command.api.framework.ArgumentConvertor;
import team.idealstate.hyper.commons.base.AssertUtils;

/**
 * <p>CharacterConvertor</p>
 *
 * <p>创建于 2024/3/1 12:04</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public final class CharacterConvertor implements ArgumentConvertor<Character> {
    @Override
    public @NotNull Character convert(@NotNull CommandContext context, @NotNull String argument) throws Throwable {
        AssertUtils.notNull(context, "无效的命令上下文");
        AssertUtils.notBlank(argument, "无效的参数");
        if (argument.length() != 1) {
            throw new IllegalArgumentException("参数长度必须为 1");
        }
        return argument.charAt(0);
    }
}