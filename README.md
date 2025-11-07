# 🧠 MindFlex

**MindFlex** is a comprehensive Android application designed to help you organize your thoughts, manage tasks, stay updated with news, and test your knowledge with quizzes.  
It features persistent login, offline-first news caching, and full multi-language support.

---

## ✨ Features

- **🔐 Secure User Authentication:**  
  Sign up, log in with Email/Password or Google. Your session is remembered — no need to log in every time.

- **📰 Offline-First News Feed:**  
  Fetches top headlines from the **GNews API**. Articles are cached locally using **RoomDB**, so you can read them even without internet access.

- **🔄 Pull-to-Refresh:**  
  Manually refresh the news feed at any time.

- **🗒️ Note Taking:**  
  Create, view, and manage your personal notes — powered by **Supabase**.

- **📋 Task Manager:**  
  Keep track of your to-do items.

- **🎯 Quizzes:**  
  Test your knowledge with fun trivia from **OpenTdb**.

- **🌍 Multi-Language Support:**  
  Change the app's language from the Settings screen (supports English, IsiZulu, IsiXhosa, SeSotho).

- **🔔 Background Notifications:**  
  A **WorkManager** job periodically checks for new articles and sends push notifications if something new is found.

---

## 🎥 Demo & Screenshots

📺 **Watch the demo video:**  
[▶️ Watch on YouTube](https://your-demo-link-here.com)

| Login Screen | Dashboard | News (Offline) |
|---------------|------------|----------------|
| ![Login](assets/login.png) | ![Dashboard](assets/dashboard.png) | ![News](assets/news.png) |

| Notes | Quizzes | Settings |
|-------|----------|-----------|
| ![Notes](assets/notes.png) | ![Quizzes](assets/quizzes.png) | ![Settings](assets/settings.png) |

---

## 🛠 Tech Stack

| Category | Technology |
|-----------|-------------|
| **Language** | Kotlin |
| **Architecture** | MVVM (Repository pattern) |
| **Local Storage** | RoomDB |
| **Networking** | Retrofit2 & Gson |
| **Authentication** | Firebase Auth (Email/Password & Google) |
| **Backend (Notes)** | Supabase REST API |
| **Background Tasks** | WorkManager |
| **Notifications** | Firebase Cloud Messaging (FCM) & Local Notifications |

---

## 🚀 Getting Started

### 🧩 Prerequisites

- [Android Studio (latest version)](https://developer.android.com/studio)
- A **Firebase** project
- A **GNews API key**
- A **Supabase** project

---

### ⚙️ Installation & Setup

**1️⃣ Clone the repository:**

```bash
git clone https://github.com/nobuhle18mncube/mindflex
