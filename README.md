# QuarterEdge — Intraday Breakout Research Platform for CL Futures

QuarterEdge is a high-performance intraday research engine that models structured price behavior in Crude Oil (CL) futures. Using DR/IDR breakout logic, Quarter-Theory precision, and ML filters, it identifies high-probability intraday setups in CL futures with lightning-fast backtesting, performance dashboards, and live trading integration. Built in Java for speed, reproducibility, and production-grade engineering.

# Features
## Completed (MVP v0.1)

- 1-minute CL data ingestion (5+ years of historical test data) ✅
- Backtesting Engine ✅
- Gradle build with: 

    - modern toolchain ✅

    - JUnit 5 (Jupiter) tests ✅

    - CI pipeline (GitHub Actions) ✅

    - Checkstyle config ✅

## In Progress (v0.2)
- DR/IDR session detection

- Quarter-grid level mapping

- Break → Retest → Confirm logic

- Performance tracking engine with:

    - win rate

    - expectancy

    - R-multiple distribution

    - max drawdown

## Planned (v0.3+)


- 15-minute ATR regime analysis

- Volatility-aware breakout filter

- Data abstraction layer for multiple instruments

- Probability-weighted target sizing

- React dashboard for visualization

- Live-trading bridge (websocket + REST wrapper)

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
└── results + reporting
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


### v0.2.0 — Volatility Regime + Filters

- 15-minute ATR

- Range-to-volatility ratio

- Pre-breakout context checks

### v0.3.0 — ML Trade/Skip Classifier

Feature engineering

Model integration in Java

Confidence scoring

### v0.4.0 — Visualization Layer

Streamlit or React dashboard

charts + analytics

### v0.5.0 — Live-Trading Bridge

REST + WebSocket adapter

Simulation of slippage & market hours
