/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package crud.java;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DbMetadata {

    private List<String> dbTables;

    protected List<String> getTables() {
        return dbTables;
    }

    DbMetadata(Connection con, String catalog) {
        this.getDbTables(con, catalog);
    }

    protected List<String> getDbTables(Connection con, String catalog) {
        this.dbTables = new ArrayList<String>();
        try {
            // Aborts if connection is closed
            if (con.isClosed()) {
                return null;
            }
            // Gets an array of strings, where each string is the name of a database table
            DatabaseMetaData dbMeta = con.getMetaData();

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

    protected List<String> getTableColMetadata(Connection con, String catalog, String tableName, String metadataType) {
        List<String> tableColsNames = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(con, catalog, tableName);
            tableColsNames = this.resultSetToList(tableColsSet, metadataType);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsNames;
    }

    protected List<String> getTableColNames(Connection con, String catalog, String tableName) {
        List<String> tableColsNames = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(con, catalog, tableName);
            tableColsNames = this.resultSetToList(tableColsSet, "COLUMN_NAME");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsNames;
    }

    protected List<String> getTableColTypes(Connection con, String catalog, String tableName) {
        List<String> tableColsTypes = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(con, catalog, tableName);
            tableColsTypes = this.resultSetToList(tableColsSet, "TYPE_NAME");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsTypes;
    }

    protected List<String> getTableColSizes(Connection con, String catalog, String tableName) {
        List<String> tableColsTypes = new ArrayList<String>();
        try {
            ResultSet tableColsSet = this.getTableColsSet(con, catalog, tableName);
            tableColsTypes = this.resultSetToList(tableColsSet, "COLUMN_SIZE");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return tableColsTypes;
    }

    // util

    protected ResultSet getTableColsSet(Connection con, String catalog, String tableName) throws SQLException{
        ResultSet tableColsSet = null;
        try {
            DatabaseMetaData dbMeta = con.getMetaData();

            String schemaPattern = null;
            String columnNamePattern = null;

            tableColsSet = dbMeta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);
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
}
