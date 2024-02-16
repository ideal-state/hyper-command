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

package team.idealstate.hyper.command.impl.example;

import team.idealstate.hyper.command.api.example.ExampleProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>ExampleProviderUtils</p>
 *
 * <p>创建于 2024/2/11 17:25</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class ExampleUtils {

    public static ExampleProvider singleton(String string) {
        return ((context) -> Collections.singletonList(string));
    }

    public static ExampleProvider collection(Collection<String> strings) {
        List<String> recollectStrings = promise(strings);
        return ((context) -> recollectStrings);
    }

    private static List<String> promise(Collection<String> strings) {
        if (strings instanceof List) {
            return (List<String>) strings;
        }
        return strings == null ? Collections.emptyList() : new ArrayList<>(strings);
    }
}
