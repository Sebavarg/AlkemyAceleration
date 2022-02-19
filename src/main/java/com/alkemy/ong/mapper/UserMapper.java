package com.alkemy.ong.mapper;

import com.alkemy.ong.domain.User;
import com.alkemy.ong.dto.UserCreationDTO;
import com.alkemy.ong.dto.UserDTO;
import com.alkemy.ong.repository.model.RoleModel;
import com.alkemy.ong.repository.model.UserModel;

public class UserMapper {

    public static User mapDtoCreationToDomain(UserCreationDTO userCreationDTO){
        User userDomain = User.builder().
                email(userCreationDTO.getEmail()).
                firstName(userCreationDTO.getName()).
                lastName(userCreationDTO.getLastname()).
                password(userCreationDTO.getPassword()).build();
        return userDomain;
    }

    public static UserCreationDTO mapModelToDtoCreation(UserModel user){
        UserCreationDTO userCreationDTO = UserCreationDTO.builder().
                email(user.getEmail()).
                name(user.getFirstName()).
                lastname(user.getLastName()).
                password(user.getPassword()).build();
        return userCreationDTO;
    }

    public static UserDTO mapModelToDto(UserModel user){
        UserDTO userDTO = UserDTO.builder().
                email(user.getEmail()).
                firstName(user.getFirstName()).
                lastName(user.getLastName()).
                creationDate(user.getCreationDate()).
                id(user.getId()).
                role(user.getRole()).
                photo(user.getPhoto()).build();
        return userDTO;
    }

    public static User mapModelToDomain(UserModel userModel){
        User userDomain = User.builder()
                .firstName(userModel.getFirstName())
                .lastName(userModel.getLastName())
                .email(userModel.getEmail())
                .password(userModel.getPassword())
                .photo(userModel.getPhoto())
                .role(RoleMapper.mapModelToDomain(userModel.getRole())).build();
        return userDomain;
    }

    public static UserModel mapDomainToModel(User userDomain){
        UserModel userModel = UserModel.builder()
                .firstName(userDomain.getFirstName())
                .lastName(userDomain.getLastName())
                .email(userDomain.getEmail())
                .password(userDomain.getPassword())
                .photo(userDomain.getPhoto())
                .role(RoleMapper.mapDomainToModel(userDomain.getRole())).build();
        return userModel;
    }
}
