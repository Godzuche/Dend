## 🎨 Theming Specifications

This document outlines the specific visual design system for the DenD application, ensuring a consistent and branded user experience.

### Brand Name

**DenD**

---

### Typography

*   **Font Family:** **Inter** (from Google Fonts)
*   **Rationale:** Chosen for its exceptional screen readability, modern neutrality, and professional feel, which aligns perfectly with DenD's focus on clarity and calm control.
*   **Key Weights:**
    *   `400` (Regular) for body text.
    *   `500` (Medium) for buttons and labels.
    *   `700` (Bold) for headlines.

---

### Iconography

*   **Icon Pack:** **Material Symbols - Rounded**
*   **Style:** Icons should be consistent in stroke weight and generally use the "unfilled" style unless indicating an active or selected state.
*   **Key App Icons:**
    *   **Firewall / Zen Mode:** `shield`, `shield_off`, `self_improvement`
    *   **Rules / Lists:** `checklist`, `rule`
    *   **Activity / Log:** `history`
    *   **Permissions:** `admin_panel_settings`, `phone_disabled`, `contacts`
    *   **Privacy:** `lock`

---

### Component Style

*   **Buttons:** Use large, rounded corners (`Shape.extraLarge` / `24.dp`) to feel modern and friendly.
*   **Cards & Surfaces:** Use `surfaceVariant` or `surfaceContainerLow` for subtle background differentiation with slight elevation and soft shadows.
*   **Toggles/Switches:** Should use the `primary` color when in an "active" state to provide clear visual feedback.

---

### Color System

The following color palettes have been implemented for the Light and Dark themes. The scheme is built around a calming Indigo primary to evoke a sense of focus and security.

#### Light Theme Colors

| Role                      | Hex Code  | Color Preview                                   |
| :------------------------ | :-------- | :---------------------------------------------- |
| `primary`                 | `#5A5892` | <img src="https://via.placeholder.com/20/5A5892/000000?text=+" /> |
| `onPrimary`               | `#FFFFFF` | <img src="https://via.placeholder.com/20/FFFFFF/000000?text=+" /> |
| `primaryContainer`        | `#E2DFFF` | <img src="https://via.placeholder.com/20/E2DFFF/000000?text=+" /> |
| `onPrimaryContainer`      | `#424178` | <img src="https://via.placeholder.com/20/424178/000000?text=+" /> |
| `secondary`               | `#575992` | <img src="https://via.placeholder.com/20/575992/000000?text=+" /> |
| `onSecondary`             | `#FFFFFF` | <img src="https://via.placeholder.com/20/FFFFFF/000000?text=+" /> |
| `secondaryContainer`      | `#E1E0FF` | <img src-="https://via.placeholder.com/20/E1E0FF/000000?text=+" /> |
| `onSecondaryContainer`    | `#3F4178` | <img src="https://via.placeholder.com/20/3F4178/000000?text=+" /> |
| `tertiary`                | `#5B5891` | <img src="https://via.placeholder.com/20/5B5891/000000?text=+" /> |
| `onTertiary`              | `#FFFFFF` | <img src="https://via.placeholder.com/20/FFFFFF/000000?text=+" /> |
| `tertiaryContainer`       | `#E3DFFF` | <img src="https://via.placeholder.com/20/E3DFFF/000000?text=+" /> |
| `onTertiaryContainer`     | `#434078` | <img src="https://via.placeholder.com/20/434078/000000?text=+" /> |
| `error`                   | `#904A42` | <img src="https://via.placeholder.com/20/904A42/000000?text=+" /> |
| `background`              | `#FCF8FF` | <img src="https://via.placeholder.com/20/FCF8FF/000000?text=+" /> |
| `surface`                 | `#F9F9FF` | <img src="https://via.placeholder.com/20/F9F9FF/000000?text=+" /> |
| `onSurface`               | `#191C20` | <img src="https://via.placeholder.com/20/191C20/000000?text=+" /> |
| `onSurfaceVariant`        | `#45464F` | <img src="https://via.placeholder.com/20/45464F/000000?text=+" /> |
| `outline`                 | `#757680` | <img src="https://via.placeholder.com/20/757680/000000?text=+" /> |
| `outlineVariant`          | `#C5C6D0` | <img src="https://via.placeholder.com/20/C5C6D0/000000?text=+" /> |

#### Dark Theme Colors

| Role                      | Hex Code  | Color Preview                                   |
| :------------------------ | :-------- | :---------------------------------------------- |
| `primary`                 | `#C3C0FF` | <img src="https://via.placeholder.com/20/C3C0FF/000000?text=+" /> |
| `onPrimary`               | `#2B2A60` | <img src="https://via.placeholder.com/20/2B2A60/000000?text=+" /> |
| `primaryContainer`        | `#424178` | <img src="https://via.placeholder.com/20/424178/000000?text=+" /> |
| `onPrimaryContainer`      | `#E2DFFF` | <img src="https://via.placeholder.com/20/E2DFFF/000000?text=+" /> |
| `secondary`               | `#C0C1FF` | <img src="https://via.placeholder.com/20/C0C1FF/000000?text=+" /> |
| `onSecondary`             | `#292A60` | <img src="https://via.placeholder.com/20/292A60/000000?text=+" /> |
| `secondaryContainer`      | `#3F4178` | <img src="https://via.placeholder.com/20/3F4178/000000?text=+" /> |
| `onSecondaryContainer`    | `#E1E0FF` | <img src="https://via.placeholder.com/20/E1E0FF/000000?text=+" /> |
| `tertiary`                | `#C4C0FF` | <img src="https://via.placeholder.com/20/C4C0FF/000000?text=+" /> |
| `onTertiary`              | `#2C2960` | <img src="https://via.placeholder.com/20/2C2960/000000?text=+" /> |
| `tertiaryContainer`       | `#434078` | <img src="https://via.placeholder.com/20/434078/000000?text=+" /> |
| `onTertiaryContainer`     | `#E3DFFF` | <img src="https://via.placeholder.com/20/E3DFFF/000000?text=+" /> |
| `error`                   | `#FFB4AA` | <img src="https://via.placeholder.com/20/FFB4AA/000000?text=+" /> |
| `background`              | `#131318` | <img src="https://via.placeholder.com/20/131318/000000?text=+" /> |
| `surface`                 | `#111318` | <img src="https://via.placeholder.com/20/111318/000000?text=+" /> |
| `onSurface`               | `#E2E2E9` | <img src="https://via.placeholder.com/20/E2E2E9/000000?text=+" /> |
| `onSurfaceVariant`        | `#C5C6D0` | <img src="https://via.placeholder.com/20/C5C6D0/000000?text=+" /> |
| `outline`                 | `#8F909A` | <img src="https://via.placeholdercom/20/8F909A/000000?text=+" /> |
| `outlineVariant`          | `#45464F` | <img src="https://via.placeholder.com/20/45464F/000000?text=+" /> |

---
