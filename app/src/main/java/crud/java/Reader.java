package crud.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Reader extends CrudBase {

    private static final String readTemplate = "SELECT %s FROM %s WHERE (%s)";

    protected static void read(Connection con, String tableName, HashMap<String, String> selectedCols, ArrayList<String> filterStrList) {

        String colsStr = String.join(", ", selectedCols.keySet());
        String insertString = String.format(readTemplate, colsStr, tableName, filterStrList.get(0));

        try {
            PreparedStatement prepStatement = con.prepareStatement(insertString);
            for (int i = 1; i < filterStrList.size(); i++) {
                String value = filterStrList.get(i);
                prepStatement.setString(i, value);
            }
            System.out.println("Running query: " + prepStatement.toString());
            ResultSet result = prepStatement.executeQuery();
            // result.beforeFirst();

            // Converts resultset to a hashmap of array lists
            ArrayList<HashMap<String, String>> resultsStr = new ArrayList<HashMap<String, String>>();
            while (result.next()) {
                System.out.println("There's a next result");
                HashMap<String, String> curResults = new HashMap<String, String>();
                for (String colName : selectedCols.keySet()) {
                    String curResult = result.getString(colName);
                    curResults.put(colName, curResult);
                }
                if (curResults.isEmpty()) {
                    System.out.println("resultsStr was not filled");
                }
                resultsStr.add(curResults);
            }
            if (resultsStr.isEmpty()) {
                System.out.println("resultsStr was not filled");
            }

            // Gets number of characters per column
            HashMap<String, Integer> nCharsPerCol = new HashMap<String, Integer>();
            for (String colName : selectedCols.keySet()) {
                nCharsPerCol.put(colName, colName.length());
            }
            for (HashMap<String, String> resultLine : resultsStr) {
                for (String colName : selectedCols.keySet()) {
                    int curLength = resultLine.get(colName).length();
                    if (curLength > nCharsPerCol.get(colName)) {
                        nCharsPerCol.put(colName, curLength);
                    }
                }
            }

            // Prints heading of results table
            System.out.print("|");
            for (String colName : selectedCols.keySet()) {
                String fmtColName = String.format("%-" + nCharsPerCol.get(colName) + "s", colName);
                System.out.print(" " + fmtColName + " |");
            }
            System.out.print("\n");
            // Prints heading underline line
            System.out.print("|");
            for (String colName : selectedCols.keySet()) {
                String dashLines = "-".repeat(nCharsPerCol.get(colName)+2);
                System.out.print(dashLines + "|");
            }
            System.out.print("\n");
            // Prints results lines
            for (int i = 0; i < resultsStr.size(); i++) {
                System.out.print("|");
                for (String colName : selectedCols.keySet()) {
                    String curResult = resultsStr.get(i).get(colName);
                    String fmtCurResult = String.format("%-" + nCharsPerCol.get(colName) + "s", curResult);
                    System.out.print(" " + fmtCurResult + " |");
                }
                System.out.print("\n");
            }

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    protected static boolean readSelectCols(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> selectedCols) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        String message = "For table '"+ tableName + "', choose a column to include on the read operation:";

        int input = 0;
        do {
            displayColsMenu(message, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, selectedCols);
            input = readChoice(sc);
            if (input > 0) {
                String colName = tableColsNames.get(input-1);
                if (selectedCols.containsKey(colName)) {
                    selectedCols.remove(colName);
                } else {
                    selectedCols.put(colName, "selected");
                }
            }
        } while (input > 0);

        return (input == 0);
    }

    protected static boolean readSelectFilters(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> colsFilters) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        String message = "For table '"+ tableName + "', choose a column to filter. Format: {operator} {value}. Example: >= 0.";

        int input = 0;
        do {
            displayColsMenu(message, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, colsFilters);
            input = readChoice(sc);
            if (input > 0) {
                String colName = tableColsNames.get(input-1);
                readFilter(sc, colName, colsFilters);
            }
        } while (input > 0);

        return (input == 0);
    }

    protected static void readFilter(Scanner sc, String colName, HashMap<String, String> colsFilters) {
        sc.nextLine();
        System.out.print("Enter the filter for " + colName + ": ");
        String valueInput = sc.nextLine();
        colsFilters.put(colName, valueInput);
    }

    protected static Boolean readFilterString(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> colsFilters, ArrayList<String> filterStrList) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        String message = "For table '" + tableName + "' enter the arrangement of the filter statements by joining them with with AND, OR and/or (). Example: (1 AND 2 OR ((3 OR 1) AND (2 OR 4)))";

        boolean confirm = false;
        String filterStr = "";
        do {
            displayColsMenu(message, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, colsFilters);
            System.out.print("If you would like to cancel the read operation, enter '-1': ");
            if (sc.nextInt() == -1) {
                return false;
            }
            System.out.println("Current arrangement: " + filterStr);
            System.out.print("Enter the arrangement you want: ");
            sc.nextLine();
            filterStr = sc.nextLine();
            System.out.print("Confirm (y|n)? ");
            confirm = sc.next().equals("y");
        } while (!confirm);

        makeFilterStringList(colsFilters, filterStr, tableColsNames, filterStrList);

        return true;
    }

    protected static ArrayList<String> makeFilterStringList(HashMap<String, String> colsFilters, String filterStr, List<String> tableColsNames, ArrayList<String> filterStrList) {
        // Generates main filter string
        for (int i = 1; i <= tableColsNames.size(); i++) {
            String colName = tableColsNames.get(i-1);
            String filter = colsFilters.get(colName);
            if (filter == null) {
                continue;
            }
            String operator = filter.trim().split(" ")[0];
            String placeHolder = colName + " " + operator + " ? ";
            filterStr = filterStr.replace(String.valueOf(i), placeHolder);
        }
        // Generates array of main string and filter values
        filterStrList.add(filterStr);
        for (int i = 1; i <= tableColsNames.size(); i++) {
            String colName = tableColsNames.get(i-1);
            String filter = colsFilters.get(colName);
            if (filter == null) {
                continue;
            }
            String value = filter.trim().split(" ")[1];
            filterStrList.add(value);
        }

        return filterStrList;
    }

}
