package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class StudentController {
    @GetMapping("/new")
    public String getStudents() {
        return "text";
    }

    @GetMapping("/person")
    public String getPerson(Model model) {
        model.addAttribute("something", "this is person");
        return "person";
    }
}

