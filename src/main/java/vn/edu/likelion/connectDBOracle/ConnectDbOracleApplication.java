package vn.edu.likelion.connectDBOracle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;


public class ConnectDbOracleApplication {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("======================");
		System.out.println("       Đăng nhập      ");
		System.out.println("======================");
		System.out.print("username: ");
		String username = scanner.nextLine();
		System.out.print("password: ");
		String password = scanner.nextLine();

		String password_hash = Base64.getEncoder().encodeToString(password.getBytes());	// MÃ HÓA DỮ LIỆU
//		System.out.println(password_hash);

//		byte[] decodedBytes = Base64.getDecoder().decode(test);
//		String decodedString = new String(decodedBytes);
//		System.out.println(decodedString);



		DatabaseConnection conn = new DatabaseConnection();
		PreparedStatement stat = null;
		ResultSet resultSet = null;


		// Đăng nhập: (findUserAuthentication) => nếu tìm được trả ra true để chạy vòng lặp bên dưới.
		boolean findUser = findUserAuthentication(conn, stat, resultSet, username, password_hash);

		if (!findUser){
			main(args);
		}

		// Xác định vai trò của người dùng
//		String roleName = getUserRole(conn, stat, resultSet, username);
//
//		if (roleName != null) {
//			System.out.println("Role: " + roleName);
//		} else {
//			System.out.println("Không xác định được vai trò của người dùng.");
//			return;
//		}

		while(findUser){
			System.out.println("""
					1 - Thêm user
					2 - Sửa user
					3 - Xóa user
					4 - Xem danh sách học viên
					5 - Xem danh sách học viên có mặt trong ngày
					6 - Xem danh sách học viên vắng mặt trong ngày
					7 - Xem danh sách học viên trong ngày (cả có mặt và vắng mặt)
					8 - Điểm danh( ghi lại học viên có mặt, vắng mặt)
					9 - Thêm danh sách học sinh vào CSDL
					0 - Exit
					""");
			System.out.print("Vui lòng chọn mục: ");
			int choice = scanner.nextInt();

			switch (choice){
				case 1:
					System.out.print("username: " + username + "\n");
					System.out.print("password: " + password + "\n");
					break;
				case 2:
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					findListStudentStatus(conn, stat, resultSet, 0);
					break;
				case 6:
					findListStudentStatus(conn, stat, resultSet, 1);
					break;
				case 7:
					findListStudentStatus(conn, stat, resultSet, 2);
					break;
				case 8:
					System.out.println(LocalDate.now());
					break;
				case 9:
					try {
						// Tạo ArrayList danh sách học sinh, // Lấy danh sách học sinh lưu vào arrListStudents
						final ArrayList<Student> students = getListStudentsFormTXTFile("StudentsList.txt");

						// Thêm dữ liệu bảng Student vào CSDL
						insertStudentDB(conn, stat, resultSet, students);

						// Thêm dữ liệu bảng Student vào CSDL
						insertAttendanceStudent(conn, stat, resultSet, students);

					} catch (IOException e) {
						e.printStackTrace();
					}

					break;
				case 0:
					System.exit(0);
					break;
			}
		}
	}


	private static List<String> getUserPermissions(DatabaseConnection conn, PreparedStatement stat, ResultSet resultSet, String username) {
		List<String> permissions = new ArrayList<>();
		try {
			String query = """
            SELECT p.permission_name
            FROM user u
            JOIN roles r ON u.role_id = r.role_id
            JOIN group g ON r.role_id = g.role_id
            JOIN permission p ON g.permission_id = p.permission_id
            WHERE u.username = ?
        """;
			stat = conn.openConnect().prepareStatement(query);
			stat.setString(1, username);
			resultSet = stat.executeQuery();

			while (resultSet.next()) {
				permissions.add(resultSet.getString("permission_name"));
			}
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (stat != null) stat.close();
				conn.closeConnect();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
		return permissions;
	}



	// =====================================================
	// 				CREATE ARRAYLIST STUDENT DATA
	// =====================================================
	private static ArrayList<Student> getListStudentsFormTXTFile(String txtFilePath) throws IOException {
		ArrayList<Student> arrListStudents = new ArrayList<>();				// Tạo một mảng tạm để lưu trữ dữ liệu từ fileA
		BufferedReader fileReader = null;									// Tạo đối tượng BufferedWriter
		fileReader = new BufferedReader(new FileReader(txtFilePath));		// Đọc file bằng BufferedWriter

		String line;
		String[] data;		// ouput: data = [17, Nguyễn Đức Tấn, 0]
		Student student;	// Khởi tạo đối tượng student

		while ((line = fileReader.readLine()) != null) {
			data  = line.split("	");
//			data[1] = Base64.getEncoder().encodeToString(data[1].getBytes());	// MÃ HÓA DỮ LIỆU
			student = new Student(data[0], data[1], data[2]);
			arrListStudents.add(student);
		}

		fileReader.close();

//		System.out.println(arrListStudents);
		return arrListStudents;
	}

	private static void insertStudentDB(DatabaseConnection conn, PreparedStatement stat, ResultSet resultSet, ArrayList<Student> students) {
		try {
			// Gửi câu lệnh truy vấn tới database
			String query = "INSERT INTO student (student_id, student_name) VALUES (?, ?)";
			stat = conn.openConnect().prepareStatement(query);

			for (Student student : students) {
				if (!isStudentExist(conn, student.getId())) {
					stat.setString(1, student.getId());
					stat.setString(2, student.getName());
					stat.addBatch(); // Thêm lệnh này vào nhóm lệnh
				} else {
					System.out.println("Student with ID " + student.getId() + " already exists. Skipping...");
				}
			}

			// Thực hiện batch update
			int[] results = stat.executeBatch(); // Thực hiện tất cả các lệnh đã được thêm vào nhóm lệnh

			// Xử lý kết quả truy vấn
			for (int result : results) {
				if (result >= 0) {
					System.out.println("Insert OK");
				} else if (result == PreparedStatement.SUCCESS_NO_INFO) {
					System.out.println("Insert executed, no information about rows affected.");
				} else if (result == PreparedStatement.EXECUTE_FAILED) {
					System.out.println("Insert failed.");
				}
				System.out.println("-------------------------");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (stat != null) stat.close();
				conn.closeConnect();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
	}
	private static void insertAttendanceStudent(DatabaseConnection conn, PreparedStatement stat, ResultSet resultSet, ArrayList<Student> students) {
		try {
			// Gửi câu lệnh truy vấn tới database
			String query = "INSERT INTO attendance (student_id, dates, status) VALUES (?, ?, ?)";
			stat = conn.openConnect().prepareStatement(query);

			for (Student student : students) {
					stat.setString(1, student.getId());
					stat.setDate(2, Date.valueOf(LocalDate.now()));
					stat.setInt(3, Integer.parseInt(student.getIsActive()));
					stat.addBatch(); // Thêm lệnh này vào nhóm lệnh
			}

			// Thực hiện batch update
			int[] results = stat.executeBatch(); // Thực hiện tất cả các lệnh đã được thêm vào nhóm lệnh

			// Xử lý kết quả truy vấn
			for (int result : results) {
				if (result >= 0) {
					System.out.println("Insert OK");
				} else if (result == PreparedStatement.SUCCESS_NO_INFO) {
					System.out.println("Insert executed, no information about rows affected.");
				} else if (result == PreparedStatement.EXECUTE_FAILED) {
					System.out.println("Insert failed.");
				}
				System.out.println("-------------------------");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (stat != null) stat.close();
				conn.closeConnect();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
	}

	private static boolean isStudentExist(DatabaseConnection conn, String studentId) {
		PreparedStatement stat = null;
		ResultSet resultSet = null;
		boolean exists = false;
		try {
			stat = conn.openConnect().prepareStatement("SELECT COUNT(*) FROM student WHERE student_id = ?");
			stat.setString(1, studentId);
			resultSet = stat.executeQuery();
			if (resultSet.next() && resultSet.getInt(1) > 0) {
				exists = true;
			}
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (stat != null) stat.close();
				conn.closeConnect();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
		return exists;
	}

	private static boolean findUserAuthentication(DatabaseConnection conn, PreparedStatement stat, ResultSet resultSet, String username, String password) {
		boolean hasResult = false;

		try {
			// Câu truy vấn
			String query = "SELECT * FROM Users WHERE username = ? AND password_hash = ?";
			// Gửi câu lệnh truy vấn tới database
			stat = conn.openConnect().prepareStatement(query);
			stat.setString(1, username);
			stat.setString(2, password);

			// Thực hiện truy vấn
			resultSet = stat.executeQuery();

			// Xử lý kết quả truy vấn
			while (resultSet.next()) {
				hasResult = true;
				System.out.println("Username: " + resultSet.getString("username"));
				System.out.println("Password: " + resultSet.getString("password_hash"));
				System.out.println("Role_id: " + resultSet.getString("role_id"));
			}

			// Kiểm tra nếu không có kết quả
			if (!hasResult) {
				System.out.println("=============================");
				System.out.println("Không tìm thấy USER này !!!.");
				System.out.println("=============================");
			} else {
				System.out.println("Đăng nhập thành công !!!");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				conn.closeConnect();
				if (stat != null) stat.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}

        return hasResult;
    }


	private static void findListStudentStatus(DatabaseConnection conn, PreparedStatement stat, ResultSet resultSet, int num) {
		try {
			String query;

			if (num==2){
				query = "SELECT * FROM Student, Attendance WHERE Student.STUDENT_ID = Attendance.STUDENT_ID";
			} else {
				query = "SELECT * FROM Student, Attendance WHERE Student.STUDENT_ID = Attendance.STUDENT_ID AND STATUS = ?";

				// Đặt giá trị cho biến num
				stat.setInt(1, num);
			}
			// Câu truy vấn (0 là có mặt 1 là vắng mặt)
			// Gửi câu lệnh truy vấn tới database
			stat = conn.openConnect().prepareStatement(query);



			// Thực hiện truy vấn
			resultSet = stat.executeQuery();

			// Xử lý kết quả truy vấn
			while (resultSet.next()) {
				System.out.println("Student_ID: " + resultSet.getString(1));
				System.out.println("Student_Name: " + resultSet.getString(2));
				System.out.println("Date: " + resultSet.getString(6));
				System.out.println("Status: " + (resultSet.getString(7).equals("1") ? "Vắng mặt" : "Có mặt"));
				System.out.println("----------------------------------");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				conn.closeConnect();
				if (stat != null) stat.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException s) {
				s.printStackTrace();
			}
		}
    }

}
