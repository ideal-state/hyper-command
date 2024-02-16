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

package team.idealstate.hyper.command.impl.argument;

import team.idealstate.hyper.command.api.argument.ArgumentAcceptor;
import team.idealstate.hyper.commons.base.StringUtils;

import java.util.Collection;
import java.util.Collections;

/**
 * <p>AcceptorUtils</p>
 *
 * <p>创建于 2024/2/11 17:03</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class AcceptorUtils {

    private static final ArgumentAcceptor IS_INTEGRAL = (context) -> StringUtils.isIntegral(context.getArgument());
    private static final ArgumentAcceptor IS_NUMERIC = (context) -> StringUtils.isNumeric(context.getArgument());

    public static ArgumentAcceptor isIntegral() {
        return IS_INTEGRAL;
    }

    public static ArgumentAcceptor isNumeric() {
        return IS_NUMERIC;
    }

    public static ArgumentAcceptor isEquals(String string) {
        return ((context) -> context.getArgument().equals(string));
    }

    public static ArgumentAcceptor notEquals(String string) {
        return ((context) -> !context.getArgument().equals(string));
    }

    public static ArgumentAcceptor isEqualsIgnoreCase(String string) {
        return ((context) -> context.getArgument().equalsIgnoreCase(string));
    }

    public static ArgumentAcceptor notEqualsIgnoreCase(String string) {
        return ((context) -> !context.getArgument().equalsIgnoreCase(string));
    }

    public static ArgumentAcceptor isContains(Collection<String> strings) {
        Collection<String> recollectStrings = promise(strings);
        return ((context) -> recollectStrings.contains(context.getArgument()));
    }

    public static ArgumentAcceptor notContains(Collection<String> strings) {
        Collection<String> recollectStrings = promise(strings);
        return ((context) -> !recollectStrings.contains(context.getArgument()));
    }

    private static Collection<String> promise(Collection<String> strings) {
        return strings == null ? Collections.emptySet() : strings;
    }
}
