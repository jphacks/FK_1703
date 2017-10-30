# Grandea ～広げるアイデア～

### Mobile App : https://github.com/jphacks/FK_1703  
  
### Server : https://github.com/jphacks/FK_1703_2  

[![Grandea](/img/top.png)](https://youtu.be/TRCr_O83YDA)

## 【祝】Google Play Store リリース！
![Google Play Store](/img/googleplaystore.png)
[ダウンロードはこちらから](https://play.google.com/store/apps/details?id=com.original.haruyai.brainstorming3 "Grandeaリンク") (只今、公開待ちです。少々お待ち下さい。)

## 製品概要
話し合っている内容を認識してアイデア出しを手助けするモバイルアプリケーション

### Idea × Tech

### 背景（製品開発のきっかけ、課題等）
アイデア出しって楽しいけど難しい、、、。  
アイデア出しと言うと堅苦しく感じますが、近年、新規企画やハッカソンなどのイベント、  
また旅行の計画などの日常の中にもアイデア出しは行われています。  
そんな頻繁に行われているアイデア出しを楽しく・円滑に行いたい！  
そうした思いから今回の開発に至りました。

### 製品説明（具体的な製品の説明）
Grandeaは、アイデア出しをする際のミーティング中に起動しておくだけで、アイデア出しの補助を行ってくれるモバイルアプリケーションです。また、話した内容・要約・ミーティングのキーワードをまとめた議事録も作成してくれます。  
このアプリケーションには、主に以下の2つの機能があります。  

#### 1. アイデアマップ
モバイルアプリケーション内でストリーミングで音声認識を行い、その結果をサーバに送ります。  
送られてきた文章のキーワードを抽出し、そのキーワードに関連するキーワードを取得します。  
その取得したキーワードをモバイルアプリケーションに送り、アイデアマップ上に表示します。  
アイデアマップ上のキーワードをタップすると、そのキーワードに関連するワードを3つ表示します。  
また、アイデアマップには、自分でキーワードを追加し、その関連するキーワードを表示することもできます。  
アイデアマップ上で表示したキーワードは、まとめて見ることもできるため、関連性が低いキーワード同士からもアイデアを考えることができます。  

#### 2. 議事録
モバイルアプリケーション内でストリーミングで音声認識を行い、その結果をサーバに送ります。  
送られてきた文章を繋ぎ合わせていき、ミーティングが終わってSTOPボタンが押されると、繋ぎ合わせていた文章の要約とキーワードを取得します。  
その取得したデータと話した内容を議事録として表示します。  


![アイデアマップ1](/img/ideamap1.png)
![アイデアマップ2](/img/ideamap2.png)
![議事録](/img/minutes.png)
![システムフロー](/img/flow.png)

### 特長

#### 1. 話している内容からおすすめのキーワードを取得し表示

#### 2. ストリーミング処理で音声認識をすることでリアルタイムにレスポンスが可能

#### 3. こだわりのアイデアマップ  

#### 4. 自動で議事録作成  

### 解決出来ること
* アイデア出しの停滞防止  
話し合っている内容に関連するキーワードを提供することにより、アイデア出しの停滞を防ぎます。  
* 共通認識の欠如防止  
話し合っている内容やキーワードを示すことにより、共通認識を持てます。  
* 議事録作成の手間の削減  
話し合った内容を認識し、自動で議事録を作成することにより、議事録作成の手間を削減します。  

### 今後の展望
* WebRTCによる遠隔ビデオ通話ミーティング上での実装  
* 音声データから特徴量を抽出し、話者識別をするニューラルネットモデルの作成  
* 音声認識した文章の補完  
* Raspberry Piを用いてガジェット化  
* 高集音性マイクを用いて集音性の向上  

## 開発内容・開発技術
### 活用した技術
#### API・データ
* Google Cloud Translation API
* Google Knowledge Graph Search API
* Google Suggest API
* gooラボ キーワード抽出API
* 自動要約API

#### フレームワーク・ライブラリ・モジュール
* Android Studio
* XAMPP
* CherryPy
* Speech Recognizer
* Amazon Web Services
* さくらのレンタルサーバ

#### デバイス
* Android(Android 5.0以上)

### 独自開発技術（Hack Dayで開発したもの）
#### 2日間に開発した独自の機能・技術  
* 音声認識とサーバへのHTTP通信の並列スレッド処理 [(Code)](https://github.com/jphacks/FK_1703/blob/master/app/src/main/java/com/example/haruyai/brainstorming3/SpeechRecognition.java "SpeechRecognition.java")
* 独自アイデアマップの作成 [(Code)](https://github.com/jphacks/FK_1703/blob/master/app/src/main/java/com/example/haruyai/brainstorming3/MainActivity.java "MainActivity.java")
* 文章のキーワードを取得し、そのキーワードから2つのAPIを用いての関連キーワード取得 [(Code)](https://github.com/jphacks/FK_1703_2/blob/master/relation_char.php "relation_char.php")  
