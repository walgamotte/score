# 🥏 Multi-Player Disc Golf Scorecard App

A lightweight, native Android application designed to track scores for multiple players during an 18-hole disc golf round. Built entirely using **Kotlin** and **Jetpack Compose** with Material 3 design principles.

---

## 📲 How to Download & Install (For Friends)

You do not need Android Studio or any coding tools to install this app. Just follow these steps directly on your Android phone:

1. **Download the App**: 
   * Navigate to the **[Releases](https://github.com)** section on the right side of this GitHub page.
   * Tap on the latest release and download the attached `DiscGolfScorecard.apk` file.
2. **Open the File**: Once the download completes, tap the file in your notification bar or open it from your phone's **Downloads** folder.
3. **Allow Installation**:
   * If your phone says your browser isn't allowed to install unknown apps, tap **Settings** in the prompt and toggle on **Allow from this source**.
   * If a **Google Play Protect** warning pops up saying "Blocked by Play Protect" or "Unknown Developer," simply tap **More details** and then select **Install anyway**.
4. **Play!** The app will install and appear in your app drawer just like any other app.

---

## ✨ Features
* **Multi-Player Support**: Type in names to dynamically add as many players to your card as you want.
* **Live Leaderboard**: Displays an automated board at the top that continuously updates and sorts players from best to worst score.
* **Custom Hole Pars**: Adjust any individual hole's par (from Par 2 up to Par 6) on the fly using intuitive plus/minus adjustments.
* **Smart UI Color Coding**: Instantly highlights under-par hole scores in **green** (birdies/eagles) and over-par scores in **red** (bogeys).
* **One-Tap Wipe**: Quickly clear the entire scorecard and reset all hole pars to start a completely new round.

---

## 🛠️ Developer Setup (Compiling from Source)

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

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
