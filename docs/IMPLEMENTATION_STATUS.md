# Implementation Status

## Current state

The repository now has an executable Android MVP foundation on `main`. The next slice adds the first shared Character Profile surface and latency instrumentation.

## Implemented

- Android application module
- Kotlin + Jetpack Compose
- main navigation: Chat / Characters / Settings
- 3 resident characters + Guest Slot visual shell
- Character Profile / Runtime State domain models
- directional Relationship model
- deterministic Conversation Director speaker scoring
- Developer-friendly speaker score contribution breakdown
- `CharacterModelRuntime` abstraction
- Preview Runtime as a temporary stand-in for the future local LLM
- sample Character world
- unit tests for direct address / Room OUT exclusion / recent-speaker penalty
- GitHub Actions Android test + APK build workflow
- shared Character Profile opened from Chat participant chips and Characters list
- Profile sections for Overview / Relationship / Personality / Behavior / Interests / Expertise / Values
- Profile is presented as a full-screen dialog so Chat composition and draft state stay alive behind it
- foreground latency instrumentation for Director / generation / total turn

## Important limitation

The current runtime does **not** yet execute a real local LLM.

`PreviewCharacterModelRuntime` exists only to prove the end-to-end boundary:

```text
UI
-> ConversationDirector
-> CharacterModelRuntime
-> Character utterance
```

Character Profile, Relationship and Room state stay outside the model layer.

## Next implementation targets

1. Profile editing + persistence boundary
2. Room persistence
3. Conversation Act selection
4. Runtime Context Builder
5. direct-address parser / Response Obligation
6. laptop/local Main LLM adapter
7. streaming token display
8. Memory / Relationship event pipeline
9. Guest Slot enter / leave scoring
10. Helper model adapter after profiling

## Performance contract

Normal foreground turn target remains:

```text
<= 10 sec
```

The UI now exposes initial runtime timing so real Main / Helper model integration can be measured instead of guessed.
