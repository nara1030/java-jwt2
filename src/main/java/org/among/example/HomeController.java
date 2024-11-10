package org.among.example;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "page/home";
    }

    @GetMapping("/test")
    public String test() {
        return "page/test";
    }
}
