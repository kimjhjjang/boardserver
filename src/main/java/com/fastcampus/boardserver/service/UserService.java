package com.fastcampus.boardserver.service;

import com.fastcampus.boardserver.dto.UserDTO;
import org.springframework.stereotype.Service;


@Service
public interface UserService {

    void register(UserDTO userDto);

    UserDTO login(String id, String password);

    boolean isDuplicateId(String id);

    UserDTO getUserInfo(String userId);

    void updatePassword(String id, String beforePassword, String afterPassword);

    void deleteId(String id, String password);
}
