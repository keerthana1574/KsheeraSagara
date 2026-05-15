# 🐄 Ksheera Sagara - Dairy Profit/Loss Calculator

![Android](https://img.shields.io/badge/Android-7.0%2B-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-orange)
![License](https://img.shields.io/badge/License-Academic-blue)

## 📌 Project Overview

**Ksheera Sagara** (meaning "Ocean of Milk" in Sanskrit) is an Android-based dairy financial management application designed specifically for small and medium-scale dairy farmers in India.

### 🎯 Problem Statement

Despite India being the world's largest milk producer, most dairy farmers operate without any formal financial tracking system. While milk income is recorded through cooperative society slips, **expenses are almost never systematically recorded**. This creates a critical blind spot where farmers unknowingly operate at a loss despite consistent milk production.

### 💡 Solution

Ksheera Sagara bridges this gap by providing an intuitive mobile interface that enables farmers to:
- Log daily milk income with fat percentage and auto-calculation
- Record categorized expenses (Fodder, Medicine, Labor, Electricity, Misc)
- Instantly visualize net profit/loss through color-coded indicators
- Analyze cow-wise profitability
- Generate monthly PDF reports
- Work completely offline in rural low-bandwidth environments

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🔐 **User Authentication** | Secure login/registration using Firebase Auth |
| 💰 **Milk Income Entry** | Record quantity (liters), fat %, auto-calculated payment |
| 📊 **Expense Tracking** | 5 categories with date picker, amount, cow ID, description |
| 📈 **Dashboard** | Real-time net profit/loss with green (profit) / red (loss) indicators |
| 🥧 **Expense Pie Chart** | Visual distribution of expenses by category |
| 📊 **Cow-wise Analytics** | Bar chart showing profit/loss per individual cow |
| 📄 **PDF Reports** | Generate monthly financial summaries with one tap |
| ☁️ **Cloud Backup** | Automatic sync to Firebase Firestore |
| 📱 **Offline First** | Works without internet; syncs when online |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Kotlin** | Primary programming language |
| **Android Jetpack** | ViewModel, LiveData, Navigation |
| **Firebase Auth** | User authentication |
| **Firebase Firestore** | Cloud database with user separation |
| **MPAndroidChart** | Pie charts & bar charts |
| **iText7** | PDF report generation |
| **Material Design 3** | UI components |

---

## 🚀 Installation & Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK API 24+
- Java 11 or later
- Firebase account (free tier)

### Step 1: Clone the Repository

```bash
git clone https://github.com/keerthana1574/KsheeraSagara.git
cd KsheeraSagara
Step 2: Add Firebase Configuration
Create a project in Firebase Console

Register your Android app with package name com.example.ksheerasagara

Download google-services.json and place it in the app/ folder

Enable Authentication (Email/Password) in Firebase Console

Enable Firestore Database (start in test mode)

Step 3: Build and Run
bash
# Open the project in Android Studio
# Sync Gradle files
# Connect an Android device (API 24+) or start emulator
# Click Run (▶️)
Alternative: Install APK Directly
Download the APK from the link below and install on your Android phone:

text
[Insert your Cloud Storage or Firebase Hosting URL here]
⚠️ Note: Enable "Install from unknown sources" in your phone settings.

📁 Project Structure
text
KsheeraSagara/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/ksheerasagara/
│   │   │   ├── LoginActivity.kt          # User authentication
│   │   │   ├── RegisterActivity.kt       # New user registration
│   │   │   ├── MainActivity.kt           # Main container with drawer
│   │   │   ├── data/                     # Data models
│   │   │   │   ├── MilkEntry.kt          # Income data class
│   │   │   │   ├── ExpenseEntry.kt       # Expense data class
│   │   │   │   └── FinanceRepository.kt  # Firebase operations
│   │   │   ├── firebase/                 # Firebase helpers
│   │   │   │   ├── FirebaseAuthManager.kt
│   │   │   │   └── FirestoreSyncManager.kt
│   │   │   ├── ui/                       # UI Fragments
│   │   │   │   ├── DashboardFragment.kt
│   │   │   │   ├── IncomeFragment.kt
│   │   │   │   ├── ExpensesFragment.kt
│   │   │   │   ├── AnalyticsFragment.kt
│   │   │   │   └── ReportsFragment.kt
│   │   ├── res/                          # Layouts and resources
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts                  # Module dependencies
│   └── google-services.json              # Firebase config
├── build.gradle.kts                       # Project dependencies
├── gradle-wrapper.properties              # Gradle version
└── README.md                              # This file
🔧 Configuration
Firebase Security Rules
javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      match /milk_entries/{entry} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
      match /expense_entries/{entry} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
🧪 How to Use the App
1. Create an Account
Tap "New user? Create Account"

Enter Name, Email, Password (min 6 characters)

After registration, login with your credentials

2. Add Milk Income
Go to Income tab

Enter Cow ID / Name (optional)

Enter Milk (liters), Fat (%), Base Rate

Estimated payment appears automatically

Tap + SAVE ENTRY

3. Add Expense
Go to Expenses tab

Select date, category, amount

Optional: Cow ID, Cow Name, Description

Tap + SAVE EXPENSE

4. View Dashboard
See NET PROFIT/LOSS (green = profit, red = loss)

View total income and expenses

Check recent transactions

5. Analytics
Expense Distribution - Pie chart by category

Cow-wise Profit/Loss - Bar chart per animal

6. Generate PDF Report
Go to Reports tab

Select month/year

Tap DOWNLOAD REPORT

PDF saved to device storage

7. Logout
Tap menu (☰) icon

Tap Logout

📊 Data Flow
text
User Input → ViewModel → FirestoreSyncManager → Firebase Firestore
                                                         ↓
Dashboard ← LiveData ← ViewModel ← FirestoreSyncManager ←┘
Each user's data is isolated using their unique userId in Firestore.

🔄 Future Enhancements
AI-based feed optimization recommendations

Predictive profitability alerts

Multi-language support (Kannada, Hindi)

WhatsApp report sharing

Cloud backup with Google Drive

Home screen widget for quick profit view

👨‍💻 Developer Information
Detail	Information
Name	: Keerthana M
USN :	1AP22CS022
College :	APS College of Engineering
Course	 : Android App Development using GenAI
Project Number :	29
Group :	Prospectors g5
📞 Contact
Email: kkpmahadev8@gmail.com

GitHub: keerthana1574

Project Repository: KsheeraSagara
📄 APK Download
![Download Ksheera Sagara APK](https://storage.googleapis.com/ksheera-apk-bucket/app-release.apk)




🙏 Acknowledgments
MPAndroidChart library for beautiful charts

iText7 for PDF generation

Firebase for backend services

APS College of Engineering for project support

📝 License
This project is for academic purposes only. All rights reserved.
