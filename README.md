# FileConv — CLI File Converter (JSON / XML / CSV)

A command-line utility that converts files between three formats: **JSON**, **XML**, and **CSV** — in any direction.

## Requirements

- Java 21+
- SBT 1.10+

## Build & Run

```bash
# Build the executable JAR
sbt assembly

# Run the converter
java -jar target/fileconv.jar --input <input-file> --output <output-file>

# Short flags are also supported
java -jar target/fileconv.jar -i <input-file> -o <output-file>
```

Alternatively, you can run directly via SBT without building a JAR:

```bash
sbt "run --input <input-file> --output <output-file>"
```

## Usage Examples

### JSON to CSV
```bash
java -jar target/fileconv.jar --input data.json --output data.csv
```

Input (`data.json`):
```json
[
  {"name": "Alice", "age": 30, "city": "Vienna"},
  {"name": "Bob", "age": 25, "city": "Berlin"}
]
```

Output (`data.csv`):
```csv
"name","age","city"
"Alice","30","Vienna"
"Bob","25","Berlin"
```

### CSV to XML
```bash
java -jar target/fileconv.jar --input users.csv --output users.xml
```

### XML to JSON
```bash
java -jar target/fileconv.jar --input config.xml --output config.json
```

### JSON to XML
```bash
java -jar target/fileconv.jar --input data.json --output data.xml
```

## Supported Conversions

| From \ To | JSON | XML  | CSV  |
|-----------|------|------|------|
| **JSON**  | Yes  | Yes  | Yes  |
| **XML**   | Yes  | Yes  | Yes  |
| **CSV**   | Yes  | Yes  | Yes  |

## Nested Data Handling

- **JSON / XML**: Nested structures are supported natively.
- **CSV**: Nested fields are flattened using dot notation (`address.city`) and array indices (`items[0].name`). When reading CSV with such headers, the structure is automatically restored.

## Error Handling

The application provides clear error messages for:
- Missing or incorrect CLI arguments
- Non-existent input files
- Unsupported file formats
- Malformed file content (invalid JSON, XML, or CSV)
- Output directory issues

Example:
```
Error: Input file 'missing.json' does not exist.
Error: Unsupported format '.yaml'. Supported formats: json, xml, csv.
Error: Missing required flag: --input. Usage: fileconv --input <file> --output <file>
```

## Running Tests

```bash
sbt test
```

The project includes 83 tests covering:
- CLI argument parsing and validation
- Data model (ObjectNode, ArrayNode, ValueNode, NullNode)
- JSON parsing and writing
- XML parsing and writing
- CSV parsing and writing
- Flattening/unflattening utilities
- Conversion pipeline
- Integration tests for all 6 conversion directions

## Project Architecture

### Design Patterns (GoF)

1. **Strategy** — `DataParser` and `DataWriter` interfaces allow swapping parsing/writing implementations per format.
2. **Factory Method** — `ConverterFactory` selects the correct parser/writer based on file extension.
3. **Composite** — The internal data model (`DataNode` hierarchy) forms a tree structure for representing nested data.

### Package Structure

```
com.fileconv
├── App.java                          # Entry point
├── cli/
│   ├── CliArgs.java                  # Parsed CLI arguments (record)
│   └── CliParser.java                # CLI argument parsing & validation
├── model/
│   ├── DataNode.java                 # Sealed abstract base (Composite)
│   ├── ObjectNode.java               # Key-value map node
│   ├── ArrayNode.java                # Ordered list node
│   ├── ValueNode.java                # Scalar value leaf
│   └── NullNode.java                 # Null value singleton
├── converter/
│   ├── DataParser.java               # Strategy interface for parsing
│   ├── DataWriter.java               # Strategy interface for writing
│   ├── FormatType.java               # Enum: JSON, XML, CSV
│   ├── ConverterFactory.java         # Factory for parsers/writers
│   ├── json/
│   │   ├── JsonParser.java           # Jackson-based JSON parser
│   │   └── JsonWriter.java           # Jackson-based JSON writer
│   ├── xml/
│   │   ├── XmlParser.java            # Jackson XML parser
│   │   └── XmlWriter.java            # Jackson XML writer
│   └── csv/
│       ├── CsvParser.java            # OpenCSV-based CSV parser
│       ├── CsvWriter.java            # OpenCSV-based CSV writer
│       └── FlatteningUtils.java      # Dot-notation flatten/unflatten
├── pipeline/
│   └── ConversionPipeline.java       # Orchestrator
└── exception/
    ├── FileConvException.java        # Base exception
    ├── ParseException.java           # Parse errors
    ├── WriteException.java           # Write errors
    ├── UnsupportedFormatException.java
    └── InvalidArgumentException.java
```

## Libraries Used

| Library | Purpose |
|---------|---------|
| Jackson Databind | JSON parsing and writing |
| Jackson XML | XML parsing and writing |
| OpenCSV | CSV parsing and writing |
| JUnit 5 | Unit and integration testing |
| Mockito | Test mocking |
