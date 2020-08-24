package ch.itds.pbs.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public String loginForm() {

        //noinspection SpringMVCViewInspection
        return "redirect:/oauth2/authorize/midata";
    }

}