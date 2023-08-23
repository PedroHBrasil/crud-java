/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package crud.java;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;


public class App {

    private DbConnector dbCon;
    private List<String> dbTables;

    // DB Metadata

    protected List<String> getDbTables(Connection connection) {
        this.dbTables = new ArrayList<String>();
        try {
            // Aborts if connection is closed
            if (connection.isClosed()) {
                return null;
            }
            // Gets an array of strings, where each string is the name of a database table
            DatabaseMetaData dbMeta = connection.getMetaData();

            String catalog = this.dbCon.getConProps().getProperty("db.name");
            String schemaPattern = null;
            String tableNamePattern = null;
            String[] types = { "TABLE" };

            ResultSet dbTablesSet = dbMeta.getTables(catalog, schemaPattern, tableNamePattern, types);

            this.dbTables = this.resultSetToList(dbTablesSet, "TABLE_NAME");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return this.dbTables;
    }

    protected List<String> getTableColMetadata(int iTable, String metadataType) {
        List<String> tableColsNames = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(iTable);
            tableColsNames = this.resultSetToList(tableColsSet, metadataType);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsNames;
    }

    protected List<String> getTableColNames(int iTable) {
        List<String> tableColsNames = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(iTable);
            tableColsNames = this.resultSetToList(tableColsSet, "COLUMN_NAME");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsNames;
    }

    protected List<String> getTableColTypes(int iTable) {
        List<String> tableColsTypes = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(iTable);
            tableColsTypes = this.resultSetToList(tableColsSet, "TYPE_NAME");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsTypes;
    }

    protected List<String> getTableColSizes(int iTable) {
        List<String> tableColsTypes = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(iTable);
            tableColsTypes = this.resultSetToList(tableColsSet, "COLUMN_SIZE");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsTypes;
    }

    // DB Metadata - util

    protected ResultSet getTableColsSet(int iTable) throws SQLException{
        ResultSet tableColsSet = null;
        try {
            DatabaseMetaData dbMeta = this.dbCon.getCon().getMetaData();

            String catalog = this.dbCon.getConProps().getProperty("db.name");
            String schemaPattern = null;
            String tableNamePattern = this.dbTables.get(iTable);
            String columnNamePattern = null;

            tableColsSet = dbMeta.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsSet;
    }

    protected List<String> resultSetToList(ResultSet resultSet, String metadataType) throws SQLException {
        ArrayList<String> resultList = new ArrayList<String>();
        resultSet.beforeFirst();
        while (resultSet.next()) {
            resultList.add(resultSet.getString(metadataType));
        }

        return resultList;
    }

    // CLI - Main Menu

    protected void runMainMenu(Scanner cliSc) {
        int input = 0;
        do {
            this.displayMainMenu();
            System.out.print("Enter your choice: ");
            input = cliSc.nextInt();
            if (input != 0) {
                this.runCrudMenu(cliSc, input-1);
            }
        } while (input != 0);
    }

    protected void displayMainMenu() {
        System.out.println("\nChoose one of the following tables by number to perform operations on or enter '0' to quit.");
        System.out.println("\t0: QUIT");
        for (int i = 0; i < this.dbTables.size(); i++) {
            System.out.println("\t" + (i+1) + ": " + this.dbTables.get(i));
        }
    }

    // CLI - Crud Menu

    protected void runCrudMenu(Scanner cliSc, int iTable) {
        int crudInput = 0;
        do {
            displayCrudMenu(this.dbTables.get(iTable));
            System.out.print("Enter your choice: ");
            crudInput = cliSc.nextInt();
            switch (crudInput) {
                case 1:
                    this.create(cliSc, iTable);
                    break;
                case 2:
                    this.read(iTable);
                    break;
                case 3:
                    this.update(iTable);
                    break;
                case 4:
                    this.delete(iTable);
                    break;
                default:
                    break;
            }
        } while (crudInput != 0);
    }

    protected void displayCrudMenu(String tableName) {
        System.out.println(String.format("\nWhich operation would you like to perform on table '%s'?", tableName));
        System.out.println("\t0: BACK TO MAIN MENU");
        System.out.println("\t1: Create Item");
        System.out.println("\t2: Read Item");
        System.out.println("\t3: Update Item");
        System.out.println("\t4: Delete Item");
    }

    // CRUD

    private final String createTemplate = "INSERT INTO %s (%s) VALUES (%s)";

    protected void create(Scanner cliSc, int iTable) {
        HashMap<String, String> values = this.getInsertValues(cliSc, iTable);
    }

    protected HashMap<String, String> getInsertValues(Scanner cliSc, int iTable) {
        String tableName = this.dbTables.get(iTable);
        List<String> tableColsNames = this.getTableColMetadata(iTable, "COLUMN_NAME");
        List<String> tableColsTypes = this.getTableColMetadata(iTable, "TYPE_NAME");
        List<String> tableColsSizes = this.getTableColMetadata(iTable, "COLUMN_SIZE");
        List<String> tableColsNulls = this.getTableColMetadata(iTable, "NULLABLE");
        List<String> tableColsAutos = this.getTableColMetadata(iTable, "IS_AUTOINCREMENT");

        HashMap<String, String> values = new HashMap<String, String>();

        // Gathers data to insert
        int createInput = 0;
        do {
            List<String> effectiveColsNames = displayInsertMenu(tableName, tableColsNames, tableColsTypes, tableColsSizes, tableColsNulls, tableColsAutos, values);
            System.out.print("Enter your choice: ");
            createInput = cliSc.nextInt();
            if (createInput > 0) {
                String colName = effectiveColsNames.get(createInput-1);
                this.readInsertValue(cliSc, colName, values);
            }
        } while (createInput > 0);



        return values;
    }

    private final String colDisplayTemplate = "\t%d: %s - %s[%s] %s: %s";

    protected List<String> displayInsertMenu(String tableName, List<String> tableColsNames, List<String> tableColsTypes, List<String> tableColsSizes, List<String> tableColsNulls, List<String> tableColsAutos, HashMap<String, String> values) {
        System.out.println("For table '"+ tableName + "', choose a column to enter a value for:");
        System.out.println("\t-1: CANCEL");
        System.out.println("\t0: OK");
        List<String> colsNamesEffective = new ArrayList<String>();
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

    protected void readInsertValue(Scanner cliSc, String colName, HashMap<String, String> values) {
        System.out.print("Enter the value for " + colName + ": ");
        String valueInput = cliSc.next();
        values.put(colName, valueInput);
    }

    protected void read(int iTable) {
        
    }

    protected void update(int iTable) {
        
    }

    protected void delete(int iTable) {
        
    }

    private void run() {
        this.dbCon = new DbConnector();
        this.getDbTables(this.dbCon.getCon());

        Scanner cliSc = new Scanner(System.in);
        this.runMainMenu(cliSc);
        cliSc.close();

        this.dbCon.closeDbConnection();
    }

    public static void main(String[] args) {
        System.out.println("Welcome to my CRUD-Java CLI tool!");
        App app = new App();
        app.run();
    }
}
