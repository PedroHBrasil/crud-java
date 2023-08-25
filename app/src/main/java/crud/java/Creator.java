package crud.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Creator extends CrudBase {

    private static final String createTemplate = "INSERT INTO %s (%s) VALUES (%s)";

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
            int rowsAffected = prepStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted successfully.");

            prepStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected static boolean readInsertParams(Scanner sc, DbMetadata dbMetadata, String tableName, HashMap<String, String> values) {
        List<String> tableColsNames = dbMetadata.getTableColsMetadata(tableName, "COLUMN_NAME");
        List<String> tableColsTypes = dbMetadata.getTableColsMetadata(tableName, "TYPE_NAME");
        List<String> tableColsSizes = dbMetadata.getTableColsMetadata(tableName, "COLUMN_SIZE");
        List<String> tableColsNulls = dbMetadata.getTableColsMetadata(tableName, "NULLABLE");
        String message = "For table '"+ tableName + "', choose a column to input the value for:";

        int input = 0;
        do {
            displayColsMenu(message, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, values);
            input =  readChoice(sc);
            if (input > 0) {
                String colName = tableColsNames.get(input-1);
                Creator.readInsertValue(sc, colName, values);
            }
        } while (input > 0);

        return (input == 0);
    }

    protected static void readInsertValue(Scanner sc, String colName, HashMap<String, String> values) {
        sc.nextLine();
        System.out.print("Enter the value for " + colName + ": ");
        String valueInput = sc.nextLine();
        values.put(colName, valueInput);
    }
}
