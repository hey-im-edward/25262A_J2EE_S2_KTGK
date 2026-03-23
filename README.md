# 25262A_J2EE_S2_KTGK

## Course Registration Application

Ứng dụng đăng ký học phần cho sinh viên viết bằng Spring Boot, Thymeleaf, Spring Security, OAuth2 Login và H2 Database.

## Chức năng đã làm

- Trang Home hiển thị danh sách học phần, có tìm kiếm theo tên và phân trang 5 học phần mỗi trang
- CRUD học phần cho `ADMIN`
- CRUD danh mục cho `ADMIN`
- Đăng ký tài khoản sinh viên, mặc định gán quyền `STUDENT`
- Đăng nhập bằng `username/password`
- Phân quyền:
  - `/admin/**` chỉ `ADMIN`
  - `/courses` cho tất cả người dùng
  - `/enroll/**` chỉ `STUDENT`
- Sinh viên đăng ký học phần bằng nút `Enroll`
- Trang `My Courses` hiển thị các học phần đã đăng ký
- Đăng nhập bằng Google OAuth2
- Giao diện responsive

## Tài khoản mẫu

- `admin / admin123`
- `student / student123`

## Cách chạy

1. Cài `Java 17+`
2. Chạy lệnh:

```bash
./mvnw spring-boot:run
```

Trên Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

3. Mở trình duyệt tại:

```text
http://localhost:9090/home
```

## Cấu hình Google OAuth2

Thiết lập biến môi trường trước khi chạy:

```powershell
$env:GOOGLE_CLIENT_ID="your-google-client-id"
$env:GOOGLE_CLIENT_SECRET="your-google-client-secret"
```

Sau đó chạy lại ứng dụng.
