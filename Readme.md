# Simple Barcode Scanner

Simple Barcode Scanner is a lightweight yet powerful utility for Android, designed to provide a seamless and intuitive barcode scanning experience. Built entirely with modern, native Android technologies, the app offers a clean, responsive, and user-friendly interface for decoding a wide array of barcode formats. Its primary function is to quickly scan and parse data, with a special focus on formatting complex information, such as the data from a driver's license, into a human-readable format.

The application is architected using the latest best practices recommended by Google. The entire user interface is crafted with Jetpack Compose and Material 3, creating a fully declarative and dynamic user experience. At its core, the scanning capability is powered by Google's ML Kit Vision library, ensuring fast and accurate barcode detection. App settings and state are managed reactively using a combination of Jetpack ViewModel and DataStore, which persist user preferences like language and scanner settings efficiently. Navigation is handled cleanly by Compose Navigation, creating a robust single-activity architecture.

### Key Features:
*   **Multi-Format Scanning:** Supports a wide variety of formats, including QR Code, PDF417, UPC, EAN, Data Matrix, and more.
*   **Driver's License Parsing:** Intelligently formats the complex data from AAMVA-compliant driver's licenses into an easily readable list.
*   **Dynamic Language Support:** Instantly switch between English, Spanish, Italian, and Ukrainian without restarting the app.
*   **Modern UI:** A clean and intuitive interface built with the latest Material 3 design principles.
*   **User Settings:** Includes options to enable or disable the scanner's auto-zoom feature.
*   **Share Functionality:** Easily share the raw, unformatted data from any scanned barcode.

### Architecture

```mermaid
graph TD
    subgraph "MainActivity (Activity Entry Point)"
        MA[MainActivity]
    end

    subgraph "Navigation (Compose)"
        AN[AppNavigation]
    end

    subgraph "UI Screens (Composables)"
        HS[HomeScreen]
        RS[ResultScreen]
        SS[SettingsScreen]
        SHS[ScanHistoryScreen]
    end

    subgraph "UI Components (Composables)"
        TAM[TopAppBarMenu]
        AD[AboutDialog]
        SHI[ScanHistoryItem]
    end

    subgraph "ViewModel (Business Logic)"
        SVM[SettingsViewModel]
        SHVM[ScanHistoryViewModel]
    end

subgraph "Data Layer"
SR[SettingsRepository]
DS[DataStore Preferences]
SHR[ScanHistoryRepository]
DB["ScanHistoryDatabase (Room)"]
end

%% -- Connections --

MA -- "Holds language state" --> AN

%% Navigation
AN -- "Controls navigation to" --> HS
AN -- "Controls navigation to" --> RS
AN -- "Controls navigation to" --> SS
AN -- "Controls navigation to" --> SHS

%% UI and Component Usage
HS -- "Uses" --> TAM
HS -- "Uses" --> AD
RS -- "Uses" --> TAM
RS -- "Uses" --> AD
SHS -- "Uses" --> SHI

%% Settings Flow
HS -- "Depends on" --> SVM
SS -- "Depends on" --> SVM
SVM -- "Depends on" --> SR
SR -- "Reads/Writes to" --> DS
SS -- "Updates language state in" --> MA

%% Scan History Flow
HS -- "Writes to" --> SHR
SHS -- "Depends on" --> SHVM
SHVM -- "Depends on" --> SHR
SHR -- "Reads/Writes to" --> DB


```