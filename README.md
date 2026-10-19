# Lucky Nine

A JavaFX card-game project.

## Requirements

- Windows x64
- JDK 25 or newer
- Visual Studio Code with the **Extension Pack for Java** installed

The included `javafx-sdk-27` folder is the JavaFX 27 Windows SDK. JavaFX 27 requires JDK 25 or newer.

## Run in Visual Studio Code

1. Extract the ZIP file.
2. In VS Code, use **File > Open Folder** and select the extracted `Lucky-Nine-main` folder. Do not open only `Main.java` or just the `src` folder.
3. Open **Terminal > New Terminal** and check your Java versions:

   ```powershell
   java -version
   javac -version
   ```

   Both commands should report version 25 or newer. If either command is missing or reports an older version, install/select a compatible JDK and restart VS Code.

4. Open **Run and Debug** and choose **Lucky Nine**, then press `F5`.

The launch configuration uses a path relative to this project, so it does not depend on another person's Windows username or Desktop/Downloads folder.

## Run from PowerShell (optional)

Run these commands from the `Lucky-Nine-main` project folder:

```powershell
javac --module-path ".\javafx-sdk-27\lib" --add-modules javafx.controls -d bin src\Main.java
java --module-path ".\javafx-sdk-27\lib" --add-modules javafx.controls -cp bin Main
```

## Troubleshooting

- **`java` or `javac` is not recognized:** install a JDK and make sure its `bin` directory is on your Windows `PATH`; then reopen VS Code.
- **`JavaFX runtime components are missing`:** run using the `Lucky Nine` launch configuration, which supplies the JavaFX module path.
- **The `javafx.base` module cannot be read / class version error:** make sure JDK 25 or newer is selected. Older precompiled class files have been removed from this project so VS Code can compile `src/Main.java` again.
