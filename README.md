# Wake

自宅 LAN 上の PC に Wake-on-LAN マジックパケットを送る、シンプルな Android アプリです。PC の設定をアプリで管理し、PC ごとに 1 × 1 ホーム画面ウィジェットを配置できます。

## 使い方

1. アプリで PC の名前、MAC アドレス、送信先 IPv4、UDP ポートを登録します。
2. アプリの「起動する」を押すか、ホーム画面に「PC 起動」ウィジェットを置いて PC を選びます。
3. ウィジェットをタップすると、その PC へマジックパケットを送ります。

送信先の既定値は `255.255.255.255:9` です。ネットワークによっては `192.168.1.255` のようなブロードキャストアドレスを指定してください。PC 側で WoL が有効で、Android 端末と PC が通信可能なネットワークにある必要があります。アプリが示す送信成功は PC の起動確認ではありません。

## 開発

JDK 17 と Android SDK 36 を用意し、以下を実行します。

```sh
./gradlew testDebugUnitTest assembleDebug lintDebug
```

Windows では `gradlew.bat` を使います。開発方針・仕様・確認手順は [docs/README.md](docs/README.md) から参照してください。

GitHub Actions の [Android ワークフロー](https://github.com/nobuhiro-mizuno/android-simple-wol/actions/workflows/android.yml) では単体テスト、Debug ビルド、Lint を実行し、検証用 APK を `wake-debug-apk` として保存します。
