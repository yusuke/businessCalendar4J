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
package com.samuraism.holidays;

import java.time.LocalDate;
import java.util.TreeMap;
import java.util.function.Function;

class Map祝休日 implements Function<LocalDate, String> {
    /* アルゴリズムのテストで空にするので package private */
    TreeMap<LocalDate, String> 祝休日Map;

    Map祝休日(){
        // リソースURLから読み込まない場合
        祝休日Map = new TreeMap<>();
    }

    @Override
    public String apply(LocalDate localDate) {
        return 祝休日Map.get(localDate);
    }

    void add祝休日(LocalDate date, String 名称) {
        祝休日Map.put(date, 名称);
    }
}
