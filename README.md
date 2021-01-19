# 日本の祝休日
1955年以降の日本の祝日、休日の判定や名称取得を行えるライブラリです。依存がないので気軽に利用できます。
## ハッシュタグ
ご意見、ご感想などは [&#35;Java祝休日](https://twitter.com/intent/tweet?text=https://github.com/yusuke/japan-holidays/+%23Java祝休日) を使ってツイートしていただければ幸いです。
## 動作要件
Java 8以降

## 利用方法

```java
package com.samuraism.holidays.exmaple;

import com.samuraism.holidays.日本の祝休日;
import com.samuraism.holidays.祝休日;

import java.time.DayOfWeek;
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
        holidays.get指定期間内の祝休日️(LocalDate.of(2021, 5, 1)
                , LocalDate.of(2021, 5, 31))
                .forEach(e -> System.out.println(e.日付 + ": " + e.名称));

        // 固定のカスタム祝休日を設定
        // メソッドチェーンで続けて書けるが、ミュータブルではなくオリジナルのインスタンスに変更が加わっていることに注意
        holidays.add祝休日(new 祝休日(LocalDate.of(2013, 3, 29), "株式会社サムライズム設立"))
                // ロジックベーのカスタム祝休日を設定。当該日が祝日ならば名称を、そうでなければnullを返す関数を指定する
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SATURDAY ? "土曜日" : null)
                .add祝休日(e -> e.getDayOfWeek() == DayOfWeek.SUNDAY ? "日曜日" : null)
                .add祝休日(e -> e.getMonthValue() == 12 && e.getDayOfMonth() == 31 ? "大晦日" : null);

        // 2021年1月最終営業日を取得→ 1月30日、31日が土日なので1月29日金曜日
        System.out.println("2021年最終営業日: " + holidays.以前の営業日(LocalDate.of(2021, 1, 31)));
        // 2020年大晦日以降最初の営業日を取得→ 1月1日は元日、1月2,3日はカスタム祝日(土日)なので1月4日月曜日
        System.out.println("2020年大晦日以降最初の営業日: " + holidays.以降の営業日(LocalDate.of(2020, 12, 31)));
        // 2021年2月22日以降最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(holidays.以降の祝休日(LocalDate.of(2021, 2, 22)));
        // 2021年2月26日以前最初の祝日を取得→ 2月23日 天皇誕生日
        System.out.println(holidays.以前の祝休日(LocalDate.of(2021, 2, 26)));
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