package vn.edu.likelion.connectDBOracle;

import com.sun.source.tree.TryTree;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.edu.likelion.connectDBOracle.model.Attribute;
import vn.edu.likelion.connectDBOracle.model.Product;
import vn.edu.likelion.connectDBOracle.model.Role;
import vn.edu.likelion.connectDBOracle.model.User;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class Application {

	private static Scanner scanner = new Scanner(System.in);
	private static User user = null;

	public static void main(String[] args) throws IOException {
		Connect conn = new Connect();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		while (true) {
			// menu chuong trinh
			showMenu();
			int choice = 0;
			try {
				choice = Integer.parseInt(scanner.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập số.");
				continue;
			}
			// =============================================
			//                 		MENU
			// =============================================
			switch (choice) {
				case 1:
					// ********************* ĐĂNG KÝ **************************
					if (user == null) signup(conn, preparedStatement, resultSet);
					else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 2:
					// ********************* ĐĂNG NHẬP *************************
					if (user == null) login(conn, preparedStatement, resultSet);
					else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 3:
					// ******************** CẬP NHẬT USER **********************
					if (user != null) {
						if (user.getRole().getRole_name().equals("admin")) {
							editUser(conn, preparedStatement, resultSet);
						}
						else System.out.println("Bạn không có quyền sử dụng chức năng này.");
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 4:
					// ******************** XÓA USER **********************
					if (user != null) {
						if (user.getRole().getRole_name().equals("admin")) {
							deleteUser(conn, preparedStatement, resultSet);
						}
						else System.out.println("Bạn không có quyền sử dụng chức năng này.");
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 5:
					// ******************** THÊM KHO **********************
					if (user != null) {
						if (user.getRole().getRole_name().equals("admin")) {
							addWarehouse(conn, preparedStatement, resultSet);
						}
						else System.out.println("Bạn không có quyền sử dụng chức năng này.");
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 6:
					// ******************** SỬA KHO **********************
					if (user != null) {
						if (user.getRole().getRole_name().equals("admin")) {
							editWarehouse(conn, preparedStatement, resultSet);
						}
						else System.out.println("Bạn không có quyền sử dụng chức năng này.");
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 7:
					// ******************** XÓA KHO **********************
					if (user != null) {
						if (user.getRole().getRole_name().equals("admin")) {
							deleteWarehouse(conn, preparedStatement, resultSet);
						}
						else System.out.println("Bạn không có quyền sử dụng chức năng này.");
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 8:
					// ************** XEM SẢN PHẨM TRONG KHO *************
					if (user != null) {
						getAllProductsInWarehouseByUserID(conn, preparedStatement, resultSet);
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 9:
					// ************** THÊM SẢN PHẨM VÀO KHO *************
					if (user != null) {
						importProductsFromExcel(conn, preparedStatement, resultSet, "DanhSachSP.xlsx");
					} else System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
					break;
				case 10:
					// ********************* ĐĂNG XUẤT *************************
					if (user != null) user = null;
					break;
				case 0:
					System.out.println("Thoát chương trình.");
					System.exit(0);
				default:
					System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
			}
		}
	}


	// =============================================
	//                 SHOW MENU
	// =============================================
	private static void showMenu() {
		System.out.println("========= MENU =========");
		if (user == null) {
			System.out.println("1. Đăng ký.");
			System.out.println("2. Đăng nhập.");
		}
		if (user != null) {
			System.out.println("3. Sửa user.");
			System.out.println("4. Xóa user.");
			System.out.println("5. Thêm kho.");
			System.out.println("6. Sửa kho.");
			System.out.println("7. Xóa kho.");
			System.out.println("8. Xem sản phẩm trong kho.");
			System.out.println("9. Thêm sản phẩm vào kho.");
			System.out.println("10. Đăng xuất.");
		}
		System.out.println("0. Thoát chương trình.");
		System.out.println("========================");
		System.out.print("Chọn chức năng: ");
	}

	// =============================================
	//            HÀM DÙNG INPUT STRING
	// =============================================
	private static String getInputString(Scanner scanner, String prompt) {
		System.out.print(prompt);
		String input = scanner.nextLine().trim();
		if (input.isEmpty()) {
			System.out.println(sliceString(prompt.toLowerCase()) + " không được để trống.");	// Chuyển về tất cả chữ thường trước khi đem đi cắt chuỗi
			return null;
		}
		return input;
	}

	// =============================================
	//            HÀM DÙNG INPUT INT
	// =============================================
	private static int getInputInt(Scanner scanner, String prompt) {
		System.out.print(prompt);
		String input = scanner.nextLine().trim();
		if (input.isEmpty()) {
			System.out.println(sliceString(prompt.toLowerCase()) + " không được để trống.");	// Chuyển về tất cả chữ thường trước khi đem đi cắt chuỗi
			return -1;																			// Trả về -1 để chỉ ra lỗi
		}
		int number;
		try {
			number = Integer.parseInt(input);
			return number;
		} catch (NumberFormatException e) {
			System.out.println(sliceString(prompt.toLowerCase()) + " phải là một số nguyên.");	// Chuyển về tất cả chữ thường trước khi đem đi cắt chuỗi
			return -1;																			// Trả về -1 để chỉ ra lỗi
		}
	}

	// =============================================
	//            HÀM DÙNG ĐỂ CẮT CHUỖI
	// =============================================
	private static String sliceString(String prompt) {											// Cắt chuỗi từ vị trí đầu tiên của "Nhập " đến trước dấu ":"
		String result = prompt.substring(prompt.indexOf("nhập ") + 5, prompt.indexOf(":"));		// indexOf: tìm vị trí của từ trong chuỗi, nếu không tìm thấy trả về -1
		result = uppercaseFirstLetter(result); 													//  Đưa vào hàm viết hoa chữ cái đầu tiên
		return result;
	}

	// =============================================
	//         HÀM VIẾT HOA CHỮ CÁI ĐẦU TIÊN
	// =============================================
	public static String uppercaseFirstLetter(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}



	// =============================================
	//                 1. ĐĂNG KÝ
	// =============================================
	private static void signup(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			String username = getInputString(scanner, "Nhập tên người dùng: ");
			if (username == null) return;
			String password = getInputString(scanner, "Nhập mật khẩu: ");
			if (password == null) return;
			String passwordEncoding = Base64.getEncoder().encodeToString(password.getBytes());

			// SHOW RA TẤT CẢ CÁC ROLE
			showRoles(conn, preparedStatement, resultSet);
			int role_id = getInputInt(scanner, "Nhập ID vai trò cho người dùng: ");
			if (role_id == -1) return;

			System.out.println("Đang trong quá trình đăng ký, vui lòng chờ trong giây lát.");
			String sqlQuery = "insert into users (username, password_hash, role_id) values (?, ?, ?)";
			conn.openConnect();
			preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, passwordEncoding);
			preparedStatement.setInt(3, role_id);
			int status = preparedStatement.executeUpdate();

			if (status > 0) System.out.println("Đăng ký thành công.");
			else System.out.println("Có lỗi trong quá trình đăng ký.");
		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình đăng ký.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
			} catch (SQLException sqlException) {
				System.out.println("Có lỗi trong quá trình đăng ký.");
				sqlException.printStackTrace();
			}
		}
	}

	// =============================================
	//                 SHOW ALL ROLES
	// =============================================
	private static void showRoles(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			System.out.println("Ứng dụng đang lấy danh sách vai trò, vui lòng chờ trong giây lát !!!");
			String sqlQuery = "select * from Roles";
			conn.openConnect();
			preparedStatement = conn.getConnect().prepareStatement(sqlQuery);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.isBeforeFirst()) {		// isBeforeFirst: để kiểm tra xem ResultSet có chứa dữ liệu hay không.
				int index = 1;
				while (resultSet.next()) {
					System.out.println(index + " " + resultSet.getString("role_name"));
					index++;
				}
			} else System.out.println("Không có dữ liệu trong hệ thống.");
		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình lấy danh sách vai trò.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				System.out.println("Có lỗi trong quá trình lấy danh sách vai trò.");
				sqlException.printStackTrace();
			}
		}
	}

	// =============================================
	//                  2. ĐĂNG NHẬP
	// =============================================
	private static void login(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			String username = getInputString(scanner, "Nhập tên người dùng: ");
			if (username == null) return;
			String password = getInputString(scanner, "Nhập mật khẩu: ");
			if (password == null) return;

			String passwordEncoding = Base64.getEncoder().encodeToString(password.getBytes());
			System.out.println("Đang trong quá trình đăng nhập, vui lòng chờ trong giây lát.");

			// Câu truy vấn
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("SELECT * FROM Users, Roles WHERE users.role_id = Roles.role_id ");
			sqlQuery.append("AND username = ? AND password_hash = ? ");
			sqlQuery.append("AND Users.isDelete = 0");
			// Gửi câu lệnh truy vấn tới database
			preparedStatement = conn.openConnect().prepareStatement(sqlQuery.toString());
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, passwordEncoding);
			// Thực hiện truy vấn
			resultSet = preparedStatement.executeQuery();


			if (resultSet.isBeforeFirst()) {		// isBeforeFirst: để kiểm tra xem ResultSet có chứa dữ liệu hay không.
				Role role = null;
				while (resultSet.next()) {			// có dữ liệu thì có thể duyệt qua từng bản ghi bằng resultSet.next()
					if (user == null) {
						user = new User();
						user.setUsername(resultSet.getString("username"));
						user.setUser_id(resultSet.getInt("user_id"));
					}
					if (role == null) {
						role = new Role();
						role.setRole_name(resultSet.getString("role_name"));
					}

				}
				if (role != null) {
					if (user != null) {
						user.setRole(role);
					}
				}
			} else System.out.println("Đăng nhập thất bại !!!");

		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình đăng nhập.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				System.out.println("Có lỗi trong quá trình đăng nhập.");
				sqlException.printStackTrace();
			}
		}
	}


	// =============================================
	//                 3. SỬA USER
	// =============================================
	private static void editUser(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		// SHOW RA TẤT CẢ CÁC USERS
		getAllUser(conn, preparedStatement, resultSet);

		int userID = getInputInt(scanner, "Nhập ID người dùng: ");
		if(userID == -1) return;

		String username = getInputString(scanner, "Nhập tên người dùng: ");
		if (username == null) return;
		String password = getInputString(scanner, "Nhập mật khẩu: ");
		if (password == null) return;
		String passwordEncoding = Base64.getEncoder().encodeToString(password.getBytes());

		int role_id = getInputInt(scanner, "Nhập ID vai trò cho người dùng: ");
		if (role_id == -1) return;

		try {
			System.out.println("Đang trong quá trình cập nhật, vui lòng chờ trong giây lát.");

			// Gửi câu lệnh truy vấn tới database
			StringBuilder sqlQuery = new StringBuilder();
			sqlQuery.append("UPDATE Users ");
			sqlQuery.append("SET username = ?, password_hash = ?, role_id = ? ");
			sqlQuery.append("WHERE user_id = ? ");
			conn.openConnect();
			preparedStatement = conn.getConnect().prepareStatement(sqlQuery.toString());

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, passwordEncoding);
			preparedStatement.setInt(3, role_id);
			preparedStatement.setInt(4, userID);

			// Thực hiện truy vấn
			int result = preparedStatement.executeUpdate();

			// Xử lý kết quả truy vấn
			if (result > 0) {
				System.out.println("=> Cập nhật thành công !!!");
			}

		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình cập nhật User !!!.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				System.out.println("Có lỗi trong quá trình cập nhật User !!!.");
				sqlException.printStackTrace();
			}
		}
	}


	// =============================================
	//                 GET ALL USER
	// =============================================
	private static  void getAllUser(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			// Gửi câu lệnh truy vấn tới database
			String sqlQuery = "SELECT * FROM Users WHERE isDelete = 0";
			preparedStatement = conn.openConnect().prepareStatement(sqlQuery);
			// Thực viện truy vấn
			resultSet = preparedStatement.executeQuery();

			// Xử lý kết quả truy vấn
			if (resultSet.isBeforeFirst()) {			// isBeforeFirst: để kiểm tra xem ResultSet có chứa dữ liệu hay không.
				System.out.println("===============================================================================================");
				System.out.println("|                                     DANH SÁCH NGƯỜI DÙNG                                    |");
				System.out.println("===============================================================================================");
				System.out.printf("|%-13s | %-25s | %-15s | %-8s | %-8s |\n", "User ID", "Username", "Password", "Role ID", "isDelete");
				System.out.println("-----------------------------------------------------------------------------------------------");

				while (resultSet.next()) {
					String userId = resultSet.getString("user_id");
					String usernameResult = resultSet.getString("username");
					String passwordHash = resultSet.getString("password_hash");
					String roleId = resultSet.getString("role_id");
					String isDelete = resultSet.getString("isDelete");

					System.out.printf("|%-13s | %-25s | %-15s | %-8s | %-8s |\n",userId, usernameResult, passwordHash, roleId, isDelete);
				}
				System.out.println("===============================================================================================");
			}

		} catch (SQLException sqlException){
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}

	// =============================================
	//                 4. XÓA USER
	// =============================================
	private static void deleteUser(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		/**
		 * 1. Hiển thị danh sách tất cả các user.
		 * 2. Nhập ID của user cần xóa.
		 * 3. Kiểm tra xem kho mà user_id đang quản lý có sản phẩm hay không.
		 * 4. Nếu kho có sản phẩm, yêu cầu chuyển sản phẩm sang kho khác trước khi xóa.
		 * 5. Thực hiện xóa kho và đánh dấu người dùng liên quan là đã xóa.
		 */

		// SHOW RA TẤT CẢ CÁC USERS
		getAllUser(conn, preparedStatement, resultSet);

		int userID = getInputInt(scanner, "Nhập ID người dùng cần xóa: ");
		if(userID == -1) return;

		try {
			conn.openConnect();

			// Kiểm tra xem người dùng có kho quản lý hay không
			String checkWarehouseQuery = "SELECT warehouse_id FROM Warehouse WHERE user_id = ? AND isDelete = 0";
			preparedStatement = conn.getConnect().prepareStatement(checkWarehouseQuery);
			preparedStatement.setInt(1, userID);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				int warehouseID = resultSet.getInt("warehouse_id");

				// Kiểm tra xem kho có sản phẩm hay không
				String checkProductsQuery = "SELECT COUNT(*) FROM Products WHERE warehouse_id = ? AND isDelete = 0";
				conn.openConnect();
				preparedStatement = conn.getConnect().prepareStatement(checkProductsQuery);
				preparedStatement.setInt(1, warehouseID);
				// Thực hiện truy vấn
				resultSet = preparedStatement.executeQuery();
				resultSet.next();
				int productCount = resultSet.getInt(1);

				if (productCount > 0) {
					System.out.println("Kho này vẫn còn sản phẩm. Vui lòng chuyển tất cả sản phẩm sang kho khác trước khi xóa.");
					return;
				}

				// Xóa kho trước khi xóa người dùng
				String deleteWarehouseQuery = "UPDATE Warehouse SET isDelete = 1 WHERE warehouse_id = ?";
				preparedStatement = conn.getConnect().prepareStatement(deleteWarehouseQuery);
				preparedStatement.setInt(1, warehouseID);
				int warehouseResult = preparedStatement.executeUpdate();

				if (warehouseResult > 0) {
					System.out.println("=> Xóa kho thành công !!!");

					// Xóa người dùng
					String deleteUserQuery = "UPDATE Users SET isDelete = 1 WHERE user_id = ?";
					preparedStatement = conn.getConnect().prepareStatement(deleteUserQuery);
					preparedStatement.setInt(1, userID);
					int userResult = preparedStatement.executeUpdate();

					if (userResult > 0) {
						System.out.println("=> Xóa người dùng thành công !!!");
						// SHOW RA TẤT CẢ CÁC USERS
						getAllUser(conn, preparedStatement, resultSet);
					} else {
						System.out.println("Có lỗi trong quá trình xóa người dùng.");
					}
				} else {
					System.out.println("Có lỗi trong quá trình xóa kho.");
				}
			} else {
				System.out.println("Người dùng này không quản lý kho nào hoặc kho đã bị xóa !!!");
				// Xóa người dùng không có kho
				String deleteUserQuery = "UPDATE Users SET isDelete = 1 WHERE user_id = ?";
				preparedStatement = conn.getConnect().prepareStatement(deleteUserQuery);
				preparedStatement.setInt(1, userID);
				int userResult = preparedStatement.executeUpdate();

				if (userResult > 0) {
					System.out.println("=> Xóa người dùng thành công !!!");
					// SHOW RA TẤT CẢ CÁC USERS
					getAllUser(conn, preparedStatement, resultSet);
				} else {
					System.out.println("Có lỗi trong quá trình xóa người dùng.");
				}
			}

		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình cập nhật User !!!.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				System.out.println("Có lỗi trong quá trình cập nhật User !!!.");
				sqlException.printStackTrace();
			}
		}
	}

	// =============================================
	//                  5. THÊM KHO
	// =============================================
	private static void addWarehouse(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		String warehouseName = getInputString(scanner, "Nhập tên của nhà kho: ");
		if (warehouseName == null) return;

		// SHOW ALL USER
		getAllUser(conn, preparedStatement, resultSet);
		int userID = getInputInt(scanner, "Nhập ID người dùng quản lý kho (nhập 0 nếu không có): ");
		if(userID == -1) return;

		Integer userIdValue = null;
		if (userID != 0) {
			userIdValue = userID;
		}

		try {
			System.out.println("Đang trong quá trình thêm kho, vui lòng chờ trong giây lát.");

			// Gửi câu lệnh truy vấn tới database
			String sqlQuery = "INSERT INTO Warehouse (warehouse_name, user_id, isDelete) VALUES (?, ?, 0)";
			preparedStatement = conn.openConnect().prepareStatement(sqlQuery);

			preparedStatement.setString(1, warehouseName);
			if (userIdValue == null) {
				preparedStatement.setNull(2, java.sql.Types.INTEGER);
			} else {
				preparedStatement.setInt(2, userIdValue);
			}

			// Thực hiện truy vấn
			int result = preparedStatement.executeUpdate();

			// Xử lý kết quả truy vấn
			if (result > 0) {
				// SHOW ALL WARE HOUSE
				getAllWarehouse(conn, preparedStatement, resultSet);
				System.out.println("Thêm kho thành công !!!");
			} else {
				System.out.println("Có lỗi trong quá trình thêm kho.");
			}

		} catch (SQLException sqlException) {
			if (sqlException.getErrorCode() == 1) { // ORA-00001: unique constraint violated
				System.out.println("Người dùng này đã quản lý một kho khác. Vui lòng chọn người dùng khác.");
			} else {
				System.out.println("Có lỗi trong quá trình thêm kho.");
				sqlException.printStackTrace();
			}
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}

	// =============================================
	//                GET ALL WAREHOUSE
	// =============================================
	private static void getAllWarehouse(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			// Gửi câu lệnh truy vấn tới database
			String sqlQuery = "SELECT * FROM Warehouse WHERE isDelete = 0";
			preparedStatement = conn.openConnect().prepareStatement(sqlQuery);
			// Thực hiện truy vấn
			resultSet = preparedStatement.executeQuery();

			// Xử lý kết quả truy vấn
			if (resultSet.isBeforeFirst()) { // isBeforeFirst: để kiểm tra xem ResultSet có chứa dữ liệu hay không.
				System.out.println("===============================================================================================");
				System.out.println("|                                     DANH SÁCH NHÀ KHO                                       |");
				System.out.println("===============================================================================================");
				System.out.printf("|%-13s | %-25s | %-15s | %-8s |\n", "Warehouse ID", "Warehouse Name", "User ID", "isDelete");
				System.out.println("-----------------------------------------------------------------------------------------------");

				while (resultSet.next()) {
					String warehouseId = resultSet.getString("warehouse_id");
					String warehouseName = resultSet.getString("warehouse_name");
					String userId = resultSet.getString("user_id");
					String isDelete = resultSet.getString("isDelete");

					System.out.printf("|%-13s | %-25s | %-15s | %-8s |\n", warehouseId, warehouseName, userId, isDelete);
				}
				System.out.println("===============================================================================================");
			}

		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}

	// =============================================
	//                  6. SỬA KHO
	// =============================================
	private static void editWarehouse(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		// SHOW ALL WARE HOUSE
		getAllWarehouse(conn, preparedStatement, resultSet);

		int warehouseID = getInputInt(scanner, "Nhập ID kho cần chỉnh sửa: ");
		if(warehouseID == -1) return;

		String warehouseName = getInputString(scanner, "Nhập tên kho: ");
		if (warehouseName == null) return;

		// SHOW ALL USER
		getAllUser(conn, preparedStatement, resultSet);
		int userID = getInputInt(scanner, "Nhập ID người dùng quản lý kho (nhập 0 nếu không có): ");
		if(userID == -1) return;

		Integer userIdValue = null;
		if (userID != 0) {
			userIdValue = userID;
		}

		try {
			System.out.println("Đang trong quá trình thêm kho, vui lòng chờ trong giây lát.");

			// Gửi câu lệnh truy vấn tới database
			String sqlQuery = "UPDATE Warehouse SET warehouse_name = ?, user_id = ? WHERE warehouse_id = ? ";
			preparedStatement = conn.openConnect().prepareStatement(sqlQuery);

			preparedStatement.setString(1, warehouseName);
			if (userIdValue == null) {
				preparedStatement.setNull(2, java.sql.Types.INTEGER);
			} else {
				preparedStatement.setInt(2, userIdValue);
			}
			preparedStatement.setInt(3, warehouseID);

			// Thực hiện truy vấn
			int result = preparedStatement.executeUpdate();

			// Xử lý kết quả truy vấn
			if (result > 0) {
				// SHOW ALL WARE HOUSE
				getAllWarehouse(conn, preparedStatement, resultSet);
				System.out.println("Cập nhật kho thành công !!!");
			} else {
				System.out.println("Có lỗi trong quá trình cập nhật kho.");
			}

		} catch (SQLException sqlException) {
			if (sqlException.getErrorCode() == 1) { // ORA-00001: unique constraint violated
				System.out.println("Người dùng này đã quản lý một kho khác. Vui lòng chọn người dùng khác.");
			} else {
				System.out.println("Có lỗi trong quá trình cập nhật kho.");
				sqlException.printStackTrace();
			}
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}


	// =============================================
	//                 7. XÓA KHO
	// =============================================
	private static void deleteWarehouse(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		/**
		 * 1. Hiển thị danh sách tất cả các kho.
		 * 2. Nhập ID của kho cần xóa.
		 * 3. Kiểm tra xem kho có sản phẩm hay không.
		 * 4. Nếu kho có sản phẩm, yêu cầu chuyển sản phẩm sang kho khác trước khi xóa.
		 * 5. Thực hiện xóa kho và đánh dấu người dùng liên quan là đã xóa.
		 */

		// SHOW RA TẤT CẢ CÁC WAREHOUSE
		getAllWarehouse(conn, preparedStatement, resultSet);

		int warehouseID = getInputInt(scanner, "Nhập ID kho cần xóa: ");
		if (warehouseID == -1) return;

		try {

			// Kiểm tra xem kho đang có sản phẩm hay không
			String checkProductsQuery = "SELECT COUNT(*) FROM Products WHERE warehouse_id = ? AND isDelete = 0";
			conn.openConnect();
			preparedStatement = conn.getConnect().prepareStatement(checkProductsQuery);
			preparedStatement.setInt(1, warehouseID);
			// Thực hiện truy vấn
			resultSet = preparedStatement.executeQuery();
			resultSet.next();
			int productCount = resultSet.getInt(1);

			if (productCount > 0) {
				// ******************************************
				// CHUYỂN SẢN PHẨM SANG KHO KHÁC ĐỂ XÓA KHO
				// ******************************************
				System.out.println("Kho này vẫn còn sản phẩm. Vui lòng chuyển tất cả sản phẩm sang kho khác trước khi xóa.");

				int warehouseIDNew = getInputInt(scanner, "Nhập ID kho cần chuyển sản phẩm đến: ");
				if (warehouseIDNew == -1) return;

				String updateWarehouseQuery = "UPDATE Products SET warehouse_id = ? WHERE warehouse_id = ?";
				preparedStatement = conn.getConnect().prepareStatement(updateWarehouseQuery);
				preparedStatement.setInt(1, warehouseIDNew);
				preparedStatement.setInt(2, warehouseID);
				// Thực hiện truy vấn
				resultSet = preparedStatement.executeQuery();

				if (resultSet.next()) {
					System.out.println("=> Cập nhật ID kho thành công !!!");
				} else {
					System.out.println("Có lỗi trong quá trình cập nhật kho !!!");
				}

			}

			// Xóa kho và đánh dấu người dùng liên quan là đã xóa
			String deleteWarehouseQuery = "UPDATE Warehouse SET isDelete = 1 WHERE warehouse_id = ?";
			preparedStatement = conn.getConnect().prepareStatement(deleteWarehouseQuery);
			preparedStatement.setInt(1, warehouseID);
			int warehouseResult = preparedStatement.executeUpdate();

			if (warehouseResult > 0) {
				String deleteUserQuery = "UPDATE Users SET isDelete = 1 WHERE user_id = (SELECT user_id FROM Warehouse WHERE warehouse_id = ?)";
				preparedStatement = conn.getConnect().prepareStatement(deleteUserQuery);
				preparedStatement.setInt(1, warehouseID);
				int userResult = preparedStatement.executeUpdate();

				if (userResult > 0) {
					System.out.println("=> Xóa kho và người dùng thành công !!!");
					// SHOW RA TẤT CẢ CÁC WAREHOUSE
					getAllWarehouse(conn, preparedStatement, resultSet);
				} else {
					System.out.println("Không có người dùng để xóa !!!");
					System.out.println("=> Xóa kho thành công !!!");
					// SHOW RA TẤT CẢ CÁC WAREHOUSE
					getAllWarehouse(conn, preparedStatement, resultSet);
				}
			} else {
				System.out.println("Có lỗi trong quá trình xóa kho.");
			}

		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình xóa kho !!!.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}

	// =======================================================
	//                 8. XEM SẢN PHẨM TRONG KHO (By user_id)
	// =======================================================
	private static void getAllProductsInWarehouseByUserID(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		if (user != null) {
			int warehouseID;

			if ("admin".equals(user.getRole().getRole_name())) {
				getAllWarehouse(conn, preparedStatement, resultSet);
				warehouseID = getInputInt(scanner, "Nhập ID kho cần xem sản phẩm: ");
				if (warehouseID == -1) return;
			} else {
				String userWarehouseQuery = "SELECT warehouse_id FROM Warehouse WHERE user_id = ? AND isDelete = 0";
				try {
					preparedStatement = conn.openConnect().prepareStatement(userWarehouseQuery);
					preparedStatement.setInt(1, user.getUser_id());
					resultSet = preparedStatement.executeQuery();
					if (resultSet.next()) {
						warehouseID = resultSet.getInt("warehouse_id");
					} else {
						System.out.println("Không tìm thấy kho của người dùng này.");
						return;
					}
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					return;
				} finally {
					try {
						if (resultSet != null) resultSet.close();
					} catch (SQLException sqlException) {
						sqlException.printStackTrace();
					}
				}
			}

			try {
				String sqlQuery = "SELECT * FROM Products WHERE warehouse_id = ? AND isDelete = 0";
				preparedStatement = conn.openConnect().prepareStatement(sqlQuery);
				preparedStatement.setInt(1, warehouseID);
				resultSet = preparedStatement.executeQuery();

				if (resultSet.isBeforeFirst()) {
					System.out.println("===============================================================================================");
					System.out.println("|                                     DANH SÁCH SẢN PHẨM                                    |");
					System.out.println("===============================================================================================");
					System.out.printf("|%-13s | %-25s | %-15s | %-8s |\n", "Product ID", "Product Name", "Warehouse ID", "isDelete");
					System.out.println("-----------------------------------------------------------------------------------------------");

					while (resultSet.next()) {
						String productId = resultSet.getString("product_id");
						String productName = resultSet.getString("product_name");
						String warehouseId = resultSet.getString("warehouse_id");
						String isDelete = resultSet.getString("isDelete");

						System.out.printf("|%-13s | %-25s | %-15s | %-8s |\n", productId, productName, warehouseId, isDelete);
					}
					System.out.println("===============================================================================================");
				} else {
					System.out.println("Không có sản phẩm nào trong kho này.");
				}

			} catch (SQLException sqlException) {
				System.out.println("Có lỗi trong quá trình xem sản phẩm trong kho.");
				sqlException.printStackTrace();
			} finally {
				try {
					if (conn != null) conn.closeConnect();
					if (preparedStatement != null) preparedStatement.close();
					if (resultSet != null) resultSet.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
				}
			}
		} else {
			System.out.println("Bạn cần đăng nhập để thực hiện chức năng này.");
		}
	}

	// =======================================================
	//                 GET ALL PRODUCT BY USER ID
	// =======================================================
	private static void getAllProductsByUserID (Connect conn, PreparedStatement preparedStatement, ResultSet resultSet) {
		try {
			String sqlQuery = "SELECT warehouse_id FROM Warehouse WHERE user_id = ? AND isDelete = 0";
			preparedStatement = conn.openConnect().prepareStatement(sqlQuery);
			preparedStatement.setInt(1, user.getUser_id());
			resultSet = preparedStatement.executeQuery();


			if (resultSet.next()) {
				System.out.println(resultSet.getInt("warehouse_id"));

				String sqlQueryProducts = "SELECT * FROM Products WHERE warehouse_id = ? AND isDelete = 0";
				preparedStatement = conn.openConnect().prepareStatement(sqlQueryProducts);
				preparedStatement.setInt(1, resultSet.getInt(1));
				resultSet = preparedStatement.executeQuery();

				if (resultSet.isBeforeFirst()) {
					System.out.println("===============================================================================================");
					System.out.println("|                                     DANH SÁCH SẢN PHẨM                                    |");
					System.out.println("===============================================================================================");
					System.out.printf("|%-13s | %-25s | %-15s | %-8s |\n", "Product ID", "Product Name", "Warehouse ID", "isDelete");
					System.out.println("-----------------------------------------------------------------------------------------------");

					while (resultSet.next()) {
						String productId = resultSet.getString("product_id");
						String productName = resultSet.getString("product_name");
						String warehouseId = resultSet.getString("warehouse_id");
						String isDelete = resultSet.getString("isDelete");

						System.out.printf("|%-13s | %-25s | %-15s | %-8s |\n", productId, productName, warehouseId, isDelete);
					}
					System.out.println("===============================================================================================");
				} else {
					System.out.println("Không có sản phẩm nào trong kho này.");
				}

			} else System.out.println("Không tìm thấy warehouse_id !!!");


		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình xem sản phẩm trong kho.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}



	// =======================================================
	//                 	9. THÊM SẢN PHẨM TỪ FILE EXCEL
	// =======================================================
	private static List<Product> readProductsFromExcel(String excelFilePath) {
		List<Product> productList = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(excelFilePath);
			 Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0);
			for (int rowIndex = 5; rowIndex <= 12; rowIndex++) { // Đọc dữ liệu từ hàng thứ 6 trở đi
				Row row = sheet.getRow(rowIndex);
				if (row != null) {
					Product product = new Product();
					product.setProduct_name(getCellValueAsString(row.getCell(1))); // Cột B cho tên sản phẩm

					Attribute attribute = new Attribute();
					attribute.setDescription(getCellValueAsString(row.getCell(2))); // Cột C cho mô tả
					attribute.setQuantity((int) row.getCell(3).getNumericCellValue()); // Cột D cho số lượng
					attribute.setPrice((int) row.getCell(4).getNumericCellValue()); // Cột E cho giá

					product.addAttribute(attribute);
					productList.add(product);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return productList;
	}

	private static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				return String.valueOf((int) cell.getNumericCellValue());
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				return cell.getCellFormula();
			default:
				return "";
		}
	}


	private static void importProductsFromExcel(Connect conn, PreparedStatement preparedStatement, ResultSet resultSet, String excelFilePath) {
		List<Product> productList = readProductsFromExcel(excelFilePath);

		int warehouseID;
		if ("admin".equals(user.getRole().getRole_name())) {
			getAllWarehouse(conn, preparedStatement, resultSet);
			warehouseID = getInputInt(scanner, "Nhập ID kho cần nhập sản phẩm: ");
			if (warehouseID == -1) return;
		} else {
			String userWarehouseQuery = "SELECT warehouse_id FROM Warehouse WHERE user_id = ? AND isDelete = 0";
			try {
				preparedStatement = conn.openConnect().prepareStatement(userWarehouseQuery);
				preparedStatement.setInt(1, user.getUser_id());
				resultSet = preparedStatement.executeQuery();
				if (resultSet.next()) {
					warehouseID = resultSet.getInt("warehouse_id");
				} else {
					System.out.println("Không tìm thấy kho của người dùng này.");
					return;
				}
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				return;
			} finally {
				try {
					if (resultSet != null) resultSet.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
				}
			}
		}

		try {
			conn.openConnect();
			String productQuery = "INSERT INTO Products (warehouse_id, product_name, isDelete) VALUES (?, ?, 0)";
			String attributeQuery = "INSERT INTO Attributes (product_id, description, quantity, price, isDelete) VALUES (?, ?, ?, ?, 0)";
			preparedStatement = conn.getConnect().prepareStatement(productQuery, new String[]{"product_id"});
			PreparedStatement attributeStmt = conn.getConnect().prepareStatement(attributeQuery);

			for (Product product : productList) {
				preparedStatement.setInt(1, warehouseID);
				preparedStatement.setString(2, product.getProduct_name());
				preparedStatement.executeUpdate();
				ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
				if (generatedKeys.next()) {
					int productId = generatedKeys.getInt(1);
					for (Attribute attribute : product.getAttributes()) {
						attributeStmt.setInt(1, productId);
						attributeStmt.setString(2, attribute.getDescription());
						attributeStmt.setInt(3, attribute.getQuantity());
						attributeStmt.setInt(4, attribute.getPrice());
						attributeStmt.addBatch();
					}
				}
			}

			attributeStmt.executeBatch();
			System.out.println("Đã nhập: " + productList.size() + " sản phẩm vào kho ID: " + warehouseID);

		} catch (SQLException sqlException) {
			System.out.println("Có lỗi trong quá trình nhập sản phẩm.");
			sqlException.printStackTrace();
		} finally {
			try {
				if (conn != null) conn.closeConnect();
				if (preparedStatement != null) preparedStatement.close();
				if (resultSet != null) resultSet.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
	}

}
