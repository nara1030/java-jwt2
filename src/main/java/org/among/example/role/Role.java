package org.among.example.role;

/**
 * Custom Role 적용 위해 분리
 * User와 Menu와 관계(추후 CRUD 권한 분리 고려..)
 */
public enum Role {
    ANONYMOUS(0), // UNREGISTERED로 명칭했더니 에러가.. fromAuthority 메소드 참고
    MEMBER(1),
    MANAGER(2),
    ADMIN(3);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static boolean hasAccess(Role userRole, Role menuRole) {
        return userRole.getLevel() >= menuRole.getLevel();
    }

    // java.lang.IllegalArgumentException: No enum constant org.among.example.role.Role.ROLE_ANONYMOUS
    // ROLE_ 접두사를 자동으로 제거하고 매칭되는 Role을 반환하는 방법을 추가
    public static Role fromAuthority(String authority) {
        String role = authority.replace("ROLE_", "");  // ROLE_ 접두사 제거
        return Role.valueOf(role);
    }
}
