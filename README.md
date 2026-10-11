# 📝 Transcriptu

A modern, native Android application built entirely with **Jetpack Compose** that allows users to instantly extract, read, and manage transcripts from any YouTube video. 

Transcriptu features a clean, intuitive user interface and leverages a robust, scalable architecture under the hood, making it a fast and reliable tool for content creators, students, and researchers.

## 📸 Screenshots

<!-- REPLACE THE LINKS BELOW WITH YOUR ACTUAL IMAGE LINKS -->
<div align="center">
  
  <img src="https://github.com/user-attachments/assets/1067bcb6-70dc-461d-b2f7-d9ac56475286" width="22%" alt="Language Selection" />

  <img src="https://github.com/user-attachments/assets/2847d2e4-9390-4390-bfc6-a14d01df0746" width="22%" alt="Language Selection" />
  <img src="https://github.com/user-attachments/assets/68348de1-23b7-4944-bf72-88cdf1346968" width="22%" alt="Home Screen" />

  <img src="https://github.com/user-attachments/assets/19c245e7-71d6-442c-b6df-7d5962e76cb2" width="22%" alt="Transcript View" />
</div>

## 🎥 App Demo

<!-- REPLACE THE LINK BELOW WITH YOUR ACTUAL VIDEO LINK -->
<div align="center">
  <a href="https://www.youtube.com/watch?v=e9x5puKyMpY">
    <img src="https://img.youtube.com/vi/e9x5puKyMpY/maxresdefault.jpg" 
         alt="Silent Zone Demo Video" 
         width="600">
  </a>
  <br>
  <em>👆 Click to watch full demo on YouTube</em>
</div>



## ✨ Key Features

* **Instant Transcript Extraction:** Paste any YouTube URL to fetch the complete video transcript and metadata (Thumbnail, Title, Channel).
* **Timestamp Toggling:** Read as a plain paragraph or toggle timestamps to track exact video segments.
* **In-Text Search:** Quickly locate specific keywords or phrases within long transcripts.
* **Multi-Language Support:** Fetch transcripts in various available languages (English, Hindi, Spanish, French, etc.) via a dynamic bottom sheet selection.
* **One-Tap Copy:** Copy the entire transcript (with or without timestamps) directly to the clipboard.
* **Secure API Key Management:** Users can safely store and manage their own RapidAPI key directly within the app settings.
* **Material Design 3:** fully responsive layout with seamless Light and Dark mode support.

## 🛠 Tech Stack & Architecture

This project is built using modern Android development tools and best practices:

* **Language:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
* **Architecture:** **MVVM** (Model-View-ViewModel) with unidirectional data flow (StateFlow)
* **Dependency Injection:** [Koin](https://insert-koin.io/) 
* **Networking:** 
  * [Retrofit](https://square.github.io/retrofit/) & [OkHttp](https://square.github.io/okhttp/) for API requests
  * [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON parsing
* **Asynchronous Programming:** Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
* **Local Storage:** [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (for secure, async API key storage)
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/compose/)
* **HTML Parsing:** [Jsoup](https://jsoup.org/) (for robust extraction of OpenGraph video metadata)
* **Navigation:** Navigation3 Compose

## 🚀 Getting Started

### Prerequisites
* Android Studio (Koala or newer recommended)
* Minimum SDK: 26 (Android 8.0)
* Target SDK: 37

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Surajsinghyadav/Transcriptu
