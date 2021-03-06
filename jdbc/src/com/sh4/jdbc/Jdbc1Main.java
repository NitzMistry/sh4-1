package com.sh4.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Jdbc1Main {
	public static String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static String DB_URL = "jdbc:mysql://localhost:3306/sh4";
	public static String DB_USER = "nilesh";
	public static String DB_PASS = "nilesh";
	public static void main(String[] args) throws Exception {
		Class.forName(DB_DRIVER);
		Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		Statement stmt = con.createStatement();
		String sql = "SELECT * FROM contacts";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			int id = rs.getInt("ID");
			String name =  rs.getString("NAME");
			String email =  rs.getString("EMAIL");
			String mobile =  rs.getString("MOBILE");
			System.out.println(id + ", " + name + ", " + email + ", " + mobile);
		}
		rs.close();
		stmt.close();
		con.close();
	}
}
