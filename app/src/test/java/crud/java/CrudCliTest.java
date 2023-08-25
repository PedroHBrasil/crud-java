/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package crud.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class CrudCliTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;

    @AfterEach
    public void restoreStreams() {
        System.setIn(this.originalIn);
        System.setOut(this.originalOut);
    }

    @Test void backToMainMenu() {
        String inputStr = "1\n0\n0\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setIn(inputStream);
        System.setOut(new PrintStream(outputStream));

        App.main(new String[0]);

        assert(outputStream.toString().trim().contains("Going back to main menu."));
    }

    @Test void selectCreate() {
        String inputStr = "1\n1\n-1\n0\n0\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputStr.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setIn(inputStream);
        System.setOut(new PrintStream(outputStream));

        App.main(new String[0]);

        assert(outputStream.toString().trim().contains("Selected Create."));
    }
}
