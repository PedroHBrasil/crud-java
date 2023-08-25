/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package crud.java;

import java.util.HashMap;
import java.util.Scanner;


public class CrudCli {

    // Crud Menu

    protected static void runCrudMenu(Scanner sc, DbMetadata dbMetadata, String tableName) {
        int crudInput = 0;
        do {
            CrudCli.displayCrudMenu(tableName);
            System.out.print("Enter your choice: ");
            crudInput = sc.nextInt();
            switch (crudInput) {
                case 1:
                    System.out.println("Selected Create.");
                    HashMap<String, String> values = new HashMap<String, String>();
                    boolean runCreate = Creator.getInsertValues(sc, dbMetadata, tableName, values);
                    if (runCreate) {
                        Creator.create(dbMetadata.getCon(), values, tableName);
                    } else {
                        System.out.println("Create operation canceled.");
                    }
                    break;
                case 2:
                    
                    break;
                case 3:
                    
                    break;
                case 4:
                    
                    break;
                default:
                    break;
            }
        } while (crudInput != 0);

        System.out.println("Going back to main menu.");

    }

    protected static void displayCrudMenu(String tableName) {
        System.out.println(String.format("\nWhich operation would you like to perform on table '%s'?", tableName));
        System.out.println("\t0: BACK TO MAIN MENU");
        System.out.println("\t1: Create Item");
        System.out.println("\t2: Read Item");
        System.out.println("\t3: Update Item");
        System.out.println("\t4: Delete Item");
    }

    // Create Menu

}
