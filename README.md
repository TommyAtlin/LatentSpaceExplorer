# LatentSpace Explorer

LatentSpace Explorer is an object-oriented JavaFX application for exploring word embeddings, performing semantic analysis, and visualizing high-dimensional vector data in 2D and 3D.

The system combines a Python embedding engine with a Java-based interface and logic layer. Python is used to load or generate word vectors and PCA-reduced vectors, while Java is responsible for loading the data, managing the object model, visualizing the words, and performing semantic operations.

---

## System Architecture

The project follows a layered architecture in order to separate responsibilities and make the system easier to maintain and extend.

### 1. Data Model

- **Word**  
  Represents a word in the system. Each word contains the text itself, its full vector, and its PCA vector.

- **Vector**  
  Encapsulates a numerical vector and provides mathematical operations such as addition, subtraction, division, and value access.

- **Point2D**  
  A lightweight object that represents a 2D point used for chart visualization.

### 2. Storage and Loading

- **WordStorage**  
  Stores all words in the system. It uses both a list and a map:
  - The list is used for iterating over all words.
  - The map is used for fast lookup by word text.

- **JsonLoader**  
  Loads full vectors and PCA vectors from JSON files and creates the object model.

- **PythonRunner**  
  Runs the Python embedding script when the vector files do not already exist.

### 3. Logic Layer

- **WordService**  
  Acts as the main service layer for semantic operations. It provides methods for finding words, nearest neighbors, distances, analogies, and nearest words to a custom vector.

- **KNearestWords**  
  Finds the K nearest words to a word or vector using a selected distance strategy.

- **VectorArithmeticService**  
  Performs analogy-style vector arithmetic in the form:

```text
third + second - first
```

- **Projections**  
  Handles PCA projection, custom semantic axis projection, and centroid calculation.

### 4. View Layer

- **MainApp**  
  The main JavaFX application. It builds the user interface, connects buttons to commands, and coordinates between the UI, services, and chart management.

- **ChartManager**  
  Manages the 2D scatter chart. It is responsible for refreshing the chart, drawing points, adding tooltips, highlighting selected words, centering the chart, and clearing the chart.

- **Three_D**  
  Provides a separate 3D visualization window using JavaFX 3D.

### 5. Command Layer

- **AppCommand**  
  Defines the command interface for user actions.

- **FunctionalCommand**  
  Wraps an action and an undo action using `Runnable`.

- **CommandInvoker**  
  Executes commands and manages basic Undo/Redo stacks.

---

## Object-Oriented Design

The project was designed according to object-oriented principles.

### Encapsulation

The internal data of vectors and words is protected inside dedicated classes.  
For example, `Vector` wraps the internal numerical array and exposes controlled methods for accessing and manipulating vector data.

This prevents the rest of the system from directly modifying the internal representation of a vector.

### Abstraction

The system uses interfaces to represent general behavior.

For example:

- `DistanceStrategy` abstracts the idea of a distance metric.
- `AppCommand` abstracts the idea of a user action.

This allows the system to work with general concepts instead of depending directly on specific implementations.

### Polymorphism

Different distance metrics, such as `Euclidean` and `Cosine`, implement the same `DistanceStrategy` interface.

This allows `KNearestWords` to work with different distance calculations without changing its own code.

The command system also uses polymorphism: `CommandInvoker` works with the `AppCommand` interface and can execute any command that implements it.

### Separation of Concerns

The system separates responsibilities between different classes:

- `MainApp` handles the application flow and UI setup.
- `ChartManager` handles chart visualization.
- `WordService` handles semantic logic.
- `Projections` handles projection and centroid calculations.
- `CommandInvoker` handles command execution and Undo/Redo.
- `JsonLoader` handles file loading.
- `PythonRunner` handles running the Python script.

This keeps the code easier to understand and easier to extend.

### Extensibility

The project is designed to be extended. For example:

- A new distance metric can be added by implementing `DistanceStrategy`.
- A new user action can be added by creating a new command.
- A new view can be added without changing the core `Word` and `Vector` model.
- A future terminal-based UI could reuse the same logic layer without rewriting the semantic calculations.

---

## Design Patterns

### Strategy Pattern

The distance calculation is implemented using the Strategy Pattern.

`DistanceStrategy` defines the common interface for distance metrics.  
`Euclidean` and `Cosine` are concrete strategies.

This allows `KNearestWords` to work with any distance metric without depending on a specific implementation.

For example, adding a new metric such as Manhattan distance would only require creating a new class that implements `DistanceStrategy`.

### Command Pattern

User actions are implemented using the Command Pattern.

Each button action is wrapped inside a command object.  
The `CommandInvoker` executes commands and manages Undo/Redo stacks.

This makes it possible to support basic Undo and Redo behavior and allows future extension of user actions.

The current Undo/Redo implementation is basic: for most visual operations, Undo restores the clean PCA chart view, and Redo executes the command again.

### Facade-like Service

`WordService` acts as a facade-like service for the UI.

Instead of having the UI directly manage storage, nearest-neighbor search, distance calculation, and vector arithmetic, it calls clear service methods through `WordService`.

This keeps the UI layer cleaner and separates it from the internal semantic logic.

---

## Full Vectors vs PCA Vectors

The project uses two vector representations for each word:

- **fullVector**  
  Used for real semantic calculations such as distance, K nearest neighbors, vector arithmetic, and centroid.

- **pcaVector**  
  Used mainly for visualization in 2D and 3D.

This separation is important because PCA is a reduced representation of the original vector.  
It is useful for visualization, but the full vector contains more semantic information.

---

## Main Features

- Load word embeddings generated by a Python script
- Display words as points in a 2D PCA-based scatter chart
- Select PCA components for the X and Y axes
- Search for a word and center the chart around it
- Calculate semantic distance between two words
- Support multiple distance metrics:
  - Euclidean Distance
  - Cosine Distance
- Click on a word point to display and highlight its K nearest neighbors
- Perform custom semantic projection between two selected words
- Perform vector arithmetic in the form:

```text
third + second - first
```

- Find the closest words to the result of a vector arithmetic operation
- Calculate a centroid vector for a group of selected words
- Find the K nearest words to the centroid
- Open a 3D visualization window using JavaFX 3D
- Use Command Pattern for user actions
- Support basic Undo and Redo for visual operations

---

## Custom Projection

The custom projection feature allows the user to choose two words that define a semantic axis.

For each word in the system, the project calculates its projection onto the axis created by the two selected words.

In this view:

- The X axis represents the projection value.
- The Y axis is only used for visual separation, so points do not overlap on a single line.

The Y value in this view does not represent a semantic meaning. It is only used to make the visualization easier to read.

---

## Undo and Redo

The project includes basic Undo and Redo support using the Command Pattern.

Each command contains:

- an `execute` action
- an `undo` action

The `CommandInvoker` manages two stacks:

- `undoStack`
- `redoStack`

For most visual operations, Undo restores the clean PCA chart view.  
Redo executes the command again.

A possible future improvement would be to implement full state-based Undo/Redo using a dedicated `ChartState` class.

---

## How to Run

### Prerequisites

1. Java with JavaFX configured.
2. Python installed and available from the command line.
3. The embedding script should be located at:

```text
src/embedder.py
```

### Execution

Run:

```text
MainApp.java
```

On the first run, if the following files do not exist:

```text
full_vectors.json
pca_vectors.json
```

the Java program will automatically run the Python script to generate them.

After the files are created, Java loads them and starts the visualization.

---

## Example Usage

### Search

Enter a word in the search field and click `Search`.

The chart centers around the selected word.

### Distance

Enter two words in `Word 1` and `Word 2`, then click `Distance`.

The system displays both Euclidean distance and Cosine distance.

### K Nearest Neighbors

Click on a point in the chart.

The selected word is highlighted, and its K nearest neighbors are highlighted as well.

### Vector Arithmetic

Enter three words and click `Vector Arithmetic`.

The system calculates:

```text
third + second - first
```

Then it finds the closest words to the resulting vector.

### Centroid Nearest

Enter several words separated by commas, for example:

```text
king,queen,man
```

Then click `Centroid Nearest`.

The system calculates the centroid vector and finds the nearest words to it.

### Custom Projection

Enter two words in `Word 1` and `Word 2`, then click `Custom Projection`.

The system projects all words onto the semantic axis between those two words.

### 3D View

Select X, Y, and Z PCA components, then click `Open 3D View`.

The system opens a separate JavaFX 3D window that displays the words in a 3D PCA space.

---

## Future Improvements

Possible future improvements include:

- Full state-based Undo/Redo using a dedicated `ChartState` class
- A terminal-based UI that reuses the same logic layer
- Additional distance or similarity metrics
- Better JSON parsing using a dedicated JSON library
- More advanced 3D controls
- Exporting analysis results to a file

---
