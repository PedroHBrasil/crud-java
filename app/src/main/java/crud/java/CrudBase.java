package crud.java;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

abstract class CrudBase {

    private static final String colDisplayTemplate = "\t%d: %s - %s[%s] %s: %s";

    protected static void displayColsMenu(String message, List<String> tableColsNames, List<String> tableColsTypes, List<String> tableColsSizes, List<String> tableColsNulls, HashMap<String, String> values) {
        System.out.println(message);
        System.out.println("\t-1: CANCEL");
        System.out.println("\t0: OK");
        for (int i = 0; i < tableColsNames.size(); i++) {
            String colName = tableColsNames.get(i);
            String colType = tableColsTypes.get(i);
            String colSize = tableColsSizes.get(i);
            String colNull = tableColsNulls.get(i).equals("1") ? "optional" : "required";
            String colValue = values.containsKey(colName) ? values.get(colName) : "";
            String colDisplay = String.format(colDisplayTemplate, (i+1), colName, colType, colSize, colNull, colValue);
            System.out.println(colDisplay);
        }
    }

    protected static int readChoice(Scanner sc) {
        System.out.print("Enter your choice: ");
        return sc.nextInt();
    }
}
