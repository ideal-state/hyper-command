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

package team.idealstate.hyper.command.impl.complete;

import team.idealstate.hyper.command.api.complete.CommandCompleter;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>CompleterUtils</p>
 *
 * <p>创建于 2024/2/11 17:14</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class CompleterUtils {

    private static final CommandCompleter DEFAULT = (context, examples) -> {
        if (examples.isEmpty()) {
            return examples;
        }
        String argument = context.getArgument();
        List<String> result = null;
        for (String example : examples) {
            if (example != null && example.startsWith(argument)) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.add(example);
            }
        }
        if (result == null) {
            return examples;
        }
        return result;
    };

    public static CommandCompleter defaultCompleter() {
        return DEFAULT;
    }
}
