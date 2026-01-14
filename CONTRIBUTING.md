# Contributing to dend

Thanks for your interest in contributing to Dend! Whether you're here to report a bug, suggest a new feature, or submit code, your help is very welcome.

## How to Contribute

### 1. Fork the repository

First, fork the repository to your own GitHub account and clone it locally:
```bash
git clone https://github.com/Godzuche/Dend.git
cd dend
```

### 2. Create a new branch

Always create a new branch from `main` for your work. This keeps the commit history clean.
```bash
git checkout -b your-feature-name
```

### 3. Make your changes

Make your improvements, fix bugs, or add new features.

Ensure your code:
* Follows standard Kotlin best practices.
* Adheres to Jetpack Compose conventions and Material 3 guidelines.
* Targets a minimum SDK of 29+ (as required by `CallScreeningService`).
* Avoids hardcoded strings (use string resources when possible).

### 4. Run the linter & tests

Before submitting, please use Android Studio to:
* Run the app and test your changes thoroughly on a physical or emulator device.
* Run `Code > Reformat Code` to ensure consistent formatting.
* Run `Analyze > Inspect Code` to catch any potential issues.

### 5. Commit and push

Commit your changes with a clear and descriptive message:

```bash
git commit -m "Feat: Add custom SMS reply feature"
or git commit -m "Fix: Corrected logic for Zen Mode"
git push origin your-feature-name
```


### 6. Open a pull request

Submit your pull request to the main`dend` repository via GitHub. Please be sure to include:
* A clear description of the problem you are solving or the feature you are adding.
* Any related issue number (e.g., `Closes #12`, `Fixes #8`).
* Screenshots or a screen recording if your changes affect the UI.

## Code Style Guidelines

We follow the standard Kotlin style conventions enforced by Android Studio's default formatter. Please run `Code > Reformat Code` before committing your changes.

## Reporting Issues

If you've found a bug or have a feature request:
* First, search the [issues](https://github.com/Godzuche/dend/issues) to see if a similar one already exists.
* If not, open a new issue with a clear title and a detailed description. If reporting a bug, include steps to reproduce it.

## Discussions

Not ready to contribute code? That’s fine too!
* Join our [GitHub Discussions](https://github.com/Godzuche/dend/discussions) to suggest ideas, provide feedback, or ask questions about the project.

## Thank You!

Your help makes DenD better for everyone. We appreciate every contribution, big or small.
