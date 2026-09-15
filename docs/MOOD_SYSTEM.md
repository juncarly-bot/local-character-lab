# Mood System v1

## Goal

Moodは固定ラベルを直接保存するのではなく、内部の連続値から滑らかに変化させる。

表示上はHAPPY / TIRED / SLEEPYなどのラベルを出してよいが、内部状態は数値で保持する。

## Internal axes

初期v1では以下の4軸を使う。

- `valence`: 0-100。気分の良さ
- `energy`: 0-100。元気・活動性
- `social_energy`: 0-100。人と話したい余力
- `sleepiness`: 0-100。眠気

各キャラクターの初期値はユーザーが個別に設定する。

## Default change policy

特別な条件がない場合、各軸の自然増減は大きく差をつけず、同程度の緩やかな変化にする。

v1では複雑なキャラクター固有回復式を最初から入れず、まず共通の標準レートを使う。

キャラクター差は主として以下から生む。

- Character Profile
- Conversation Analysis
- Interests
- time of day
- participation duration
- Relationship / Tension

## Event-driven modifiers

明確な条件があるときだけ標準レートへ補正を加える。

例:

- 楽しい会話 -> valence上昇
- 好きな話題 -> valence / energyが少し上昇
- 長時間会話 -> energy / social_energy低下
- 一人で静かに過ごす -> social_energy回復候補
- 睡眠に近い時間帯 -> sleepiness上昇
- 睡眠 -> energy回復、sleepiness低下
- conflict -> valence低下、social_energy低下候補

数値は実装・試遊後に調整する。

## Personality modifiers

Personalityは変化量への軽い補正として使う。

例:

- `emotionality` 高 -> valence変化がやや大きい
- `sociability` 高 -> 会話中のsocial_energy消費をやや軽減
- `silence_tolerance` 高 -> social_energyが低くても即退出しにくい

ただしv1では補正を強くしすぎず、同じ会話でもキャラ差が少し出る程度から始める。

## Display mood

内部数値から表示用ラベルを導出する。

例:

- high valence + medium/high energy -> `HAPPY`
- high energy + high social_energy -> `EXCITED` or `SOCIAL`
- low energy + high sleepiness -> `SLEEPY`
- low social_energy + medium valence -> `QUIET`
- low valence -> `DOWN` / `ANNOYED` depending on conflict and context

表示ラベルは1つに固定せず、必要ならprimary / secondaryの2つまで出してよい。

## Design principle

Moodは急激にラベルが切り替わるスイッチではなく、会話内容、参加時間、時間帯、人格によってじわじわ動く状態として扱う。

初期値はユーザーが決め、条件がない場合の自然変化はまず共通の標準レートで統一し、必要なところだけ明確な補正を加える。
