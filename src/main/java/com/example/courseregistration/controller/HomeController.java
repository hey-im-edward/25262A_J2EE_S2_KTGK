package com.example.courseregistration.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.courseregistration.entity.Course;
import com.example.courseregistration.service.CourseService;

@Controller
public class HomeController {

    private final CourseService courseService;

    public HomeController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping({"/", "/home", "/courses"})
    public String showHome(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "keyword", defaultValue = "") String keyword,
        Model model
    ) {
        Page<Course> coursePage = courseService.findCourses(keyword, page);
        model.addAttribute("coursePage", coursePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("pageTitle", "Danh sách học phần");
        return "home";
    }

    @GetMapping("/courses/suggestions")
    @ResponseBody
    public List<String> suggestCourses(@RequestParam(name = "keyword", defaultValue = "") String keyword) {
        return courseService.suggestCourseNames(keyword);
    }
}
