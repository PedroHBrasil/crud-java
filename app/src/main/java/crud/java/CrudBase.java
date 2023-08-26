package crud.java;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

    protected static void displayQueryResults(ResultSet result, Set<String> selectedCols) throws SQLException {
        ArrayList<HashMap<String, String>> resultsStr = resultSetToArrayListOfHashMap(result, selectedCols);
        HashMap<String, Integer> nCharsPerCol = getNumCharsPerCol(resultsStr, selectedCols);
        displayResultsTable(resultsStr, selectedCols, nCharsPerCol);
    }

    private static ArrayList<HashMap<String, String>> resultSetToArrayListOfHashMap(ResultSet result, Set<String> selectedCols) throws SQLException {
        ArrayList<HashMap<String, String>> resultsStr = new ArrayList<HashMap<String, String>>();
        while (result.next()) {
            System.out.println("There's a next result");
            HashMap<String, String> curResults = new HashMap<String, String>();
            for (String colName : selectedCols) {
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
}
