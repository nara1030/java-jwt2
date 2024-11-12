package org.among.example.menu;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {
    /**
     * 타임리프에서 filteredMenus 변수 사용 위해 추가
     * 모든 컨트롤러에서 공통으로 반환할 변수(ex. 메뉴) 공통화 목적
     */
    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest httpServletRequest, Model model) {
        List<Menu> filteredMenus = (List<Menu>) httpServletRequest.getAttribute("filteredMenus");
        model.addAttribute("filteredMenus", filteredMenus);
    }
}
