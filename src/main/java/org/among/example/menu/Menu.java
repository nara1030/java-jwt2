package org.among.example.menu;

import org.among.example.role.Role;

public class Menu {
    private int id;
    private String name;
    private int depth;
    private Integer parent; // menu.json에서 최상위 null 아닌 root로 처리할 순 없을까?
    private Role role;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", depth='" + depth + '\'' +
                ", role=" + role.name() +
                '}';
    }
}
