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

import org.jetbrains.annotations.NotNull;
import team.idealstate.hyper.command.api.Command;
import team.idealstate.hyper.command.impl.CommandImpl;
import team.idealstate.hyper.command.spi.CommandFactory;

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
}
