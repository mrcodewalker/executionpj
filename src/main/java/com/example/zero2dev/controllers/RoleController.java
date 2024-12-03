package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.RoleDTO;
import com.example.zero2dev.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    @PostMapping("/create")
    public ResponseEntity<?> createRole(
            @RequestBody RoleDTO roleDTO){
        return ResponseEntity.ok(this.roleService.createRole(roleDTO));
    }
    @GetMapping("/filter/{id}")
    public ResponseEntity<?> filterRole(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.roleService.getRoleById(id));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRole(
            @PathVariable("id") Long id,
            @RequestBody RoleDTO roleDTO){
        return ResponseEntity.ok(this.roleService.updateRole(id, roleDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") Long id){
        this.roleService.deleteRoleById(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
