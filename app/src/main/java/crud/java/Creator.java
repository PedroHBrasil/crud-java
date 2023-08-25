package crud.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Creator {

    private static final String createTemplate = "INSERT INTO %s (%s) VALUES (%s)";

    protected static void create(Connection con, HashMap<String, String> values, String tableName) {

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
            int rowsAffected = prepStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted successfully.");

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static HashMap<String, String> getInsertValues(Scanner sc, DbMetadata dbMetadata, String tableName) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        List<String> tableColsAutos = dbMetadata.getTableColsMetadata(tableName, "IS_AUTOINCREMENT");

        HashMap<String, String> values = new HashMap<String, String>();
        int createInput = 0;
        do {
            List<String> effectiveColsNames = displayInsertMenu(tableName, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, tableColsAutos, values);
            System.out.print("Enter your choice: ");
            createInput = sc.nextInt();
            if (createInput > 0) {
                String colName = effectiveColsNames.get(createInput-1);
                Creator.readInsertValue(sc, colName, values);
            }
        } while (createInput > 0);

        return values;
    }

    private static final String colDisplayTemplate = "\t%d: %s - %s[%s] %s: %s";

    protected static List<String> displayInsertMenu(String tableName, List<String> tableColsNames, List<String> tableColsTypes, List<String> tableColsSizes, List<String> tableColsNulls, List<String> tableColsAutos, HashMap<String, String> values) {
        List<String> colsNamesEffective = new ArrayList<String>();
        System.out.println("For table '"+ tableName + "', choose a column to enter a value for:");
        System.out.println("\t-1: CANCEL");
        System.out.println("\t0: OK");
        for (int i = 0; i < tableColsNames.size(); i++) {
            boolean isColAuto = tableColsAutos.get(i).equals("YES");
            if (!isColAuto) {
                String colName = tableColsNames.get(i);
                String colType = tableColsTypes.get(i);
                String colSize = tableColsSizes.get(i);
                String colNull = tableColsNulls.get(i).equals("1") ? "optional" : "required";
                String colValue = values.containsKey(colName) ? values.get(colName) : "";
                String colDisplay = String.format(colDisplayTemplate, (colsNamesEffective.size()+1), colName, colType, colSize, colNull, colValue);
                System.out.println(colDisplay);
                colsNamesEffective.add(colName);
            }
        }

        return colsNamesEffective;
    }

    protected static void readInsertValue(Scanner sc, String colName, HashMap<String, String> values) {
        sc.nextLine();
        System.out.print("Enter the value for " + colName + ": ");
        String valueInput = sc.nextLine();
        values.put(colName, valueInput);
    }
}
