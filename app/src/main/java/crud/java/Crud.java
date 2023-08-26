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
import java.util.Set;

class Crud {

    // SQL Templates

    private static final String createTemplate = "INSERT INTO %s (%s) VALUES (%s)";
    private static final String readTemplate = "SELECT %s FROM %s WHERE (%s)";
    private static final String updateTemplate = "UPDATE %s SET %s WHERE (%s)";
    private static final String deleteTemplate = "DELETE FROM %s WHERE (%s)";

    // Create

    protected static void create(Connection con, String tableName, HashMap<String, String> values) {

        String colsStr = String.join(", ", values.keySet());
        String valuesPlaceHolder = String.join(", ", Collections.nCopies(values.size(), "?"));
        String insertString = String.format(createTemplate, tableName, colsStr, valuesPlaceHolder);

        try {
            PreparedStatement prepStatement = con.prepareStatement(insertString);
            for (int i = 0; i < values.size(); i++) {
                String colName = colsStr.split(", ")[i];
                String value = values.get(colName);
                prepStatement.setString(i+1, value);
            }
            System.out.println("Running query: " + prepStatement.toString());
            int rowsAffected = prepStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted successfully.");

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Read

    protected static void read(Connection con, String tableName, Set<String> selectedCols, ArrayList<String> filterStrList) {

        String colsStr = String.join(", ", selectedCols);
        String insertString = String.format(readTemplate, colsStr, tableName, filterStrList.get(0));

        try {
            PreparedStatement prepStatement = con.prepareStatement(insertString);
            for (int i = 1; i < filterStrList.size(); i++) {
                String value = filterStrList.get(i);
                prepStatement.setString(i, value);
            }
            System.out.println("Running query: " + prepStatement.toString());
            ResultSet result = prepStatement.executeQuery();
            displayQueryResults(result, selectedCols);

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Update

    protected static void update(Connection con, String tableName, HashMap<String, String> values, ArrayList<String> filterStrList) {

        String[] colsNames = values.keySet().toArray(new String[0]);
        String colsStr = String.join(" = ?, ", colsNames) + " = ?";
        String insertString = String.format(updateTemplate, tableName, colsStr, filterStrList.get(0));

        try {
            PreparedStatement prepStatement = con.prepareStatement(insertString);
            // Values Update
            for (int i = 0; i < values.size(); i++) {
                String colName = colsNames[i];
                String value = values.get(colName);
                System.out.println(i);
                System.out.println(colName);
                System.out.println(value);
                prepStatement.setString(i+1, value);
            }
            // Filters
            for (int i = 1; i < filterStrList.size(); i++) {
                String value = filterStrList.get(i);
                prepStatement.setString(values.size() + i, value);
            }
            // Runs update
            System.out.println("Running query: " + prepStatement.toString());
            int rowsAffected = prepStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) updated successfully.");

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static void delete(Connection con, String tableName, ArrayList<String> filterStrList) {

        String insertString = String.format(deleteTemplate, tableName, filterStrList.get(0));

        try {
            PreparedStatement prepStatement = con.prepareStatement(insertString);
            for (int i = 1; i < filterStrList.size(); i++) {
                String value = filterStrList.get(i);
                prepStatement.setString(i, value);
            }
            System.out.println("Running query: " + prepStatement.toString());
            int rowsAffected = prepStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) deleted successfully.");

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Display

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

    protected static void displayQueryResults(ResultSet result, Set<String> selectedCols) throws SQLException {
        ArrayList<HashMap<String, String>> resultsStr = resultSetToArrayListOfHashMap(result, selectedCols);
        HashMap<String, Integer> nCharsPerCol = getNumCharsPerCol(resultsStr, selectedCols);
        displayResultsTable(resultsStr, selectedCols, nCharsPerCol);
    }

    private static ArrayList<HashMap<String, String>> resultSetToArrayListOfHashMap(ResultSet result, Set<String> selectedCols) throws SQLException {
        ArrayList<HashMap<String, String>> resultsStr = new ArrayList<HashMap<String, String>>();
        while (result.next()) {
            HashMap<String, String> curResults = new HashMap<String, String>();
            for (String colName : selectedCols) {
                String curResult = result.getString(colName);
                curResults.put(colName, curResult);
            }
            resultsStr.add(curResults);
        }

        return resultsStr;
    }

    private static HashMap<String, Integer> getNumCharsPerCol(ArrayList<HashMap<String, String>> resultsStr, Set<String> selectedCols) {
        HashMap<String, Integer> nCharsPerCol = new HashMap<String, Integer>();
        for (String colName : selectedCols) {
            nCharsPerCol.put(colName, colName.length());
        }
        for (HashMap<String, String> resultLine : resultsStr) {
            for (String colName : selectedCols) {
                int curLength = resultLine.get(colName).length();
                if (curLength > nCharsPerCol.get(colName)) {
                    nCharsPerCol.put(colName, curLength);
                }
            }
        }

        return nCharsPerCol;
    }

    private static void displayResultsTable(ArrayList<HashMap<String, String>> resultsStr, Set<String> selectedCols, HashMap<String, Integer> nCharsPerCol) {
        // Prints heading of results table
        System.out.print("|");
        for (String colName : selectedCols) {
            String fmtColName = String.format("%-" + nCharsPerCol.get(colName) + "s", colName);
            System.out.print(" " + fmtColName + " |");
        }
        System.out.print("\n");

        // Prints heading underline line
        System.out.print("|");
        for (String colName : selectedCols) {
            String dashLines = "-".repeat(nCharsPerCol.get(colName)+2);
            System.out.print(dashLines + "|");
        }
        System.out.print("\n");

        // Prints results lines
        for (int i = 0; i < resultsStr.size(); i++) {
            System.out.print("|");
            for (String colName : selectedCols) {
                String curResult = resultsStr.get(i).get(colName);
                String fmtCurResult = String.format("%-" + nCharsPerCol.get(colName) + "s", curResult);
                System.out.print(" " + fmtCurResult + " |");
            }
            System.out.print("\n");
        }
    }

    // CRUD CLI

    protected static boolean askForColsValues(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> values) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        String message = "For table '"+ tableName + "', choose the columns to assign values for:";

        int input = 0;
        do {
            displayColsMenu(message, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, values);
            input =  readChoice(sc);
            if (input > 0) {
                String colName = tableColsNames.get(input-1);
                askColValue(sc, colName, values);
            }
        } while (input > 0);

        return (input == 0);
    }

    protected static void askColValue(Scanner sc, String colName, HashMap<String, String> values) {
        sc.nextLine();
        System.out.print("Enter the new value for " + colName + ": ");
        String valueInput = sc.nextLine();
        values.put(colName, valueInput);
    }

    protected static boolean askSelectCols(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> selectedCols) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        String message = "For table '"+ tableName + "', choose the columns to include on the read operation:";

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

    protected static boolean askFilters(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> colsFilters) {
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
                askFilter(sc, colName, colsFilters);
            }
        } while (input > 0);

        return (input == 0);
    }

    protected static void askFilter(Scanner sc, String colName, HashMap<String, String> colsFilters) {
        sc.nextLine();
        System.out.print("Enter the filter for " + colName + ": ");
        String valueInput = sc.nextLine();
        colsFilters.put(colName, valueInput);
    }

    protected static Boolean askFilterArrangement(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> colsFilters, ArrayList<String> filterStrList) {
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
        filterStrList.add("TO BE REPLACED");
        String[] splittedFilterStr = filterStr.split(" ");
        for (int i = 0; i < splittedFilterStr.length; i++) {
            String element = splittedFilterStr[i];
            if (element.matches("^[1-9][0-9]*$")) { // is positive integer
                int iCol = Integer.parseInt(element) - 1;
                String colName = tableColsNames.get(iCol);

                String filter = colsFilters.get(colName);
                String value = filter.trim().split(" ")[1];
                filterStrList.add(value);

                String operator = filter.trim().split(" ")[0];
                String placeHolder = colName + " " + operator + " ? ";
                splittedFilterStr[i] = placeHolder;
            }
        }
        filterStrList.set(0, String.join(" ", splittedFilterStr));

        return filterStrList;
    }

}
