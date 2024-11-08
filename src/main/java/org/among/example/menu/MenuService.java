package org.among.example.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.among.example.role.Role;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private static final String menuFilePath = "data/menu.json";

    public List<Menu> getMenus() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(menuFilePath);

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(classPathResource.getInputStream(), new TypeReference<Map<String, List<Menu>>>() {}).get("menu");
    }

    public List<Menu> getFilteredMenus(Authentication authentication) throws IOException {
        // 1. 전체 메뉴 읽어오기
        List<Menu> allMenus = getMenus();

        // 2. 현재 로그인한 사용자의 권한 가져오기(권한을 복수개로 세팅해야 하나?)
        List<Role> userRoles = authentication.getAuthorities().stream()
                .map(grantedAuthority -> Role.fromAuthority(grantedAuthority.getAuthority()))
                .toList();

        // 3. 사용자 권한에 해당하는 메뉴들만 필터링
        return allMenus.stream()
                .filter(menu -> hasAccessToMenu(menu, userRoles))
                .collect(Collectors.toList());
    }

    private boolean hasAccessToMenu(Menu menu, List<Role> userRoles) {
        Role menuRole = menu.getRole();

        // 사용자가 가진 권한 중 하나라도 메뉴 접근 가능 시 true
        for (Role userRole : userRoles) {
            if (Role.hasAccess(userRole, menuRole)) {
                return true;
            }
        }
        return false;
    }
}
