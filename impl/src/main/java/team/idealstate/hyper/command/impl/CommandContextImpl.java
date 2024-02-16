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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.hyper.command.api.Command;
import team.idealstate.hyper.command.api.CommandContext;
import team.idealstate.hyper.commons.base.AssertUtils;

import java.util.*;

/**
 * <p>CommandContextImpl</p>
 *
 * <p>
 * 这个实现是一个简单的命令上下文实现，它并不是并发安全的（话虽如此，但通常情况下它不会在并发环境下使用）。
 * </p>
 *
 * <p>创建于 2024/2/16 16:55</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandContextImpl implements CommandContext {

    private final Map<String, Object> valueMap = new LinkedHashMap<>(16, 0.6F);
    private String[] arguments = Command.EMPTY_ARGS;
    private int depth = -1;

    @Override
    public @Nullable Object put(@NotNull String key, @NotNull Object value) {
        AssertUtils.notBlank(key, "无效的键");
        AssertUtils.notNull(value, "无效的值");
        return valueMap.put(key, value);
    }

    @Override
    public @Nullable Object remove(@NotNull String key) {
        AssertUtils.notBlank(key, "无效的键");
        return valueMap.remove(key);
    }

    @Override
    public <T> @Nullable T remove(@NotNull String key, @NotNull Class<T> valueType) {
        AssertUtils.notBlank(key, "无效的键");
        AssertUtils.notNull(valueType, "无效的值类型");
        if (valueMap.containsKey(key)) {
            if (valueType.isInstance(valueMap.get(key))) {
                return valueType.cast(valueMap.remove(key));
            }
        }
        return null;
    }

    @Override
    public @NotNull Set<String> getKeys() {
        if (valueMap.isEmpty()) {
            return Collections.emptySet();
        }
        return new LinkedHashSet<>(valueMap.keySet());
    }

    @Override
    public boolean hasKey(@NotNull String key) {
        AssertUtils.notBlank(key, "无效的键");
        return valueMap.containsKey(key);
    }

    @Override
    public boolean hasValue(@NotNull String key) {
        AssertUtils.notBlank(key, "无效的键");
        return valueMap.get(key) != null;
    }

    @Override
    public @Nullable Object getValue(@NotNull String key) {
        AssertUtils.notBlank(key, "无效的键");
        return valueMap.get(key);
    }

    @Override
    public <T> @Nullable T getValue(@NotNull String key, @NotNull Class<T> valueType) {
        AssertUtils.notBlank(key, "无效的键");
        AssertUtils.notNull(valueType, "无效的值类型");
        if (valueMap.containsKey(key)) {
            Object valueObject = valueMap.get(key);
            if (valueType.isInstance(valueObject)) {
                return valueType.cast(valueObject);
            }
        }
        return null;
    }

    @Override
    public @NotNull String getArgument() {
        return arguments[depth];
    }

    @Override
    public void clear() {
        valueMap.clear();
    }

    @Override
    public void reset() {
        clear();
        this.arguments = Command.EMPTY_ARGS;
        this.depth = -1;
    }

    @Override
    public @NotNull String[] getArguments() {
        return arguments;
    }

    @Override
    public void setArguments(String[] arguments) {
        this.arguments = Command.promise(arguments);
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public void setDepth(int depth) {
        if (depth < 0 || depth >= arguments.length) {
            throw new IllegalArgumentException("depth 的值必须介于 0 和 " + arguments.length + "之间");
        }
        this.depth = depth;
    }
}
