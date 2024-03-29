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

package team.idealstate.hyper.command.impl.intercept;

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.command.api.CommandContext;
import team.idealstate.hyper.command.api.action.ActionInterceptor;
import team.idealstate.hyper.command.api.action.CommandAction;

/**
 * <p>PreCompletionInterceptor</p>
 *
 * <p>创建于 2024/2/16 19:34</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class PreCompletionInterceptor implements ActionInterceptor {
    @Override
    public boolean intercept(@NotNull CommandContext context, @NotNull CommandAction action) {
        if (CommandAction.COMPLETE.equals(action)) {
            return doIntercept(context);
        }
        return false;
    }

    public abstract boolean doIntercept(@NotNull CommandContext context);
}
