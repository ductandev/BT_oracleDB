package vn.edu.likelion.connectDBOracle.Tool;

import java.util.InputMismatchException;
import java.util.Scanner;

public class KiemTraDieuKien {

    public static int userInputInt() {
        int choice = 0;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                break;
            } catch (InputMismatchException e) {
                System.out.println("* Error: Vui lòng chỉ nhập số nguyên !!!");
                scanner.nextLine(); // Clear invalid input
                System.out.print("Nhập lại: ");
            }
        }
        return choice;
    }

    public static String userInputString() {
        String choise = "";
        Scanner scanner = new Scanner(System.in);
        while (true){
            try {
                choise = scanner.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.err.println("* Error: Vui lòng chỉ nhập ký tự !!!");
                scanner.close();
            }
        }
        return choise;
    }


    public boolean KiemTraChuoiRong(String chuoi) {
        if(chuoi.isEmpty()) {
            System.out.println("Chuoi nhap vao khong duoc de trong, nhap lai:");
            return false;
        } else return true;
    }

    public boolean KiemTraChieuDaiChuoi(String chuoi, int chieuDai) {
        if(chuoi.length() == chieuDai) return true;
        else {
            System.out.println("Chuoi nhap vao phai co chieu dai la " + chieuDai + ", nhap lai:");
            return false;
        }
    }

    public boolean KiemTraChuoiSo(String chuoi) {
        if(chuoi == null || chuoi.equals("")) {
            System.out.println("Chuoi la gia tri null, hoac khong chua ky tu, nhap lai:");
            return false;
        }
        try {
            long iVal = Long.parseLong(chuoi);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Chuoi chua ky tu khong phai so, nhap lai:");
            return false;
        }
    }

    public boolean KiemTraLaSoDuong(double so) {
        if(so < 0) {
            System.out.println("Doanh thu thang >= 0, nhap lai:");
            return false;
        } else return true;
    }

    public boolean KiemTraSoNgayLamViec(int soNgayLamViec, int thang, int nam) {
        int soNgayCuaThang = SoNgayCuaThang(thang, nam);
        if(soNgayLamViec >= 0 && soNgayLamViec <= soNgayCuaThang) return true;
        else {
            System.out.println("So ngay lam viec cua thang " + thang + " tu 0 - " + soNgayCuaThang + ", nhap lai:");
            return false;
        }
    }

    private int SoNgayCuaThang(int thang, int nam) {
        switch (thang) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if((nam % 4 == 0 && nam % 100 != 0) || nam % 400 == 0) return 29;
                else return 28;
            default:
                return -1;
        }
    }

    public boolean KiemTraPhanTramCoPhieu(float ptCoPhieu) {
        if(ptCoPhieu >= 0 && ptCoPhieu <= 100) return true;
        else {
            System.out.println("Phan tram co phieu trong khoang 0 - 100");
            return false;
        }
    }
}
