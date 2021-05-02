# [&#35;javaHolidays](https://twitter.com/search?q=%23javaHolidays&src=typed_query&f=live)
1955年以降の日本の祝日、休日の判定や名称取得を行えるライブラリです。依存がないので気軽に利用できます。
「指定したの日の祝日の名称を取得する」「指定した日以降の最初の営業日を取得する」「指定した期間の祝日のリストを取得する」といったことが簡単に行えます。

内閣府の公開している情報を直接取得して、かつ定期的に更新してるため正確です。内閣府で公開している確定情報の範囲よりも後の日付については現行の法律をベースにしたアルゴリズムと国立天文台の情報を元に休祝日を推定します。

また「土日を祝日扱いにする」、「特定の日を祝休日扱いにする」、などの定義も簡単に行えるので事業等の実態に合わせた営業日の導出が行えます。

## ハッシュタグ
ご意見、ご感想などは [&#35;javaHolidays](https://twitter.com/intent/tweet?text=https://github.com/yusuke/holidays/+%23javaHolidays) を使ってツイートしていただければ幸いです。

[![@HolidaysJava](https://img.shields.io/twitter/url/https/twitter.com/HolidaysJava.svg?style=social&label=Follow%20%40HolidaysJava)](https://twitter.com/HolidaysJava)
## 動作要件
Java 8以降

## 利用方法
Maven Central Repositoryにリリースされているため、以下のように依存を指定するだけで利用出来るようになります。

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.samuraism/holidays/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.samuraism/holidays)

### Mavenの場合
```xml
<dependencies>
    <dependency>
        <groupId>com.samuraism</groupId>
        <artifactId>holidays</artifactId>
        <version>1.6</version>
    </dependency>
</dependencies>
```
### Gradleの場合
```text
dependencies {
    compile 'com.samuraism:holidays:1.6'
}
```
## 利用方法

###### サンプルコードを見れば使い方が一通り分かるようになっています。[com.samuraism.holidays.exmaple.JapaneseHolidaysExample](https://github.com/yusuke/holidays/blob/main/src/test/java/com/samuraism/holidays/exmaple/JapaneseHolidaysExample.java) は日本の休日、[com.samuraism.holidays.exmaple.UnitedStatesHolidaysExample](https://github.com/yusuke/holidays/blob/main/src/test/java/com/samuraism/holidays/exmaple/UnitedStatesHolidaysExample.java) はアメリカ合衆国の休日APIのサンプルとなっています。
```java
import com.samuraism.holidays.ja.日本の祝休日;
import com.samuraism.holidays.ja.祝休日;

import java.time.LocalDate;
import java.util.Optional;

public class Example {
    public static void main(String[] args) {
        日本の祝休日 holidays = new 日本の祝休日();

        // 元日なのでtrueが表示される
        System.out.println("2021年1月1日は祝日？: " + holidays.is祝休日(LocalDate.of(2021, 1, 1)));
        // 元日なのでfalseが表示される
        System.out.println("2021年1月1日は営業日？: " + holidays.is営業日(LocalDate.of(2021, 1, 1)));

        // 成人の日を取得
        Optional<祝休日> holiday = holidays.get祝休日(LocalDate.of(2021, 1, 11));
        holiday.ifPresent(e -> System.out.println("2021年1月11日は何の日？: " + e.名称));

        System.out.println("2021年5月の祝休日一覧: ");
        // 2021-05-03:憲法記念日、2021-05-04:みどりの日、2021-05-05:こどもの日 を表示
        holidays.get指定期間内の祝休日(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.日付 + ": " + e.名称));

        // 固定のカスタム祝休日を設定
        // メソッドチェーンで続けて書けるが、ミュータブルではなくオリジナルのインスタンスに変更が加わっていることに注意
        holidays.add祝休日(LocalDate.of(2013, 3, 29), "株式会社サムライズム設立")
                .add祝休日(日本の祝休日.土日休業)
                // ロジックベーのカスタム祝休日を設定。当該日が祝日ならば名称を、そうでなければnullを返す関数を指定する
                .add祝休日(e -> e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? "大晦日" : null);

        // 2021年1月最終営業日を取得→ 1月30日、31日が土日なので1月29日金曜日
        System.out.println("2021年最終営業日: " + holidays.最後の営業日(LocalDate.of(2021, 1, 31)));
        // 2020年大晦日以降最初の営業日を取得→ 1月1日は元日、1月2,3日はカスタム祝日(土日)なので1月4日月曜日
        System.out.println("2020年大晦日以降最初の営業日: " + holidays.最初の営業日(LocalDate.of(2020, 12, 31)));
        // 2021年2月22日以降最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(holidays.最初の祝休日(LocalDate.of(2021, 2, 22)));
        // 2021年2月26日以前最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(holidays.最後の祝休日(LocalDate.of(2021, 2, 26)));
    }
}
```

```java
import com.samuraism.holidays.Holiday;
import com.samuraism.holidays.UnitedStatesHolidays;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static com.samuraism.holidays.UnitedStatesHolidays.*;

public class Example {
    public static void main(String[] args) {
        UnitedStatesHolidays holidays = new UnitedStatesHolidays(Locale.ENGLISH,
                NEW_YEARS_DAY,
                MARTIN_LUTHER_KING_JR_DAY,
                MEMORIAL_DAY,
                INDEPENDENCE_DAY,
                LABOR_DAY,
                VETERANS_DAY,
                THANKS_GIVING_DAY,
                CHRISTMAS_DAY);

        // prints true, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a holiday?: " + holidays.isHoliday(LocalDate.of(2021, 1, 1)));
        // prints false, because it's New Year's Day
        System.out.println("Is Jan, 1 2021 a business day?: " + holidays.isBusinessDay(LocalDate.of(2021, 1, 1)));

        // get Martin Luther King Jr. Day
        Optional<Holiday> holiday = holidays.getHoliday(LocalDate.of(2021, 1, 18));
        holiday.ifPresent(e -> System.out.println("What is Jan, 18 2021?: " + e.name));

        System.out.println("List of holidays in 2021: ");
        holidays.getHolidaysBetween️(LocalDate.of(2021, 1, 1)
                , LocalDate.of(2021, 12, 31))
                .forEach(e -> System.out.println(e.date + ": " + e.name));

        // sets a fixed custom Holiday
        // You can specify custom holidays using method chain. Note that the UnitedStatesHolidays instance is mutated upon each method call.
        holidays.addHoliday(LocalDate.of(1995, 5, 23), "Java public debut")
                .addHoliday(UnitedStatesHolidays.CLOSED_ON_SATURDAYS_AND_SUNDAYS)
                // Specify logic based custom holidays. returns a string if the day is a holiday
                .addHoliday(e -> e.getMonthValue() == 5 && e.getDayOfMonth() == 19 ? "James Gosling's birthday" : null);

        // Gets the last business day of Jan, 2021 → the answer is Jan 29 since Jan 30, 31 are weekend
        System.out.println("Last business day of Jan 2021: " + holidays.lastBusinessDay(LocalDate.of(2021, 1, 31)));
        // Gets the first business day on and after July 4, 2021 → the answer is July 6, 2021 because July 4 and 5 are the Independence day and it's substitute
        System.out.println("First business day on or after July 4, 2021: " + holidays.firstBusinessDay(LocalDate.of(2021, 7, 4)));
        // First holiday on and after Dec 20, 2021 →  Dec 24 (Christmas Day)
        System.out.println(holidays.firstHoliday(LocalDate.of(2021, 12, 20)));
        // Last holiday by Nov 12, 2021 →  Nov 11 (Veterans Day)
        System.out.println(holidays.lastHoliday(LocalDate.of(2021, 11, 12)));
    }
}
```
## カスタム祝日
add祝休日()メソッドにより独自に固定の、またはアルゴリズムベースの祝日を追加できます。
コード例にあるとおり、メソッドチェーンで続けて指定ができるので、特に定数としてコード中に定義するのに便利です。
メソッドチェーンで連ねて書く際、オリジナルのインスタンスに変更が加わっていることに注意してください。
場面毎に異なる祝休日セットが必要な場合は別にインスタンスを生成する必要があります。
```java
public class Example {
    // 土日は非営業日
    private final 日本の祝休日 HOLIDAYS = new 日本の祝休日()
            .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
            .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null);
}
```

## 祝日情報取得の仕組み
祝日の情報は[内閣府の祝日情報](https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html) に掲載されている [syukujitsu.csv](https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv) を利用しています。
起動時に読み込んだあと、負荷をかけないよう毎31日±5分毎(±5分の部分はランダム)に再読み込みを行います。

当該URLからCSVの読み込みを失敗した場合はリソースファイルに配置されている /syukujitsu.csv にフォールバックします。

祝日情報を記載したCSVファイルを読み込めるURLをシステムプロパティ SYUKUJITSU_URL に指定すれば独自の祝日情報を設定できます。

# ライセンス
Apache License Version 2.0

![Java CI with Gradle](https://github.com/yusuke/holidays/workflows/Java%20CI%20with%20Gradle/badge.svg)