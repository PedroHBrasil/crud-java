package crud.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Reader extends CrudBase {

    private static final String readTemplate = "SELECT %s FROM %s WHERE (%s)";

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
            Reader.displayQueryResults(result, selectedCols);

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
        filterStrList.add("TO BE REPlACED");
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
