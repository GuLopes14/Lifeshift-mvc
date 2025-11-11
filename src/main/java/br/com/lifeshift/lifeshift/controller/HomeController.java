package br.com.lifeshift.lifeshift.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal OAuth2User user) {
        model.addAttribute("user", user);
        model.addAttribute("currentTime", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        return "index";
    }
}
