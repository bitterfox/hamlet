/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.github.bitterfox.hamlet;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class LanguageUtil {
    /**
     * https://stackoverflow.com/a/21879031
     * @param f
     * @return
     */
    static String describeMethodReference(MyFunction<?, ?> f) {
        for (Class<?> cl = f.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(f);
                if(!(replacement instanceof SerializedLambda))
                    break;// custom interface implementation
                SerializedLambda l = (SerializedLambda) replacement;
                return l.getImplClass().replace('/', '.') + "::" + l.getImplMethodName();
            }
            catch (NoSuchMethodException e) {}
            catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }
        return "(unknown)";
    }


    static StackTraceElement findLocation() {
        for (StackTraceElement stackTrace : Thread.currentThread().getStackTrace()) {
            if (!stackTrace.getClassName().equals(Thread.class.getName())
                && !stackTrace.getClassName().equals(LanguageUtil.class.getName())
                && !stackTrace.getClassName().equals(LetMatcher.class.getName())
                && !stackTrace.getClassName().equals(Hamlet.class.getName())
                && !stackTrace.getClassName().startsWith(HamletMatcherStage.class.getName())) {
                return stackTrace;
            }
        }
        return null;
    }
}
