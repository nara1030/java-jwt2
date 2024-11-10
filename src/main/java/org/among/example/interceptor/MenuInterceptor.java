package org.among.example.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.among.example.menu.Menu;
import org.among.example.menu.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class MenuInterceptor implements HandlerInterceptor {
    @Autowired
    private MenuService menuService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 불필요한 경로(ex. Rest API) 리턴

        // 전체 메뉴(화면에서 필터링)
        List<Menu> allMenus = menuService.getMenus();

        // 필터링 메뉴(서버에서 필터링)
        List<Menu> filteredMenus = menuService.getFilteredMenus(SecurityContextHolder.getContext().getAuthentication());
        System.out.println("Filtered Menus: ");
        System.out.println(filteredMenus);

        request.setAttribute("allMenus", allMenus);
        request.setAttribute("filteredMenus", filteredMenus);

        return true;
    }
}
