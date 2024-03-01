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

package team.idealstate.hyper.command.api.framework;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.idealstate.hyper.command.api.CommandContext;
import team.idealstate.hyper.command.api.framework.convert.*;
import team.idealstate.hyper.commons.base.AssertUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>StandardConvertors</p>
 *
 * <p>创建于 2024/3/1 12:19</p>
 *
 * @author ketikai
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes"})
public abstract class StandardConvertors {

    private static final Map<Class, ArgumentConvertor> CONVERTORS;

    static {
        HashMap<Class, ArgumentConvertor> map = new HashMap<>(19);
        ByteConvertor byteConvertor = new ByteConvertor();
        map.put(byte.class, byteConvertor);
        map.put(Byte.class, byteConvertor);
        ShortConvertor shortConvertor = new ShortConvertor();
        map.put(short.class, shortConvertor);
        map.put(Short.class, shortConvertor);
        IntegerConvertor integerConvertor = new IntegerConvertor();
        map.put(int.class, integerConvertor);
        map.put(Integer.class, integerConvertor);
        LongConvertor longConvertor = new LongConvertor();
        map.put(long.class, longConvertor);
        map.put(Long.class, longConvertor);
        FloatConvertor floatConvertor = new FloatConvertor();
        map.put(float.class, floatConvertor);
        map.put(Float.class, floatConvertor);
        DoubleConvertor doubleConvertor = new DoubleConvertor();
        map.put(double.class, doubleConvertor);
        map.put(Double.class, doubleConvertor);
        BooleanConvertor booleanConvertor = new BooleanConvertor();
        map.put(boolean.class, booleanConvertor);
        map.put(Boolean.class, booleanConvertor);
        CharacterConvertor characterConvertor = new CharacterConvertor();
        map.put(char.class, characterConvertor);
        map.put(Character.class, characterConvertor);
        map.put(String.class, new StringConvertor());
        map.put(BigDecimal.class, new BigDecimalConvertor());
        map.put(BigInteger.class, new BigIntegerConvertor());
        CONVERTORS = Collections.unmodifiableMap(map);
    }

    @Nullable
    public static <R> R convert(@NotNull CommandContext context, @NotNull String argument, @NotNull Class<R> argumentType) throws Throwable {
        AssertUtils.notNull(context, "无效的命令上下文");
        AssertUtils.notBlank(argument, "无效的参数");
        AssertUtils.notNull(argumentType, "无效的参数类型");
        ArgumentConvertor<R> convertor = findArgumentConvertor(argumentType);
        if (convertor == null) {
            throw new IllegalArgumentException("不支持的参数类型: " + argumentType.getName());
        }
        return convertor.convert(context, argument);
    }

    @SuppressWarnings({"unchecked"})
    @Nullable
    public static <R> ArgumentConvertor<R> findArgumentConvertor(@NotNull Class<R> argumentType) {
        AssertUtils.notNull(argumentType, "无效的参数类型");
        return (ArgumentConvertor<R>) CONVERTORS.get(argumentType);
    }
}
