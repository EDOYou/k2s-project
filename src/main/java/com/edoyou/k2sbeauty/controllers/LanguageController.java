package com.edoyou.k2sbeauty.controllers;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Controller
public class LanguageController {

  @GetMapping("/changeLanguage")
  public String changeLanguage(HttpServletRequest request, @RequestParam String lang) {
    Locale locale = Locale.forLanguageTag(lang);
    request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
    return "redirect:" + request.getHeader("referer");
  }
}