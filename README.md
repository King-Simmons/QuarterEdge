# QuarterEdge — Intraday Breakout Research Platform for CL Futures

QuarterEdge is a high-performance intraday research engine that models structured price behavior in Crude Oil (CL) futures. Using DR/IDR breakout logic, Quarter-Theory precision, and ML filters, it identifies high-probability intraday setups in CL futures with lightning-fast backtesting, performance dashboards, and live trading integration. Built in Java for speed, reproducibility, and production-grade engineering.

## Architecture Overview
````
QuarterEdge
│
├── data ingestion
│     └── minute bars → normalized → model-ready
│
├── strategy core
│     ├── range session model (DR/IDR)
│     ├── quarter-level engine
│     └── breakout detector
│
├── backtesting engine
│     ├── entry/exit order simulation
│     ├── take profit and stop loss handling
│     └── performance statistics
│
├── ML layer (planned)
│     └── trade/skip probability & confidence scoring
│
├── results + reporting
│ 
├── summary metrics
├── CSV output
└── future charts/dashboard
````

## Technology Stack

- Java 25 (core engine)

- Gradle 9.x (build system)

- JUnit Jupiter 5 (testing)

- Checkstyle (code quality)

- GitHub Actions (CI)

- MIT License

- Coming soon:

  - Python notebooks for ML exploration
  - React for dashboard
  - Rust for Live Trading Implementation

## Installation

Clone the repo:
````
git clone https://github.com/King-Simmons/QuarterEdge.git
cd QuarterEdge
````

Build:

````
./gradlew clean build
````

Run:
````
./gradlew run
````

Running Tests
````
./gradlew test
````

## Roadmap
### v0.1.0 — Backtest MVP (🚀 current)

- Backtesting Engine
- Basic EMA Crossover Strategy Implementation
- CSV Data Ingestion


### v0.2.0 — Basic Breakout Strategy + Trading Filters

- 15-minute ATR
- IDR/DR Ranges
- Quarter Theory Entries
- IDR + QT Breakout Strategy Implementation
- Performance Engine

### v0.3.0 — ML Trade/Skip Classifier

- Machine Learning Filters
- AI Integration
- Confidence scoring

### v0.4.0 — Visualization Layer

- React dashboard
- Charts + analytics
- Simulations + Projections

### v0.5.0 — Live-Trading Bridge

- REST + WebSocket adapter
- Tradovate Integration

## Motivation

Intraday futures behavior (especially CL) exhibits repeatable structural patterns during the trading day:

- Liquidity cycles around 8:30 CT & 9:00 CT

- Pre-market compression

- Range boundaries (DR/IDR)

- Quarter-level magnets and rejection zones

- Volatility-driven breakout success/failure rates

QuarterEdge aims to formalize, measure, and model these behaviors with engineering discipline.

## Disclaimer

This project is for research and educational purposes only.
Nothing here constitutes financial advice.
Trading futures involves the significant risk of loss.

## Author
King Simmons

Software Engineer

www.linkedin.com/in/kingsimmons

## ⭐ Support the Project

If you like the work, star ⭐ the repo — it helps visibility and motivates development.


