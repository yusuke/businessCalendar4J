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
package one.cafebabe.businesscalendar4j;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

class CSVHolidays extends HolidayMap {
    private final String resourceURL;
    private final String fallbackResource;
    private final String prefix;
    private final Charset charset;

    CSVHolidays(long interval, @NotNull String resourceURL, @NotNull String fallbackResource, @NotNull  String prefix,
                @NotNull Charset charset) {
        this.resourceURL = resourceURL;
        this.fallbackResource = fallbackResource;
        this.prefix = prefix;
        this.charset = charset;
        loadHolidays();
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                loadHolidays();
            }
        }, interval, interval);
    }

    /**
     * Load holiday information
     */
    private void loadHolidays() {
        try {
            final URLConnection con = new URL(resourceURL).openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(5000);
            holidayMap = load(con.getInputStream(), prefix, charset);
        } catch (IOException e) {
            // failed to load resourceURL
            try {
                holidayMap = load(Objects.requireNonNull(Japan.class.getResourceAsStream(fallbackResource)), prefix, charset);
            } catch (IOException ignored1) {
            }
        }
    }

    static TreeMap<LocalDate, String> load(InputStream is, String prefix, Charset charset) throws IOException {
        final TreeMap<LocalDate, String> holidayMap = new TreeMap<>();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(20000);
        byte[] buf = new byte[1024];
        int length;
        while (-1 != (length = is.read(buf))) {
            outputStream.write(buf, 0, length);
        }
        String result = new String(outputStream.toByteArray(), charset);
        boolean firstLine = true;
        for (String line : result.split("\n")) {
            if (firstLine) {
                firstLine = false;
            }else{
                final String[] split = line.split(",");
                final LocalDate date = LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy/M/d"));
                String holidayName = split[1].trim();
                holidayMap.put(date, prefix + holidayName);
            }
        }
        return holidayMap;
    }
}

