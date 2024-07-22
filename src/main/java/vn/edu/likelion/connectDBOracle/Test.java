package vn.edu.likelion.connectDBOracle;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        // Thông tin kết nối
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "system";
        String password = "1234";

        String query = "select * from help";

        Connection conn = null;
        PreparedStatement stat = null;
        ResultSet resultSet = null;
        try {
            // Mở kết nối
            conn = DriverManager.getConnection(url, user, password);
            // Gửi câu lệnh truy vấn tới database
            stat = conn.prepareStatement(query);
            // Thực hiện truy vấn
            resultSet = stat.executeQuery();

            // Xử lý kết quả truy vấn
            while (resultSet.next()) {
                System.out.println("Topic: " +  resultSet.getString(1));
                System.out.println("Seq: "   +  resultSet.getString(2));
                System.out.println("Info: "  +  resultSet.getString(3));
            }

        } catch (SQLException sqlException){
            sqlException.printStackTrace();
        } finally {
            try {
                // Kiểm tra khác null thì mới đóng
                if (resultSet != null) conn.close();
                if (stat != null) stat.close();
                if (resultSet != null) resultSet.close();
            } catch (SQLException s) {
                s.printStackTrace();
            }
        }
    }
}
