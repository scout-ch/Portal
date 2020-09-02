package ch.itds.pbs.portal.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class LogoutController {

    @Value("${logout-success-url}")
    String logoutSuccessUrl;

    @RequestMapping("/logoutMidata/{lang}")
    public String logoutMidata(@PathVariable String lang, Model model) {

        String sanitizedLang = "de";
        if (List.of("de", "fr", "it").contains(lang)) {
            sanitizedLang = lang;
        }

        String url = logoutSuccessUrl.replaceAll("\\{lang\\}", sanitizedLang);

        model.addAttribute("localizedLogoutUrl", url);

        return "logoutMidata";
    }

}
