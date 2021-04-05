package com.azortis.azorbot;

import com.azortis.azorbot.cocoUtil.CocoFiles;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Test {
    private static final CocoFiles file = new CocoFiles("config/example.txt");
    private static final CocoFiles file2 = new CocoFiles("config/example2.txt");

    public static void main(String[] args){
        List<String> page = file.read();
        System.out.println(page.toString());
        List<String> query = file2.read();
        System.out.println(query.toString());


    }

    public static void viewTable(Connection con) throws SQLException {
        String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
                float price = rs.getFloat("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");
                System.out.println(coffeeName + ", " + supplierID + ", " + price +
                        ", " + sales + ", " + total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
