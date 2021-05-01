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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

class CSVHolidays extends HolidayMap {
    private final String リソースURL;
    private final ResourceBundle resource;

    CSVHolidays(long ロード間隔, String リソースURL, ResourceBundle resource){
        // リソースURLから読み込む場合
        this.リソースURL = リソースURL;
        this.resource = resource;
        祝休日情報をロード();
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                祝休日情報をロード();
            }
        }, ロード間隔, ロード間隔);
    }
    /**
     * 祝日情報を読み込む。
     */
    private void 祝休日情報をロード() {
        try {
            final URLConnection con = new URL(リソースURL).openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(5000);
            holidayMap = load(con.getInputStream(), resource);
        } catch (IOException ignored) {
            // www8.cao.go.jpの読み込みに失敗している
            try {
                holidayMap = load(JapaneseHolidays.class.getResourceAsStream("/syukujitsu.csv"), resource);
            } catch (IOException ignored1) {
            }
        }
    }

    static TreeMap<LocalDate, String> load(InputStream is, ResourceBundle resource) throws IOException {
        final TreeMap<LocalDate, String> holidayMap = new TreeMap<>();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(20000);
        byte[] buf = new byte[1024];
        int length;
        while (-1 != (length = is.read(buf))) {
            outputStream.write(buf, 0, length);
        }
        String result = new String(outputStream.toByteArray(), Charset.forName("Shift_JIS"));
        Arrays.stream(result.split("\n")).forEach(line -> {
            if (!line.contains("国民の祝日・休日名称")) {
                final String[] split = line.split(",");
                final LocalDate date = LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy/M/d"));
                String holidayName = split[1].trim();
                try {
                    holidayName = resource.getString(split[1].trim());
                }catch(MissingResourceException ignore){}
                holidayMap.put(date, holidayName);
            }
        });
        return holidayMap;
    }
}

