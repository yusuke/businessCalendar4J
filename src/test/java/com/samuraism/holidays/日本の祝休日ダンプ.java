package com.samuraism.holidays;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 来年以降の祝休日を向こう9年間計算してファイルにダンプ
 */
public class 日本の祝休日ダンプ {
    public static void main(String[] args) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        final LocalDate start = LocalDate.of(1955, 1, 1);
        final LocalDate now = LocalDate.now();
        final LocalDate end = LocalDate.of(now.getYear() + 9, 12, 31);
        final String shiftJisFileName = String.format("calculated/japan-holidays%s-%s-Shift_JIS.csv", start.format(formatter), end.format(formatter));
        final String utf8FileName = String.format("calculated/japan-holidays%s-%s-UTF8.csv", start.format(formatter), end.format(formatter));
        try (final BufferedWriter shiftJIS = Files.newBufferedWriter(Paths.get(shiftJisFileName), Charset.forName("Shift_JIS"));
             final BufferedWriter utf8 = Files.newBufferedWriter(Paths.get(utf8FileName), StandardCharsets.UTF_8)        ) {
            final String header = "国民の祝日・休日月日,国民の祝日・休日名称\n";
            shiftJIS.write(header);
            utf8.write(header);
            for (祝休日 holiday : new 日本の祝休日().get指定期間内の祝休日️(start, end)) {
                final String line = String.format("%s,%s\n", holiday.日付.format(dateTimeFormatter), holiday.名称);
                System.out.print(line);
                shiftJIS.write(line);
                utf8.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
