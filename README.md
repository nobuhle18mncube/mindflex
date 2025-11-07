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
[▶️ Watch on YouTube]([https://youtu.be/WwkTLGWo6GU](https://youtu.be/WwkTLGWo6GU))

| Login Screen | Dashboard | News (Offline) |
|---------------|------------|----------------|
| ![Login Screen](https://github.com/user-attachments/assets/285f64d5-fb77-4fe5-a288-22c5641051bf) | ![Dashboard](https://github.com/user-attachments/assets/53490c96-dec8-47c6-bc16-068bb21e6697) | ![News](https://github.com/user-attachments/assets/a9d4b75c-7019-4dc1-a09c-5c8e4adb9f92)) |

| Notes | Quizzes | Settings |
|--------|----------|-----------|
| ![Notes](https://github.com/user-attachments/assets/d1291dc9-f02c-4385-864f-e3903e389992) | ![Quizzes](https://github.com/user-attachments/assets/6b456200-eece-4241-8438-943914bb7312) | ![Settings](https://github.com/user-attachments/assets/0d9b7eba-829e-4f31-8938-7e4719f971d9) |


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
