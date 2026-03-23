package com.example.courseregistration.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CourseForm {

    @NotBlank(message = "Tên học phần không được để trống")
    @Size(max = 150, message = "Tên học phần tối đa 150 ký tự")
    private String name;

    @NotBlank(message = "Hình ảnh không được để trống")
    @Size(max = 255, message = "Đường dẫn hình ảnh tối đa 255 ký tự")
    private String image;

    @NotNull(message = "Số tín chỉ không được để trống")
    @Min(value = 1, message = "Số tín chỉ phải từ 1 đến 10")
    @Max(value = 10, message = "Số tín chỉ phải từ 1 đến 10")
    private Integer credits;

    @NotBlank(message = "Giảng viên không được để trống")
    @Size(max = 100, message = "Tên giảng viên tối đa 100 ký tự")
    private String lecturer;

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
