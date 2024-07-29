package vn.edu.likelion.connectDBOracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private final String DB_URL;
    private final String USER;
    private final String PASS;
    private Connection conn = null;

    public Connect() {
        DB_URL = "jdbc:oracle:thin:@localhost:1521:xe";
        USER = "root1";
        PASS = "1234";
    }

    public Connection openConnect() {
        try {
            // Đăng ký driver JDBC
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Mở kết nối
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
//            System.out.println("=> Kết nối CSDL thành công!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Không tìm thấy driver JDBC!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Kết nối CSDL thất bại!");
        }
        return conn;
    }

    public void closeConnect() throws SQLException {
        if (conn != null) conn.close();
    }

    public Connection getConnect() {
        return conn;
    }
}

