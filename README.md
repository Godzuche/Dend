# DenD - Your Personal Call Firewall

**Stop unwanted calls. Reclaim your peace.** DenD gives you simple, powerful control to decide exactly who can and cannot make your phone ring.![GitHub last commit](https://img.shields.io/github/last-commit/Godzuche/DenD?style=for-the-badge)
![GitHub repo size](https://img.shields.io/github/repo-size/Godzuche/DenD?style=for-the-badge)
![GitHub issues](https://img.shields.io/github/issues/Godzuche/DenD?style=for-the-badge)

---

## Core Features

DenD uses Android's official `CallScreeningService` API to give you a secure and seamless firewall for your calls.

*   **Build Your Blocklist:** Add any number to your personal blacklist. They will be rejected instantly and silently, without ever disturbing you.
*   **Define Your VIPs:** Create a whitelist of approved numbers (family, friends, work). These calls will always get through, no matter what.
*   **Zen Mode:** With a single tap, activate the ultimate firewall. This mode blocks all incoming calls that aren't on your whitelist, guaranteeing you absolute focus and quiet.
*   **Clean & Invisible:** Blocked calls don't leave "missed call" notifications and won't clutter your phone's history. No ring, no notification, no mess.

## Philosophy

Your phone should be a tool that serves you, not a source of constant interruption. DenD is built on a simple principle: **you are the gatekeeper.** We started with the most important feature—giving you absolute manual control—as the foundation for a smarter, more respectful calling experience.

## Built With

*   [**Kotlin**](https://kotlinlang.org/): The primary language for modern Android development.
*   [**Jetpack Compose**](https://developer.android.com/jetpack/compose): For building a beautiful, reactive, and modern UI.
*   [**CallScreeningService API**](https://developer.android.com/reference/android/telecom/CallScreeningService): The official, secure Android API for call interception.
*   **Coroutines & Flow:** For asynchronous operations and managing data streams.
*   **Material 3:** Following the latest design guidelines from Google.

## Roadmap: What's Next?

DenD is actively being developed. Our vision is to evolve this personal firewall into an intelligent guardian for your calls.

- [ ] **Intelligent Spam Detection:** Automatically block known spam and scam numbers using a community-powered or global database.
- [ ] **Customizable Firewall Rules:** Block calls based on patterns (e.g., all international numbers, all hidden numbers).
- [ ] **Firewall Log:** View a history of all the calls your firewall has blocked for you.
- [ ] **Temporary Pass:** Grant a one-time pass to a blocked number, allowing them to call you back within a short time window (e.g., for delivery services).

## Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

Don't forget to give the project a star! Thanks again!

## License

Distributed under the **Apache License 2.0**. See `LICENSE.txt` for more information.
