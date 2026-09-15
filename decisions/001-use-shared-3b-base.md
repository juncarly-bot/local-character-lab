# Decision 001: Use one shared 3B base model

Status: Adopted

## Decision

複数キャラクターのために3Bモデルを人数分常駐させず、共通の3B級ベースモデルを1つ使う。

キャラクター差はProfile、Context、Memory、Relationship、Mood、Desire、および将来的なAdapterで表現する。

## Reason

- 16GB級PCでも扱いやすくする
- 10キャラクター登録時のRAM・ストレージ増加を抑える
- 共通Runtimeを保ち、Character Engineとして再利用しやすくする
- キャラクター追加をモデル本体の複製なしで行えるようにする
