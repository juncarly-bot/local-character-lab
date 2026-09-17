# Relationship System

## Goal

単一の「好感度」だけでは表現できない関係を扱う。

例:

- 好きで一緒にいると楽しいが、重要なことは任せにくい
- それほど親密ではないが、とても信用している
- 親しいが、今だけ少し気まずい

## Long-term axes

各関係は方向ごとに保存する。

`A -> B` と `B -> A` は別の値を持つ。

### Familiarity

範囲: `0-100`

どれだけ相手を知っているか。付き合いの長さや共有体験の蓄積を表す。

### Affection

範囲: `0-100`

親しさ・好意。マイナスにはしない。

関係悪化を「負の好感度」で表すのではなく、Trust、Comfort、Tensionなどとの組み合わせで表現する。

### Trust

範囲: `0-100`

どの程度、相手を信用できるか。大切なことを任せる、秘密を話す、といった行動に影響する。

### Comfort

範囲: `0-100`

相手と一緒にいてどれだけ気楽か。会話の自然さ、沈黙への耐性、入室したさなどに影響する。

## Temporary axis

### Tension

範囲: `0-100`

気まずさ、苛立ち、喧嘩直後の空気など、一時的な関係状態。

Tensionは時間経過で自然に減衰する。ただし、原因となった出来事はMemoryに残り得る。

## Strict semantic roles of each axis

Relationship各軸は似た意味の好感度スコアとして扱わず、以下の役割を厳密に分離する。本方針をv1の正式仕様として採用する。

### Familiarity = 共有経験と相手理解の蓄積

Familiarityは「どれだけ相手を知っているか」「どれだけ一緒に時間を過ごしたか」を表す。

上がりやすい例:

- 普通の会話を継続する
- 一緒にRoomで過ごす
- 共同体験が増える
- 相手の好みや習慣を知る

単に優しくされたから大きく上がる値ではない。

### Affection = 相手を好ましく思う度合い

Affectionは「この人が好き」「一緒にいたい」「好意的に感じる」に対応する。

上がりやすい例:

- 楽しい共有体験
- 好きな話題で盛り上がる
- 思いやりのある行動
- 自分を気にかけてもらう
- 好意的なInteraction Eventの積み重ね

AffectionはTrustやComfortの代替にしない。

### Trust = 信用して任せられる度合い

Trustは「この人の言うことを信じられる」「大切なことを任せられる」「秘密や弱みを預けられる」に対応する。

上がりやすい例:

- 約束を守る
- 困った時に実際に助ける
- 大事な相談へ誠実に向き合う
- 個人的な情報を適切に扱う
- 長期的に一貫した行動を取る

下がりやすい例:

- 約束を破る
- 重要な場面で裏切る
- 秘密を軽く扱う
- 信用を損なう行動

楽しい雑談だけではTrustを大きく増やさない。

### Comfort = 一緒にいて自然でいられる度合い

Comfortは「気を張らなくてよい」「沈黙でも平気」「くだらない話や失敗も見せられる」に対応する。

上がりやすい例:

- 安心できる雑談
- 沈黙を共有する
- 相手の前で自然に振る舞える経験
- 軽い冗談や失敗を受け入れてもらう
- 無理に会話を続けなくても関係が崩れない経験

Comfortが高いと、くだけたSpeech Style、自然な沈黙、軽い冗談、弱音などが出やすくなる。

### Tension = 現在の気まずさ・衝突の残響

Tensionは長期的な嫌悪度ではなく、現在の関係に残っている一時的な緊張を表す。

上がりやすい例:

- disagreementが強くなる
- criticism
- conflict
- 気まずい出来事

下がりやすい例:

- 時間経過
- apology
- reconciliation
- 穏やかな会話の再開

原因となった出来事自体はMemoryやTrust低下として残り得るため、Tensionが0に戻っても「何もなかったこと」にはしない。

## Axis independence principle

1つの出来事で全Relationship軸を一律に上下させない。

例:

```text
楽しい雑談
→ Familiarity + small
→ Affection + small
→ Comfort + small
→ Trust almost unchanged

約束を守る
→ Trust + meaningful
→ Affection + possible small
→ Familiarity almost unchanged

喧嘩
→ Tension + large
→ Comfort - moderate
→ Trust / Affectionは内容次第
```

この分離により、例えば以下のような関係を表現できる。

```text
Affection 90
Trust 45
Comfort 80
```

これは「好きで一緒にいると楽しいが、重要なことを全面的に任せるほど信用してはいない」という関係として扱える。

## Relationship Tags

数値だけでは関係の意味を表現できないため、タグを別途持つ。

候補:

- `friend`
- `close_friend`
- `senior_junior`
- `sibling_like`
- `rival`
- `protector`
- `admirer`
- `childhood_friend`

Tagsは主として関係の種類を表し、数値は現在の深さ・状態を表す。

## Directionality

例:

```yaml
sayo_to_tt:
  familiarity: 80
  affection: 72
  trust: 82
  comfort: 70
  tension: 5
  tags: [close_friend]

tt_to_sayo:
  familiarity: 80
  affection: 65
  trust: 90
  comfort: 58
  tension: 5
  tags: [close_friend]
```

同じ2人でも感情・信頼・気楽さは非対称でよい。

## Initial values

関係性は全員0から始めず、キャラクター設定時に初期値を与える。

初期値の目安は `20-80` を中心とし、100は長期的に到達する特別な状態として残す。

例:

```text
初対面に近い: 10 / 35 / 20 / 25
普通の知人:   35 / 45 / 40 / 40
友人:         60 / 65 / 60 / 65
親しい友人:   80 / 80 / 78 / 85
```

順序は Familiarity / Affection / Trust / Comfort。

## Conversation-driven change policy

Relationshipはメッセージ1通ごとに機械的に加点しない。

会話内容、会話量、Character Personality、Character Interests、そのキャラクターの実参加時間などをConversation Analysisで評価し、会話イベントまたはセッション単位で更新する。

特に、キャラクターが好きな話題を相手と楽しく話した場合は、AffectionやComfortが上がりやすくなる。

概念例:

- ordinary shared time -> Familiarity中心
- enjoyable favorite-topic conversation -> Affection / Comfort中心
- serious consultation / meaningful disclosure -> Trust中心
- positive long conversation -> Familiarity + small Affection / Comfort
- repeated unpleasant interaction -> Comfort低下、Tension上昇、必要ならAffection小低下
- promise broken -> Trust低下
- reconciliation -> Tension低下、Comfort回復

同じ長さの会話でも、本人のInterestsやValuesと合うかどうかで変化量は異なる。

Relationship growthは総Room時間ではなく、本人の`participation_duration`を重視する。

## Invitation effect

ユーザーがUIの招待ボタンで特定キャラクターを呼ぶ行為は、「自分を選んで呼んでくれた」という小さなRelationshipシグナルとして扱ってよい。

候補:

- Affection: very small positive
- Comfort: very small positive

ただし招待連打によるポイント稼ぎを防ぐため、同一セッション / 一定時間内では1回だけ有効にするなどcooldownを設ける。

退出ボタンは主としてUI操作であり、長期Affectionを大きく下げない。

## Passive Affection decay

長期間まったく話さない場合、Affectionはごくゆっくり下がってよい。

ただし、親しくなった関係が放置だけで赤の他人へ戻ることは避ける。

初期案:

```text
if affection > 80 and long_inactivity:
    affection slowly approaches 80

if affection <= 80:
    no passive decay from inactivity
```

つまり自然減衰のfloorを約`80`に置く。

この`80`はv1の初期値であり、試遊後に調整可能とする。

Familiarity / Trustは通常、単に話さなかっただけでは下げない。Comfortは必要なら非常に小さな減衰を検討する。

Tensionは逆に時間経過で減衰する。

## Interaction Event v1

Relationship変化の正式な橋渡しとして `docs/INTERACTION_EVENTS.md` を採用する。

v1の中心イベント:

- `shared_enjoyment`
- `support`
- `personal_disclosure`
- `praise`
- `gratitude`
- `help`
- `disagreement`
- `criticism`
- `conflict`
- `apology`
- `reconciliation`
- `promise_kept`
- `promise_broken`

Interaction Eventは原則として `actor -> target` の方向を持ち、`strength: 0.0-1.0` とConversation Contextを使って実際のdeltaを調整する。

長期軸の通常変化は小さくし、Tensionのみ短期状態として比較的大きく変動してよい。

Affectionは更新後も必ず `0-100` にclampし、負値を許可しない。

## Anti-farming principle

- ターン単位ではなくセッション / conversation-event単位で評価する
- 同一内容の反復は効果を弱める
- 1セッションあたりの変化量に上限を持たせる
- 高い数値ほど成長を緩やかにすることを検討する
- 招待ボタン等のUIイベントにもcooldownを設ける

通常会話ではFamiliarity / Affection / Trust / Comfortの各軸を1セッションあたり絶対値 `2.0` 程度までに抑える初期案を採用する。重大イベントは別枠で扱える。

## Use in behavior

Relationshipは以下へ影響させる。

- 入室したさ
- 招待への応答
- 発言相手の選択
- 秘密や弱音を話すか
- 冗談やツッコミの強さ
- 沈黙の気まずさ
- 退出判断
- 自発的欲求の発生

総合的な1つのAffinity値は内部の正規状態として保存しない。必要なら表示用に4軸から計算する。
