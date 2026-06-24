
# 🥏 Disc Golf Scorecard App

A lightweight, native Android application designed to quickly track scores during an 18-hole disc golf round. Built entirely using **Kotlin** and **Jetpack Compose** with Material 3 design principles.

## ✨ Features
* **Live Score Summary**: Tracks total strokes, course par, and dynamically calculates your relative score (e.g., -2, E, +4).
* **Smart UI Color Coding**: Instantly highlights under-par holes in **green** (birdies/eagles) and over-par holes in **red** (bogeys).
* **Clean Scannability**: Uses a highly efficient `LazyColumn` scrolling layout for smooth performance across all 18 holes.
* **One-Tap Reset**: Quickly wipe the scorecard to start a brand new round with a single button press.

## 🛠️ Built With
* **Language**: [Kotlin](https://kotlinlang.org)
* **UI Framework**: [Jetpack Compose](https://android.com)
* **Design Language**: Material Design 3

## 📲 How to Install and Run

### Prerequisites
* Android Studio (Ladybug or newer)
* An Android device running Android 8.0 (API 26) or higher

### Steps to Run:
1. **Clone the Repository**:
   ```bash
   git clone https://github.com
   ```
2. **Open in Android Studio**: Launch Android Studio, select **Open**, and navigate to the cloned folder.
3. **Enable USB Debugging**: On your phone, go to *Settings > About Phone* and tap *Build Number* 7 times. Go back to *Developer Options* and turn on *USB Debugging*.
4. **Run the App**: Connect your phone via USB, select your device from the top toolbar dropdown in Android Studio, and click the green **Run (▶)** button.

## 🚀 Future Roadmap
* Support for multiple players on the same scorecard.
* Custom course setups to change individual hole pars.
* Local database storage to save round history and track personal statistics.

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
