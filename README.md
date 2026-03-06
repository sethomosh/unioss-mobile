# UniOSS Mobile 📡

A native Android companion app for the **UniOSS Network Management System** — a real-time network monitoring platform built to track wireless devices, signal quality, and network health across a PTMP (Point-to-Multipoint) infrastructure.

Built in **Kotlin + Jetpack Compose** as part of a full-stack network monitoring suite.

---

## Screenshots

> Dashboard | Devices | Towers | Alerts | Settings

*(screenshots coming soon)*

---

## Features

- 📊 **Dashboard** — live overview of device health, bandwidth usage, active alerts and system status
- 📡 **Devices** — full device list with CPU, memory and uptime per device, searchable, color-coded by status
- 🗼 **Towers** — tower APs grouped with their connected remote clients, RSSI and signal % per client, expandable cards
- 🔔 **Alerts** — filterable alert feed (Critical / Warning / Cleared) with severity color coding
- ⚙️ **Settings** — configurable backend URL, auto-refresh interval, connection tester

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM |
| Navigation | Jetpack Navigation Compose |
| Networking | Retrofit2 + OkHttp |
| Async | Kotlin Coroutines + StateFlow |
| Theme | Custom dark NOC-style theme |

---

## Architecture
```
com.example.unioss_mobile/
├── screens/          # All 5 screen composables
├── navigation/       # NavGraph + Screen routes
├── utils/            # Auto-refresh utility
└── ui/theme/         # Colors, typography, theme
```

---

## Backend

This app is the mobile client for the **UniOSS** backend system — a custom-built network monitoring platform featuring:

- SNMP polling via PySNMP + APScheduler
- FastAPI REST backend
- MySQL metrics storage
- Docker containerized deployment
- Real-time signal threshold alerting

Backend repo: [unioss](https://github.com/sethomosh)

---

## Getting Started

1. Clone the repo
2. Open in Android Studio
3. Run on emulator or physical device (API 26+)
4. In Settings, configure your backend URL
5. Start your UniOSS Docker stack
6. Hit **Test Connection**

---

## Developer

Built by **Zeph** — [github.com/sethomosh](https://github.com/sethomosh)
