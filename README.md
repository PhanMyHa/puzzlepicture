Picture Puzzle

Picture Puzzle là một trò chơi giải đố trên Android, trong đó người chơi xoay các mảnh ghép của hình ảnh để khôi phục lại bức hình ban đầu. Mỗi lần chạm vào một ô, ô đó sẽ xoay 90 độ theo chiều kim đồng hồ. Người chơi cần xoay đúng tất cả các mảnh để hoàn thành bức tranh.

Ứng dụng cung cấp nhiều mức độ khó, bộ sưu tập hình ảnh đa dạng, hệ thống theo dõi tiến trình và thống kê kết quả chơi.

Giới thiệu

Picture Puzzle được thiết kế nhằm mang lại trải nghiệm giải đố đơn giản nhưng thử thách. Người chơi phải quan sát và xoay các ô hình sao cho tất cả các phần của bức ảnh được căn chỉnh đúng vị trí.

Trò chơi bao gồm nhiều kích thước lưới khác nhau để tăng độ khó và giúp người chơi cải thiện kỹ năng giải đố.

Tính năng
Gameplay chính

Xoay ô hình bằng cách chạm vào ô (mỗi lần xoay 90 độ)

Nhiều kích thước lưới khác nhau

3x3 – dễ

4x4 – trung bình

5x5 – khó

6x6 – rất khó

Tự động phát hiện khi người chơi hoàn thành puzzle

Bộ đếm số lần di chuyển

Bộ đếm thời gian hoàn thành

Thư viện hình ảnh

Hơn 25 hình ảnh chất lượng cao

5 danh mục hình ảnh

Nature

Animals

Art

Architecture

Food

Theo dõi các puzzle đã hoàn thành

Lọc hình ảnh theo danh mục

Trải nghiệm người dùng

Hiệu ứng chiến thắng khi hoàn thành puzzle

Âm thanh khi xoay ô và khi chiến thắng

Hiệu ứng phóng to hình ảnh khi hoàn thành

Hộp thoại hiển thị kết quả gồm thời gian và số bước

Theo dõi tiến trình

Lưu kết quả chơi trong cơ sở dữ liệu

Theo dõi thời gian tốt nhất cho từng mức độ

Hiển thị trạng thái đã hoàn thành của từng puzzle

Thống kê quá trình chơi

Giao diện

Màn hình Splash khi mở ứng dụng

Hộp thoại chọn mức độ trước khi chơi

Hệ thống gợi ý để hiển thị các ô chưa đúng

Điều hướng mượt mà giữa các màn hình

Hỗ trợ Dark Mode trong tương lai

Kiến trúc hệ thống

Dự án được xây dựng theo nguyên tắc Clean Architecture kết hợp với mô hình MVVM.

Presentation Layer
(Activities, Fragments, Views, ViewBinding)

        ↓ LiveData

ViewModel Layer
(Business Logic, UI State)

        ↓ Coroutines

Repository Layer
(Data coordination)

        ↓

Data Layer
(Room Database, DAO, Entities)
Cấu trúc thư mục
app/src/main/java/com/example/picturepuzzle/

data/
    database/        Room database, DAO, Entity
    model/           Các model dữ liệu
    repository/      Repository quản lý dữ liệu

di/
    Module dependency injection (Hilt)

ui/
    dialog/          Custom dialog
    game/            Màn hình chơi game
    gallery/         Màn hình thư viện ảnh
    profile/         Màn hình hồ sơ (dự kiến)
    settings/        Màn hình cài đặt (dự kiến)

utils/
    Các lớp tiện ích

MainActivity.kt
SplashActivity.kt
PuzzleApplication.kt
Công nghệ sử dụng
Core

Ngôn ngữ: Kotlin

Min SDK: 24 (Android 7.0)

Target SDK: 34 (Android 14)

Kiến trúc

MVVM

ViewModel

LiveData

Navigation Component

Hilt Dependency Injection

Giao diện

View Binding

RecyclerView

DiffUtil

Material Design Components

Custom View với Canvas

Lottie Animation

Glide để tải hình ảnh

Lưu trữ dữ liệu

Room Database

SharedPreferences

Kotlin Coroutines

Âm thanh

SoundPool để phát hiệu ứng âm thanh

Cài đặt
Yêu cầu

Android Studio Hedgehog 2023.1.1 hoặc mới hơn

JDK 17

Android SDK 34

Gradle 8.0 trở lên

Các bước cài đặt
1. Clone repository
git clone https://github.com/yourusername/picture-puzzle.git
cd picture-puzzle
2. Mở project trong Android Studio

File → Open → Chọn thư mục project

3. Đồng bộ Gradle

Android Studio sẽ tự động đồng bộ các file Gradle.
