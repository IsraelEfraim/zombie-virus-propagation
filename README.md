# zombie-virus-propagation
Multi-agent System (MAS) to solve a fictional zombie virus propagation in Java using JADE framework.

## Dependencies

These are necessary dependencies in order to run: JADE, JavaFX, JFoenix. All of them are under ./lib folder, just make sure to add them to the project's libraries in your IDE.

## VM Options

You must add these VM options in order to run:

`
--module-path
JAVAFX_LIB_PATH
--add-modules
javafx.controls,javafx.fxml,javafx.media
--add-exports
javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED
`

Where JAVAFX_LIB_PATH represents a valid path to a JavaFX library in your system. You may use the path to ./lib/javafx-sdk-11.0.2/lib/. For instance, in my system, I use: "D:\usr\sync\...\lib\javafx-sdk-11.0.2\lib"
