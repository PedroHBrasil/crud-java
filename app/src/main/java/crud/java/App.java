/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package crud.java;

import java.util.Scanner;
import java.util.List;


public class App {

    private void run() {
        System.out.println("Welcome to my CRUD-Java CLI tool!");

        DbConnector dbCon = new DbConnector();
        String catalog = dbCon.getConProps().getProperty("db.name");
        DbMetadata dbMetadata = new DbMetadata(dbCon.getCon(), catalog);
        Scanner sc = new Scanner(System.in);

        int input = 0;
        do {
            this.displayMainMenu(dbMetadata.getTables());
            System.out.print("Enter your choice: ");
            input = sc.nextInt();
            if (input > 0) {
                String tableName = dbMetadata.getTables().get(input-1);
                System.out.println("Accessing table " + tableName + "'s CRUD menu.");
                CrudCli.runCrudMenu(sc, dbMetadata, tableName);
            }
        } while (input != 0);

        System.out.println("Exiting.");
        sc.close();
        dbCon.closeDbConnection();
    }

    protected void displayMainMenu(List<String> tables) {
        System.out.println("\nChoose one of the following tables by number to perform operations on or enter '0' to quit.");
        System.out.println("\t0: QUIT");
        for (int i = 0; i < tables.size(); i++) {
            System.out.println("\t" + (i+1) + ": " + tables.get(i));
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }
}
