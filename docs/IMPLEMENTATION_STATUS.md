# Implementation Status

## Current branch

`feature/android-mvp-skeleton`

## First implementation slice

The repository has moved from design-only documentation into an executable Android MVP skeleton.

Implemented:

- Android application module
- Kotlin + Jetpack Compose
- main navigation: Chat / Characters / Settings
- 3 resident characters + Guest Slot visual shell
- Character Profile / Runtime State domain models
- directional Relationship model
- initial deterministic Conversation Director speaker scoring
- Developer-friendly speaker score contribution breakdown
- `CharacterModelRuntime` abstraction
- Preview Runtime as a temporary stand-in for the future local LLM
- sample Character world
- unit tests for direct address / Room OUT exclusion / recent-speaker penalty
- GitHub Actions Android test + APK build workflow

## Important limitation

The current runtime does **not** yet execute a real local LLM.

`PreviewCharacterModelRuntime` exists only to prove the end-to-end boundary:

```text
UI
-> ConversationDirector
-> CharacterModelRuntime
-> Character utterance
```

The next runtime implementation can replace Preview Runtime without moving Character Profile, Relationship, Memory, or Room state into the model layer.

## Next implementation targets

1. Character Profile screen
2. Room persistence boundary
3. Conversation Act selection
4. Runtime Context Builder
5. latency instrumentation
6. laptop/local Main LLM adapter
7. Helper model adapter after profiling
8. Memory / Relationship event pipeline
9. Guest Slot enter / leave scoring
10. streaming token display

## Performance contract

Normal foreground turn target remains:

```text
<= 10 sec
```

The implementation should optimize first-visible-token latency and keep normal Main LLM calls near one per user turn.
