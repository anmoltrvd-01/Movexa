# Movexa

Movexa is a modern fitness and hydration tracking application for Android, built with a focus on smooth user experience, clean code, and scalable architecture.

## 🚀 Features

-   **Activity Tracking**: Monitor your daily physical activities and progress.
-   **Hydration Tracking**: Stay hydrated with dedicated water intake monitoring.
-   **Health Insights**: Detailed views for Vitality, Heart Rate, Sleep patterns, and Stress levels.
-   **Smart Notifications**: Never miss a goal with timely alerts.
-   **Onboarding & Auth**: Secure authentication flow including Login, Sign Up, and OTP verification.
-   **Adaptive UI**: Built with Jetpack Compose and Material3 Adaptive Navigation Suite for various screen sizes.
-   **Smooth Transitions**: Utilizes Shared Element Transitions for a premium feel.

## 📸 Screenshots

| Splash | Login | Sign Up |
| :---: | :---: | :---: |
| <img src="screenshots/SplashScreen.jpeg" width="280"> | <img src="screenshots/login.jpeg" width="280"> | <img src="screenshots/signup.jpeg" width="280"> |

| Home (Top) | Home (Bottom) | Heart Rate |
| :---: | :---: | :---: |
| <img src="screenshots/home_1.jpeg" width="280"> | <img src="screenshots/home_2.jpeg" width="280"> | <img src="screenshots/heart.jpeg" width="280"> |

| Sleep Analysis | Activity Tracking |
| :---: | :---: |
| <img src="screenshots/sleep.jpeg" width="280"> | <img src="screenshots/activity.jpeg" width="280"> |

## 🛠 Tech Stack

-   **Language**: [Kotlin](https://kotlinlang.org/)
-   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
-   **Architecture**: Clean Architecture with MVVM
-   **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
-   **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
-   **Adaptive Layouts**: Material3 Adaptive Navigation Suite
-   **Animations**: Shared Element Transitions (Compose)
-   **Local Storage**: (e.g., Room / DataStore - *Add if applicable*)
-   **Network**: (e.g., Retrofit / Ktor - *Add if applicable*)

## 🏗 Architecture

The project follows **Clean Architecture** principles, separating concerns into:
-   **Data**: Implementation of repositories and data sources.
-   **Domain**: Business logic, Use Cases, and Repository interfaces.
-   **Presentation**: UI (Compose) and ViewModels.

## 🗺 KMP Migration
A migration to **Kotlin Multiplatform (KMP)** is currently planned to bring Movexa to other platforms.

## 🏁 Getting Started

1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/movexa-android.git
    ```
2.  Open the project in **Android Studio (Ladybug or newer)**.
3.  Add your `google-services.json` to the `app/` directory (if using Firebase).
4.  Build and run!

---
*Created with ❤️ by the Movexa team.*
