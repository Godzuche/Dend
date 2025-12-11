## 📄 Product Requirements Document (PRD)

# DenD – Your Personal Call Firewall

### Purpose

To give users simple, powerful control over incoming phone calls, allowing them to block unwanted interruptions, protect their focus, and prevent disruptions to their mobile data connection, all without compromising privacy.

---

### Core Features

*   **Call Screening Engine**
    *   Acts as the default "Caller ID & spam app".
    *   Intercepts all incoming calls for processing before the phone rings.
    *   Requires the `CallScreeningService` role to be granted by the user.

*   **Blacklist Filtering**
    *   Users can add an unlimited number of phone numbers to a personal blacklist.
    *   Calls from numbers on the blacklist are **instantly rejected**.
    *   The phone does not ring, and no "missed call" notification is created.
    *   The rejected call does not appear in the native call log.

*   **Whitelist Filtering**
    *   Users can add an unlimited number of phone numbers to a personal whitelist (e.g., family, work).
    *   Calls from numbers on the whitelist will **always be allowed** to ring, bypassing all other rules.

*   **Zen Mode**
    *   A single-tap toggle that enables an aggressive blocking mode.
    *   When active, **all incoming calls** are rejected unless they are on the user's whitelist.
    *   Designed for meetings, gaming sessions, or periods of focused work.

*   **Privacy First**
    *   No data (phone numbers, call times, lists) ever leaves the device.
    *   No accounts or cloud synchronization.
    *   No network permissions are required for the core functionality.

---

### Future Enhancements (v2+)

*   **Automated SMS Replies**
    *   Option to automatically send a pre-defined text message when a call is blocked.
    *   Support for a general default message.
    *   Support for custom messages assigned to specific numbers.

*   **Intelligent Spam Detection**
    *   Integrate a community-powered or global database to automatically block known spam and scam numbers.

*   **Advanced Firewall Rules**
    *   Allow blocking based on patterns (e.g., all hidden/private numbers, all international numbers).

*   **Firewall Log**
    *   An in-app screen that shows a history of all calls that DenD has blocked.

---

### Permissions

This section outlines the sensitive permissions required for DenD to function as intended.

*   **Role Manager Access** (for `ROLE_CALL_SCREENING`)
    *   **Reason:** The core permission that allows the app to intercept and screen incoming calls.

*   **Answer Phone Calls** (`ANSWER_PHONE_CALLS`)
    *   **Reason:** Required to programmatically reject an incoming call. This is essential for the blocking functionality.

*   **Write Call Log** (`WRITE_CALL_LOG`)
    *   **Reason:** To provide a seamless user experience by deleting the "missed call" entry that Android creates after a call is rejected.

*   **Read Call Log** (`READ_CALL_LOG`)
    *   **Reason:** To enable the future "Firewall Log" feature, which will display a history of blocked calls to the user.

*   **Read Contacts** (`READ_CONTACTS`)
    *   **Reason:** To intelligently handle calls from numbers saved in the user's contacts. This allows for future features like "don't block my contacts" and makes whitelist management easier.

*   **Send SMS** (`SEND_SMS`)
    *   **Reason:** (Optional) Will only be requested when the user enables the "Automated SMS Replies" feature.

*   **Post Notifications** (`POST_NOTIFICATIONS`)
    *   **Reason:** For any non-intrusive app-related alerts or status updates (e.g., "Zen Mode is active").

---

### Monetization

*   **Freemium Model:**
    *   Core features (blacklist, whitelist, Zen Mode) are free.
    *   Future premium features (e.g., Automated SMS Replies, advanced rules) will require a one-time in-app purchase.

---

### Target Users

*   **Students & Professionals** who need uninterrupted focus time for work or study.
*   **Gamers & Streamers** who cannot afford to have their game interrupted or their data connection dropped by a call.
*   **Privacy-Conscious Individuals** who want to control who can contact them without sharing data with a third party.
*   **Anyone** feeling overwhelmed by robocalls, telemarketers, or unknown numbers.

---
