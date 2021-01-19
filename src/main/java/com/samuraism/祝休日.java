/*
   Copyright 2021 the original author or authors.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.samuraism;

import java.time.LocalDate;
import java.util.Objects;

@SuppressWarnings("NonAsciiCharacters")
public final class 祝休日 implements Comparable<祝休日> {
    public final LocalDate 日付;
    public final String 名称;

    public 祝休日(LocalDate 日付, String 名称) {
        this.日付 = 日付;
        this.名称 = 名称;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        祝休日 祝休日 = (祝休日) o;
        return 日付.equals(祝休日.日付) && 名称.equals(祝休日.名称);
    }

    @Override
    public int hashCode() {
        return Objects.hash(日付, 名称);
    }

    @Override
    public String toString() {
        return "祝休日{" +
                "日付=" + 日付 +
                ", 名称='" + 名称 + '\'' +
                '}';
    }

    @Override
    public int compareTo(祝休日 o) {
        return this.日付.compareTo(o.日付);
    }
}
