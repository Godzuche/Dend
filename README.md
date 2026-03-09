# Dend – An Intelligent Call Firewall for Android

<p align="center">
  <img src="https://user-images.githubusercontent.com/20674409/236163351-2e4a6e38-8c08-4b2a-8991-53b9b47e2469.png" alt="Dend App Logo" width="150"/>
</p>

<p align="center">
  <strong>Block Calls, Not Connections.</strong>
  <br />
  A modern, open-source Android app that gives you back your focus by intelligently filtering incoming calls based on your rules.
</p>

<p align="center">
  <a href="https://github.com/Godzuche/Dend/releases">
    <img src="https://img.shields.io/github/v/release/Godzuche/Dend?style=for-the-badge" alt="Latest Release"/>
  </a>
  <img src="https://img.shields.io/github/license/Godzuche/Dend?style=for-the-badge" alt="License"/>
</p>

## The Problem
Ever been in a focused work session, an important meeting, or a critical moment in a game, only to have an unwanted call interrupt you? Worse, on many networks, that incoming call can momentarily kill your phone's mobile data connection, disrupting your flow entirely.

Dend was engineered as a robust solution to this exact problem. It's a lightweight, private, and powerful firewall for your calls, ensuring that only the people you trust can make your phone ring at critical times.

## Features Implemented
This project showcases a production-ready application built with modern, best-practice Android architecture.

- **Three-Mode Firewall:**
    - **Firewall On:** Instantly and silently rejects any number on your personal blacklist.
    - **Zen Mode:** Rejects all calls *except* those on your trusted whitelist. Perfect for zero-distraction focus sessions.
    - **Firewall Off:** Allows all calls through as normal.

- **Intelligent Rules Management:**
    - **Persistent Blacklist & Whitelist:** Your rules are saved securely on your device in a Room database, surviving app restarts.
    - **Easy Creation of Rules:** Rules can be easily added using any of the following
      - A custom call logs screen built with Jetpack Compose
      - The user's contacts from their preferred Contact application
      - Manually entering the phone number and an optional name
    - **"Promote & Demote" Logic:** A number can only exist on one list. Adding a number to the whitelist automatically removes it from the blacklist (and vice-versa), ensuring predictable behavior.
    - **Forgiving "Undo" Action:** Accidentally removed a rule? A `Snackbar` with an "Undo" action provides a safety net, powered by a persistent `isPendingDeletion` flag in the database to handle app closures gracefully.

- **State-Aware Activity Log:**
    - **Proof & Transparency:** View a chronological history of every call the app has blocked, complete with timestamps and the mode it was blocked in.
    - **Contextual Headers:** The log is beautifully organized with "Today," "Yesterday," and date-based `stickyHeader`s for effortless scanning.
    - **Smart Actions:** Each log item features a dynamic set of quick actions. "Allow" a number (add to whitelist), "Call Back," or "Add Contact." Actions are intelligently hidden if they are not relevant (e.g., "Add Contact" is hidden if the number is already in your device contacts).

- **Robust & Resilient Call Screening:**
    - **System-Level Integration:** Built with `CallScreeningService` to intercept calls before they ring.
    - **High-Reliability Architecture:** Engineered for efficiency by elevating process priority with a `ForegroundService` during the critical call-rejection window.
    - **Intelligent Number Normalization:** Uses Google's `libphonenumber` to parse and normalize numbers to the E.164 standard, correctly handling local and international formats to ensure rules are matched reliably.

- **Modern & Decoupled Architecture:**
    - **100% Kotlin & Jetpack Compose:** A fully declarative UI built for performance and state-driven rendering.
    - **Declarative Navigation 3:** Leverages the latest Jetpack Navigation 3 for a state-based navigation model with `BottomSheet` and `NavigationBar`.
      - **State-Managed Back Stack:** Replaces complex internal managers with a developer-owned `List` of states or `SnapshotStateList`, making deep-linking and state restoration seamless.
      - **Type-Safe Routing:** Uses serializable Kotlin objects instead of string-based routes, ensuring compile-time safety across the entire navigation graph.
      - **Adaptive Layouts:** Built-in support for "Scenes," enabling easy transitions between single-pane and multi-pane (list-detail) layouts.
    - **Clean MVVM & UDF:** Follows modern MVVM principles with Unidirectional Data Flow using `Flow`, `StateFlow`, and `Channel/SharedFlow`.
    - **Centralized Event Bus:** A generic `UiEventBus` decouples ViewModels from the UI, allowing any feature to request a `Snackbar` or other global UI action without direct dependencies.
    - **Dependency Injection:** Uses Koin for a lightweight and maintainable dependency graph.

- **Private by Design:**
  All rule processing and call logging happens entirely on your device. Your contacts, rules, and call patterns never leave your phone.

## Screenshots

*Coming soon... The UI is being polished to perfection!*

## Installation & Setup

1.  Download the latest APK from the [**Releases Page**](https://github.com/Godzuche/Dend/releases).
2.  Install the app on your Android device (Android 10/Q or higher).
3.  **Crucial Step:** On first launch, the app will guide you to set **Dend** as the default **"Caller ID & spam app"**. This permission is required by Android for the app to screen and manage incoming calls.
4.  Start building your firewall rules and enjoy the silence!

## Roadmap & Planned Features
This project is an active exploration of modern Android development.

- [ ] **Custom Firewall Rules:** Block calls based on patterns (e.g., hidden numbers which is automatically blocked for now, international numbers).
- [ ] **Automated SMS Replies:** Optionally send a configurable, automatic text message when a call is blocked.
- [x] **Onboarding Flow:** A guided setup experience for new users.
- [ ] **Intelligent Spam Database Integration:** (Future) Optionally sync with a known spam number database.

## Contributing
This is an open-source project built for learning and solving a real-world problem. Contributions, feature ideas, and pull requests are welcome and greatly appreciated! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for more details.

## License
This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for details.

