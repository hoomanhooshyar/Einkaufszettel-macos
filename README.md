# 🛒 Einkaufszettel (Shopping List App)

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-000000.svg?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/firebase-%23039BE5.svg?style=for-the-badge&logo=firebase)
![Koin](https://img.shields.io/badge/Koin-FF3254?style=for-the-badge&logo=kotlin&logoColor=white)
![Android CI](https://github.com/hoomanhooshyar/Einkaufszettel-macos/actions/workflows/ci.yml/badge.svg)

Eine moderne, plattformübergreifende Einkaufslisten-App, entwickelt mit **Kotlin Multiplatform (KMP)** und **Compose Multiplatform**. Die App bietet eine nahtlose Offline-First-Erfahrung mit Hintergrund-Synchronisation zu Firebase.

## 📱 Screenshots

<div align="center">
  <!-- Ersetze die Links durch deine tatsächlichen Screenshot-URLs -->
  <img src="link_to_home_screenshot" width="24%" />
  <img src="link_to_detail_screenshot" width="24%" />
  <img src="link_to_settings_screenshot" width="24%" />
  <img src="link_to_darkmode_screenshot" width="24%" />
</div>

## ✨ Hauptfunktionen (Features)

* **Offline-First Architektur:** App funktioniert komplett ohne Internet. Daten werden lokal gespeichert und synchronisiert, sobald eine Verbindung besteht.
* **Echtzeit-Synchronisation:** Nahtlose Cloud-Speicherung und Abruf von Daten mittels Firebase Firestore.
* **Authentifizierung:** Sicheres Login und Logout über Firebase Authentication.
* **Dynamische Suche:** Schnelles Filtern und Suchen von Einkaufslisten in Echtzeit.
* **Mehrsprachigkeit:** Vollständige Unterstützung für Deutsch und Englisch (im App-Menü umschaltbar).
* **Cross-Platform:** Ein einziger Codebase (UI & Business Logic) für Android und iOS dank KMP).

## 🛠 Technologien & Architektur

Die App wurde unter Berücksichtigung von Best Practices und **Clean Architecture** Prinzipien entwickelt.

* **UI:** Jetpack Compose / Compose Multiplatform
* **Architektur-Pattern:** MVVM (Model-View-ViewModel) mit UDF (Unidirectional Data Flow)
* **Dependency Injection:** Koin
* **Asynchrone Programmierung:** Kotlin Coroutines & StateFlow / SharedFlow
* **Lokale Datenbank:** Room (Multiplatform) / SQLite
* **Remote Backend:** Firebase (Auth & Firestore)
* **Testing:** Umfassende Unit Tests für Repositories und UseCases (mit Fake-Implementierungen).

## 🚀 Lokale Installation & Setup

Um das Projekt lokal auszuführen, folge diesen Schritten:

1. **Repository klonen:**
   ```bash
   git clone [https://github.com/hooman-hooshyar/einkaufszettel.git](https://github.com/hooman-hooshyar/einkaufszettel.git)
Firebase Setup:

Erstelle ein Projekt in der Firebase Console.

Füge eine Android-App hinzu und lade die google-services.json Datei herunter.

Platziere die Datei im Verzeichnis composeApp/src/androidMain/ (oder entsprechend deiner Modulstruktur).

Projekt in Android Studio öffnen:

Verwende Android Studio (vorzugsweise die neueste Version) mit dem Kotlin Multiplatform Plugin.

Führe einen Gradle Sync durch.

App ausführen:

Wähle die composeApp Run-Configuration und drücke auf Run (Android Emulator oder physisches Gerät).

🧠 Architektur-Entscheidungen
Besonderes Augenmerk wurde auf den Synchronisationsmechanismus gelegt:

Ein dedizierter SyncDatabaseUseCase verwaltet Konflikte zwischen der lokalen Datenbank und Firestore.

Status-Flags (LSL für lokal, SYNCED für synchronisiert) garantieren, dass bei Verbindungsausfällen keine Daten verloren gehen.

Die UI reagiert reaktiv auf Datenbankänderungen mittels Flow<T>, was manuelle UI-Updates überflüssig macht.

🔮 Geplante Funktionen (Roadmap)
Dieses Projekt wird kontinuierlich weiterentwickelt. Folgende Features sind für zukünftige Updates geplant:

KI-Integration (Künstliche Intelligenz): Ein intelligenter Assistent, der das Einkaufsverhalten analysiert und dem Nutzer smarte Empfehlungen für einen besseren und effizienteren Einkauf gibt.

Sprachsteuerung & Speech-to-Text: Freihändige Bedienung der App! Hinzufügen von Produkten und Steuern der App komplett über Sprachbefehle, unterstützt durch moderne KI-Sprachmodelle.

iOS-Unterstützung: Kompilierung der App für iOS durch Ausnutzung der Kotlin Multiplatform Fähigkeiten.

Kollaborative Listen (Teilen): Einkaufslisten mit Familienmitgliedern oder Freunden teilen und in Echtzeit gemeinsam bearbeiten.

Push-Benachrichtigungen: Erinnerungen an unvollständige Einkaufslisten oder Updates bei geteilten Listen.

Kategorisierung & Sortierung: Intelligente Gruppierung von Produkten (z.B. Obst, Milchprodukte) für einen schnelleren Einkauf.

👨‍💻 Autor
Hooman Hooshyar

GitHub: @hooman-hooshyar

Feedback, Issues und Pull Requests sind jederzeit willkommen!