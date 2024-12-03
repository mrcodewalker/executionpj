package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.RoleDTO;
import com.example.zero2dev.responses.RoleResponse;

public interface IRoleService {
    RoleResponse createRole(RoleDTO roleDTO);
    RoleResponse updateRole(Long id, RoleDTO roleDTO);
    void deleteRoleById(Long id);
    RoleResponse getRoleById(Long id);
    RoleResponse getRoleByName(String name);
}
