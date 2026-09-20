# Multithreading Systems

A Java-based data processing project for CS378: Cloud Computing focused on streaming, validation, batching, and map/reduce-style processing over large taxi-related datasets.

## Team

- Sanchana Shanmuga — ss229638
- Victoria Reddy — vrr593

## Course

- CS378 - Cloud Computing
- Unique Number: 51515

## Project Overview

This project explores the practical challenges of processing large datasets efficiently in a memory-conscious way. Instead of loading everything into memory at once, the program reads a compressed dataset, validates records, filters malformed entries, and writes valid data into chunked output files for downstream processing.

The implementation follows a pipeline inspired by map/reduce patterns:

- parse a large input file
- validate records
- accumulate valid rows in batches
- sort valid data into smaller files
- prepare data for further aggregation or analysis

## Core Concepts Learned

### 1. Streaming file processing
Large files are handled line by line rather than as one giant in-memory structure. This makes the workflow more scalable and avoids excessive memory usage.

### 2. Data validation
The program checks whether each record has the expected format, enough columns, and parseable values before accepting it as valid.

### 3. Batching and chunking
Rather than processing everything at once, the dataset is partitioned into manageable chunks. This is a common strategy used in distributed and parallel systems.

### 4. Sorting and compaction
Valid records are sorted and stored into multiple smaller files. This helps create a more structured and efficient processing flow for later reduction steps.

### 5. Map/reduce-inspired workflow
Although the project is not a full distributed cluster system, it follows the same high-level philosophy of mapping input data into intermediate structured outputs, then reducing or processing those outputs further.

## System Workflow

```mermaid
flowchart LR
    A[Compressed Input File] --> B[Read Line by Line]
    B --> C[Validate Records]
    C --> D[Keep Valid Records]
    D --> E[Sort by Fare Value]
    E --> F[Write miniSortedFile# CSVs]
    F --> G[Reduce / Aggregate Later]
```

## What the Program Does

The Java entry point loads a compressed dataset and runs a data preparation pipeline:

- reads `.bz2` compressed input
- validates each row
- records the number of valid and invalid lines
- groups valid rows into sorted output files
- writes those results to files such as `miniSortedFile1.csv`, `miniSortedFile2.csv`, and so on

This creates a foundation for larger-scale parallel or distributed analytics workflows.

## Tech Stack

- Java
- Maven
- Apache Commons Compress
- CSV parsing and validation
- File streaming and batching

## Repository Structure

```text
.
├── src/
│   └── main/java/edu/utexas/cs/cs378/
│       ├── Main.java
│       ├── MapToDataFile.java
│       └── Reducer.java
├── pom.xml
├── README.md
├── LICENSE
├── taxi-data-sorted-small.csv.bz2
├── miniSortedFile1.csv
├── miniSortedFile2.csv
├── miniSortedFile3.csv
├── miniSortedFile4.csv
└── .gitignore
```

## Getting Started

### Prerequisites

- Java 8+
- Maven installed and configured on your system

### Compile the project

```bash
mvn clean compile
```

### Run the project

```bash
mvn clean compile exec:java -Dexec.args="taxi-data-sorted-small.csv.bz2 4000"
```

### Parameters

- first argument: input file path
- second argument: batch size

Example:

```bash
mvn clean compile exec:java -Dexec.args="taxi-data-sorted-small.csv.bz2 500"
```

## Notes

This project demonstrates memory-aware data processing techniques that are useful in multithreaded and distributed systems contexts, especially when working with large-scale datasets that cannot fit comfortably into memory.

## License

This project is available under the repository license included in the project files.


