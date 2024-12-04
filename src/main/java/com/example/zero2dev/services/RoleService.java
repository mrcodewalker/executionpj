package com.example.zero2dev.services;

import com.example.zero2dev.dtos.RoleDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.IRoleService;
import com.example.zero2dev.mapper.RoleMapper;
import com.example.zero2dev.models.Role;
import com.example.zero2dev.repositories.RoleRepository;
import com.example.zero2dev.responses.RoleResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper mapper;
    @Override
    public RoleResponse createRole(RoleDTO roleDTO) {
        this.validRole(roleDTO.getRoleName());
        return mapper.toResponse(this.roleRepository.save(mapper.toEntity(roleDTO)));
    }
    @Override
    public RoleResponse updateRole(Long id, RoleDTO roleDTO) {
        this.validRole(roleDTO.getRoleName());
        Role existingRole = this.getRole(id);
        Role newRole = mapper.parseEntity(existingRole, roleDTO);
        newRole.setId(existingRole.getId());
        return mapper.toResponse(this.roleRepository.save(newRole));
    }
    @Override
    public void deleteRoleById(Long id) {
        try {
            this.roleRepository.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new ResourceNotFoundException(e.getMessage());
        }
    }
    @Override
    public RoleResponse getRoleById(Long id) {
        return mapper.toResponse(this.getRole(id));
    }
    @Override
    public RoleResponse getRoleByName(String name) {
        return null;
    }
    public Role getRole(Long id){
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public boolean existsByRoleName(String roleName){
        return this.roleRepository.findByRoleName(roleName).isPresent();
    }
    public void validRole(String roleName){
        if ("".equals(roleName)){
            throw new ValueNotValidException(MESSAGE.INPUT_NOT_MATCH_EXCEPTION);
        }
        if (this.existsByRoleName(roleName)){
            throw new ValueNotValidException(MESSAGE.INPUT_NOT_MATCH_EXCEPTION);
        }
    }
    public Role getRoleNew(Long id){
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
}
