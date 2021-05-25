# [&#35;businessCalendar4J](https://twitter.com/search?q=%23businessCalendar4J&src=typed_query&f=live)

BusinessCalendar4J は100% pure Javaのビジネスカレンダーライブラリです。柔軟に祝休日や営業時間を定義出来ます。またアメリカ合衆国と日本の祝休日も定義されています。他の国の祝休日サポートが必要ですか？コントリビュートをお願いします！

「指定した日の祝日名称を取得する」「指定した日以降の最初の営業日」「指定した期間内の祝休日のリスト」なども取得できます。

また [ビジネスカレンダーBuilder](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/ビジネスカレンダーBuilder.java) より固定の日、またはlambda式で柔軟に「土日を祝日扱いにする」、「特定の日を祝休日扱いにする」、などの定義が行えるので事業等の実態に合わせた営業日の導出が行えます。

日本の祝日については内閣府の公開している情報を直接取得して、かつ定期的に更新してるため正確です。内閣府で公開している確定情報の範囲よりも後の日付については現行の法律をベースにしたアルゴリズムと国立天文台の情報を元に休祝日を推定します。

[![@businessCal4J](https://img.shields.io/twitter/url/https/twitter.com/BusinessCal4J.svg?style=social&label=Follow%20%40BusinessCal4J)](https://twitter.com/businessCal4J)

## 動作要件
Java 8以降

## 依存の宣言
Maven Central Repositoryにリリースされているため、以下のように依存を指定するだけで利用出来るようになります。

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.samuraism/businessCalendar/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.samuraism/businessCalendar)

### Mavenの場合
```xml
<dependencies>
    <dependency>
        <groupId>com.samuraism</groupId>
        <artifactId>businessCalendar4j</artifactId>
        <version>1.18</version>
    </dependency>
</dependencies>
```
### Gradleの場合
```text
dependencies {
    compile 'com.samuraism:businessCalendar4j:1.18'
}
```

## 利用方法

以下は日本語APIの説明になります。英語版APIについては [こちら](https://github.com/yusuke/businessCalendar4J/blob/main/README.md) をご覧ください。

### 初期化

- 初期化構文
```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    // メソッドチェーンで祝休日や営業時間を設定
    .build();
```

祝休日、営業時間の指定はメソッドチェーンの順番に評価されます。

### 設定

#### 祝休日と営業時間を設定する

- 年月日または月日で固定の祝休日を設定する

```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    // 固定の1回のみの祝日
    .年月日(1995, 5, 23).祝休日("Java デビュー")
    // 毎年5月19日は休業
    .月日(5, 19).祝休日("James Gosling's birthday")
    .build();
```

- 曜日で休業日を指定する

```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    // 曜日固定の休業日
    .曜日(DayOfWeek.SUNDAY, DayOfWeek.Wednesday)
        .祝休日("毎週日曜、水曜は休業")
    // 第二月曜日は休業
    .曜日(2, DayOfWeek.Monday).祝休日("第二月曜日は休業")
    .build();
```

- 営業時間を曜日や月日で指定

```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    // 大晦日は10時〜12時、13時〜15時営業
    .月日(12, 31).営業時間("10 - 12, 13-15")
    // 土日は10時から12時、13時〜16時半営業
    .曜日(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        .営業時間("午前10時〜午前11時半, 13時〜午後4時半")
    // そのほかの営業日は9時〜18時営業
    .営業時間("9-18")
    .build();
```

指定のない日はデフォルトで24時間営業となります。

以下はすべて"夜中0時から午前8時半、9時〜正午、午後1半から午後5時、午後7時半から夜中0時"の営業時間を表す表現になります。

| 営業時間 |
| ---- |
| "0-8:30,9-12,13:30-17,19:31-24" |
| "0-8:30,9-12,13:30-17,19:31-0" |
| "0-8:30,9-12pm,1:30pm-5pm,7:31pm-12am" |
| "12 a.m.-8:30,9-12noon, 1:30pm-5pm, 7:31pm-12am" |
| "12 a.m. - 8:30, 9 - noon,1:30pm-5pm,7:31pm-12am" |
| "midnight12 - 8:30, 9- noon 12, 1:30pm-5pm,7:31pm-12 midnight" |
| "12 a.m. to 8:30, 9-12,1:30pm to 5pm, 7:31pm-12am" |
| "12 a.m. to 8:30 & 9-12,1:30pm to 5pm&7:31pm-12am" |
| "午前12時 から 午前8時半, 9-正午,午後1時半~午後5時、午後7:31〜午前0時" |

- 祝休日、営業時間をCSVで設定する

ファイルシステムから:

```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    .csv(Paths.get("holidays-business-hours.csv"))
    .build();
```

URLから:

```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    .csv(new URL("https://somewhere.example.com/holidays-business-hours.csv"))
    .build();
```


- 祝休日、営業時間をCSVファイルで設定し、1時間おきにリロードする

```java
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    .csv(Paths.get("holidays-business-hours.csv"), Duration.of(1, ChronoUnit.HOURS))
    .build();
```

- CSVファイルで祝休日、営業時間を設定し、任意のタイミングでリロードする

```java
CsvConfiguration conf = CsvConfiguration.getInstance(Paths.get("holidayconf.csv"));
ビジネスカレンダー calendar = ビジネスカレンダー.newBuilder()
    .csv(conf);
    .build();
.
.
.
// 必要なタイミングでリロードできます
conf.reload();
```

以下は設定ファイルの記述例です。

```text
# comment
# opens 24 hours on every 2nd Sunday
hours,2,sun,0-24
hours,sun,1-17,18-19
hours,mon,2-17
hours,tue,3-17
hours,wed,4-17
hours,thu,5-17
hours,fri,6-17
hours,sat,7-17
hours,sun,8-17
holiday,5/1,May 1st
holiday,2021/12/2,Dec 2, 2021
ymdFormat,M/d/yyyy
holiday,2/1/2021,just another holiday
```


#### 定義済みの祝休日
[UnitedStates](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/UnitedStates.java) and [Japan](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/Japan.java) でアメリカ合衆国と日本の祝休日が事前定義されています。

- 日本の祝休日を適用
```java
ビジネスカレンダー japanCal = ビジネスカレンダー.newBuilder()
    .祝休日(ビジネスカレンダー.日本の祝休日)
    .build();
```

- アメリカ合衆国のキング牧師記念日と独立記念日を適用
```java
ビジネスカレンダー usCal = ビジネスカレンダー.newBuilder()
    .祝休日(UnitedStates.MARTIN_LUTHER_KING_JR_DAY)
    .祝休日(UnitedStates.INDEPENDENCE_DAY)
    .build();
```

アメリカ合衆国の祝日として利用可能な値: NEW_YEARS_DAY, MARTIN_LUTHER_KING_JR_DAY,
MEMORIAL_DAY, INDEPENDENCE_DAY, LABOR_DAY, VETERANS_DAY, THANKS_GIVING_DAY, CHRISTMAS_DAY

-  アメリカ合衆国の全ての休日を適用

```java
ビジネスカレンダー usCal = ビジネスカレンダー.newBuilder()
    .祝休日(UnitedStates.PUBLIC_HOLIDAYS)
    .build();
```

- 土日休業

```java
ビジネスカレンダー weekDays = ビジネスカレンダー.newBuilder()
    .祝休日(ビジネスカレンダー.土日休業)
    .build();
```

### 祝休日、営業日を判定する

- 今日が祝休日か営業日か判定

```java
ビジネスカレンダー cal = ...
System.out.println("今日は休業日? " + cal.is祝休日());
System.out.println("今日は営業日? " + cal.is営業日());
```

- 指定日が祝休日か営業日か判定

```java
ビジネスカレンダー cal = ...
LocalDate 令和3年五月24日 = LocalDate.of(2021, 5, 24);
System.out.println("2021年5月24日は祝日? " + cal.is祝休日(令和3年五月24日));
System.out.println("2021年5月24日は営業日? " + cal.is営業日(令和3年五月24日));
```

- 次、または前の祝休日または営業日を取得

```java
ビジネスカレンダー cal = ...
System.out.println("前の祝休日は? " + cal.以前の祝休日(LocalDate.of(2021, 5, 24)));
System.out.println("次の祝休日は? " + cal.以降の祝休日(LocalDate.of(2021, 5, 24)));
LocalDate みどりの日 = LocalDate.of(2021, 5, 4);
System.out.println("前の営業日は? " + cal.以前の営業日(みどりの日));
System.out.println("次の営業日は? " + cal.以降の営業日(みどりの日));
```

以前、以降の祝休日/営業日は指定日を含めます。つまり指定日が祝休日であれば 以前の祝休日() / 以降の祝休日() は同じ日を返します。

### 営業時間の判定

- 現在が営業時間内か判定

```java
ビジネスカレンダー cal = ...
System.out.println("営業中? " + cal.is営業時間());
```

- 指定した時間が営業時間内か判定

```java
ビジネスカレンダー cal = ...
LocalDateTime 五月24日10時23分 = LocalDateTime.of(2021, 5, 24, 10, 23);
System.out.println("営業中? " + cal.is営業時間(五月24日10時23分));
```

- 次の、または前の営業開始/終了時間を取得

```java
ビジネスカレンダー cal = ...
LocalDateTime 五月24日10時23分 = LocalDateTime.of(2021, 5, 24, 10, 23);
System.out.println("現在の営業時間はいつ終了する? " + cal.次の営業終了時間(may241023));
System.out.println("現在の営業時間はいつ開始した? " + cal.前の営業終了時間(may241023));
```

- 指定した日の [営業時間枠](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/BusinessHourSlot.java) を全て取得

```java
ビジネスカレンダー cal = ...
LocalDateTime may24 = LocalDateTime.of(2021, 5, 24);
// 祝休日の場合は空のリストを返す
List<BusinessHourSlot> slots = cal.get営業時間枠(may24);
System.out.println("2021年5月24日の営業時間枠数: " + slots.size());
System.out.println("2021年5月24日の営業開始時間: " + slots.get(0).from;
```

- 指定した期間の [祝休日](https://github.com/yusuke/businessCalendar4J/blob/main/src/main/java/com/samuraism/bc4j/Holiday.java) や営業日を取得

```java
ビジネスカレンダー cal = ...
List<Holiday> holidays = cal.get指定期間内の祝休日(LocalDateTime.of(2021, 1, 1),
    LocalDateTime.of(2021, 12, 21));
System.out.println("2021年の祝休日: " + holidays);
List<LocalDate> businessDays = cal.get指定期間内の営業日(LocalDateTime.of(2021, 1, 1),
    LocalDateTime.of(2021, 12, 21));
System.out.println("2021年の営業日: " + businessDays);
```

## サンプルコード
日本の祝日については[JapaneseHolidays](https://github.com/yusuke/businessCalendar4j/blob/main/src/main/java/com/samuraism/businessCalendar/JapaneseHolidays.java) の他、日本語のAPIである [日本の祝休日](https://github.com/yusuke/businessCalendar/blob/main/src/main/java/com/samuraism/businessCalendar/日本の祝休日.java) もあります。

サンプルコードは日本の祝日の処理方法については [JapaneseHolidaysExample.java (英語語API)](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/JapaneseHolidaysExample.java),  [ビジネスカレンダーExample.java (日本語API)](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/ビジネスカレンダーExample.java) を、アメリカ合衆国の祝日の処理方法については [UnitedStatesHolidaysExample](https://github.com/yusuke/businessCalendar4J/blob/main/src/test/java/com/samuraism/bc4j/exmaple/UnitedStatesHolidaysExample.java) をご覧ください。

## 祝日情報取得の仕組み
祝日の情報は[内閣府の祝日情報](https://www8.cao.go.jp/chosei/shukujitsu/gaiyou.html) に掲載されている [syukujitsu.csv](https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv) を利用しています。
起動時に読み込んだあと、負荷をかけないよう毎31日±5分毎(±5分の部分はランダム)に再読み込みを行います。

当該URLからCSVの読み込みを失敗した場合はリソースファイルに配置されている /syukujitsu.csv にフォールバックします。

祝日情報を記載したCSVファイルを読み込めるURLをシステムプロパティ SYUKUJITSU_URL に指定すれば独自の祝日情報を設定できます。

## ライセンス
Apache License Version 2.0

![Java CI with Gradle](https://github.com/yusuke/businessCalendar4j/workflows/Java%20CI%20with%20Gradle/badge.svg)