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

import java.util.Set;

/**
 * <p>CommandContext</p>
 *
 * <p>创建于 2024/2/16 9:43</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public interface CommandContext {

    @Nullable
    Object put(@NotNull String key, @Nullable Object value);

    @Nullable
    Object remove(@NotNull String key);

    @Nullable <T> T remove(@NotNull String key, @NotNull Class<T> valueType);

    @NotNull
    Set<String> getKeys();

    boolean hasKey(@NotNull String key);

    boolean hasValue(@NotNull String key);

    @Nullable
    Object getValue(@NotNull String key);

    @Nullable <T> T getValue(@NotNull String key, @NotNull Class<T> valueType);

    void clear();

    void reset();

    @NotNull
    String getArgument();

    @NotNull
    String[] getArguments();

    void setArguments(String[] arguments);

    int getDepth();

    void setDepth(int depth);
}
