# 開発手順と規約

## 構成

- `app/src/main/java/.../model`: 入力検証、マジックパケット生成、保存。
- `app/src/main/java/.../ui`: 一覧、編集、ウィジェット選択。
- `app/src/main/java/.../widget`: ウィジェット更新と送信。
- `app/src/main/res`: 表示文言、色、画面、アイコン。
- `app/src/test`: JVM で動く純粋なロジックのテスト。

## 変更規約

- UI 文言は `strings.xml` に置き、日本語で統一する。色・余白・角丸はリソースにまとめる。
- ネットワークや保存を UI スレッドで行わない。ウィジェットの BroadcastReceiver は `goAsync()` で処理を完了する。
- 入力を検証してから保存・送信する。ウィジェットの Intent に MAC 等の設定値を含めず、ID から保存済み設定を読む。
- `PendingIntent` はウィジェット ID ごとに一意で immutable にする。送信 BroadcastReceiver は外部公開しない。
- 実際の MAC・IP、個人情報、資格情報をテスト・文書・ログに追加しない。

## 作業の順番

`docs/work-plan.md` の単位で、仕様確認 → 実装 → 対応するテスト → 差分確認 → 結果記録を行う。新たな選択は `docs/decisions.md` に追加する。レビュー時は `docs/quality.md` の完了条件を一つずつ確認する。
