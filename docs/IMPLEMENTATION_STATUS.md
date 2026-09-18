# Implementation Status

## Current state

Character Room Lab now has an executable Android MVP foundation, a shared Character Profile surface, and the first deterministic conversation-planning path before local LLM integration.

## Implemented

- Android application module
- Kotlin + Jetpack Compose
- main navigation: Chat / Characters / Settings
- 3 resident characters + Guest Slot visual shell
- Character Profile / Runtime State domain models
- directional Relationship model
- deterministic Conversation Director speaker scoring
- Developer-friendly speaker score contribution breakdown
- shared Character Profile opened from Chat participant chips and Characters list
- Profile sections for Overview / Relationship / Personality / Behavior / Interests / Expertise / Values
- full-screen Profile dialog preserving the underlying Chat composition
- `CharacterModelRuntime` abstraction
- Preview Runtime as a temporary stand-in for the future local LLM
- foreground latency instrumentation for Director / generation / total turn
- deterministic direct-address and question signal parser
- direct address fed into speaker selection
- deterministic Conversation Act selection with a basic Knowledge cooldown
- `RuntimeContextBuilder` with a bounded recent transcript
- generation now receives structured `CharacterRuntimeContext` rather than raw Character + text arguments
- Chat developer trace for target / selected speaker / act / score / latency
- unit tests for speaker selection, Room OUT exclusion, recent-speaker penalty, direct-address parsing, Act selection, and Context slicing
- GitHub Actions Android test + APK build workflow

## Current foreground path

```text
USER message
-> deterministic signal parser
-> direct target / question signal
-> Conversation Director
-> speaker selection
-> Conversation Act selection
-> RuntimeContextBuilder
-> CharacterModelRuntime
-> character utterance
```

This is intentionally structured so a real Main LLM can replace Preview Runtime without taking ownership of Character Profile, Relationship, Memory, or world state.

## Important limitation

The current runtime does **not** yet execute a real local LLM.

The current signal parser is deliberately narrow. Ambiguous reply targets and semantic handoffs should later fall through to the lightweight Helper or Main model rather than turning the deterministic parser into a giant rule engine.

## Next implementation targets

1. Profile editing + persistence boundary
2. Room persistence
3. Response Obligation / Handoff state
4. richer Topic state + Topic Lifecycle
5. laptop/local Main LLM adapter
6. streaming token display
7. Memory / Relationship event pipeline
8. Guest Slot enter / leave scoring
9. Helper model structured analysis after profiling
10. Developer Mode dedicated inspector

## Performance contract

Normal foreground turn target remains:

```text
<= 10 sec
```

The current deterministic planner adds negligible work compared with future model inference, and latency is already exposed in the Chat developer trace.
