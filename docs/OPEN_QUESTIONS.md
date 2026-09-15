# Open Questions

このファイルは、まだ決め切っていない設計事項を追跡する。

## Character Profile v1

- Personalityの軸は6個で十分か
- Conversation behaviorに追加軸が必要か
- Interestsのキーは自由文字列か、内部ID + 表示名にするか
- Valuesの最大件数や優先度を持たせるか

## Mood

- v1で採用するMood一覧
- Moodを離散値にするか、複数軸にするか
- Moodの持続時間・減衰方法

## Relationships

- イベントごとのFamiliarity / Affection / Trust / Comfort / Tension変化量
- Relationship Tagsを固定語彙にするか、ユーザー追加可能にするか
- USERとの関係もキャラクター間と完全に同じ仕組みにするか

## Memory

- キャラクター別Memory Storeか、共通Store + `known_by`方式か
- 短期記憶 / 長期記憶の分離
- importanceの計算
- 忘却・要約・統合の方法

## Presence

- Enter Scoreの正式な式と重み
- Leave Scoreの正式な式と重み
- 判定周期
- 再入室Cooldown
- 招待の扱い

## Desire

- Desire生成の実行タイミング
- 同時に保持するDesire数
- Desireの自然減衰
- LLM生成とルール生成の分担

## Model / Runtime

- 実際に採用する3B級ベースモデル
- 量子化方式
- PC上のRuntime
- 将来のAndroid実行方式
- Character LoRAをいつ導入するか

## UI

- Character Detailの最終レイアウト
- Developer Modeの表示粒度
- 関係性数値を通常ユーザーにも全て見せるか
- Character Profileのimport / export形式
