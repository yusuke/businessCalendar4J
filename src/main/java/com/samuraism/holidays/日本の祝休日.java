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
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("NonAsciiCharacters")
public final class 日本の祝休日 {
    private final TreeMap<LocalDate, 祝休日> custom祝休日Map = new TreeMap<>();
    private final List<Function<LocalDate, String>> custom祝休日Logic = new ArrayList<>();

    public 日本の祝休日() {
        custom祝休日Logic.add(e -> e.getMonthValue() == 1 && e.getDayOfMonth() == 1 ? "元日" : null);
    }

    /**
     * ロジックベースの祝休日を追加。当該日が祝休日であれば名称を返す関数を指定する
     *
     * @param logic ロジック
     * @return このインスタンス
     */
    public 日本の祝休日 add祝休日(Function<LocalDate, String> logic) {
        custom祝休日Logic.add(logic);
        return this;
    }

    /**
     * 固定の祝休日を追加
     *
     * @param 日付 日付
     * @param 名称 名称
     * @return このインスタンス
     */
    public 日本の祝休日 add祝休日(LocalDate 日付, String 名称) {
        custom祝休日Map.put(日付, new 祝休日(日付, 名称));
        return this;
    }

    /**
     * 指定した日が祝休日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が祝休日であればtrue
     */
    public boolean is祝休日(LocalDate date) {
        return 祝休日Map.containsKey(date) || custom祝休日Map.containsKey(date) || custom祝休日Logic.stream().anyMatch(e -> e.apply(date) != null);
    }

    /**
     * 指定した日が営業日かどうかを判定する
     *
     * @param date 日付
     * @return 指定した日が営業日であればtrue
     */
    public boolean is営業日(LocalDate date) {
        return !is祝休日(date);
    }

    /**
     * 指定した日の祝休日を返す。
     *
     * @param date 日付
     * @return 祝日・休日
     */
    public Optional<祝休日> get祝休日(LocalDate date) {
        祝休日 holiday = 祝休日Map.getOrDefault(date, custom祝休日Map.get(date));
        if (holiday != null) {
            return Optional.of(holiday);
        }
        return custom祝休日Logic.stream()
                .map(e -> e.apply(date)).filter(Objects::nonNull).findFirst()
                .flatMap(e -> Optional.of(new 祝休日(date, e)));
    }

    /**
     * 指定した日以前で営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 指定した日以前の営業日
     */
    public LocalDate 以前の営業日(LocalDate date) {
        LocalDate check = date;
        while (is祝休日(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * 指定した日以降で営業日(祝休日ではない日)を返す
     *
     * @param date 指定日
     * @return 指定した日以降の営業日
     */
    public LocalDate 以降の営業日(LocalDate date) {
        LocalDate check = date;
        while (is祝休日(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        return check;
    }

    /**
     * 指定した日以前の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public 祝休日 以前の祝休日(LocalDate date) {
        LocalDate check = date;
        while (!is祝休日(check)) {
            check = check.minus(1, ChronoUnit.DAYS);
        }
        //noinspection OptionalGetWithoutIsPresent
        return get祝休日(check).get();
    }

    /**
     * 指定した日以降の祝休日を返す
     *
     * @param date 指定日
     * @return 指定した日以前の祝休日
     */
    public 祝休日 以降の祝休日(LocalDate date) {
        LocalDate check = date;
        while (!is祝休日(check)) {
            check = check.plus(1, ChronoUnit.DAYS);
        }
        //noinspection OptionalGetWithoutIsPresent
        return get祝休日(check).get();
    }

    /**
     * 指定期間内の祝休日のリストを返す。リストは古い日から並べられている。指定期間内に祝休日がない場合は空のリストを返す。
     *
     * @param 開始日 指定開始日。この日も含む。
     * @param 終了日 指定終了日。この日も含む。
     * @return 指定期間内の祝休日のリスト。
     */
    public List<祝休日> get指定期間内の祝休日️(LocalDate 開始日, LocalDate 終了日) {
        List<祝休日> list = new ArrayList<>();
        LocalDate from = 開始日.isBefore(終了日) ? 開始日 : 終了日;
        LocalDate to = (終了日.isAfter(開始日) ? 終了日 : 開始日).plus(1, ChronoUnit.DAYS);
        while (from.isBefore(to)) {
            final Optional<祝休日> holiday = get祝休日(from);
            holiday.ifPresent(list::add);
            from = from.plus(1, ChronoUnit.DAYS);
        }
        return list;
    }

    private static TreeMap<LocalDate, 祝休日> 祝休日Map;
    private static final long 約一ヶ月 = 1000L * 60 * 60 * 24 * 31 + new Random(System.currentTimeMillis()).nextLong() % (1000L * 60 * 60 * 10);

    static {
        祝休日情報をロード();
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                祝休日情報をロード();
            }
        }, 0, 約一ヶ月);
    }

    /**
     * 祝日情報を読み込む。
     */
    private static void 祝休日情報をロード() {
        try {
            final URLConnection con = new URL(System.getProperty("SYUKUJITSU_URL", "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv")).openConnection();
            con.setConnectTimeout(30000);
            con.setReadTimeout(5000);
            load(con.getInputStream());
        } catch (IOException ignored) {
            // www8.cao.go.jpの読み込みに失敗している
            try {
                load(日本の祝休日.class.getResourceAsStream("/syukujitsu.csv"));
            } catch (IOException ignored1) {
            }
        }
    }

    private static void load(InputStream is) throws IOException {
        final TreeMap<LocalDate, 祝休日> holidayMap = new TreeMap<>();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(20000);
        byte[] buf = new byte[1024];
        int length;
        while (-1 != (length = is.read(buf))) {
            baos.write(buf, 0, length);
        }
        //noinspection StringOperationCanBeSimplified
        String result = new String(baos.toByteArray(), Charset.forName("Shift_JIS"));
        Arrays.stream(result.split("\n")).forEach(line -> {
            if (!line.contains("国民の祝日・休日名称")) {
                final String[] split = line.split(",");
                final LocalDate date = LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy/M/d"));
                holidayMap.put(date, new 祝休日((date), split[1].trim()));
            }
        });
        祝休日Map = holidayMap;
    }

}
