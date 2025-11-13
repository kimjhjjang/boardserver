package com.fastcampus.boardserver.service.impl;

import com.fastcampus.boardserver.dto.UserDTO;
import com.fastcampus.boardserver.exception.DuplicateIdException;
import com.fastcampus.boardserver.mapper.UserProfileMapper;
import com.fastcampus.boardserver.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.fastcampus.boardserver.utils.SHA256Util.encryptSHA256;

@Service
@Log4j2
public class UserServiceImpl implements UserService {

    @Autowired private UserProfileMapper userMapper;

    @Override
    @Transactional
    public void register(UserDTO userDto) {

        boolean isDuplicate = isDuplicateId(userDto.getUserId());
        if (isDuplicate) {
            throw new DuplicateIdException("Duplicate ID");
        }

        userDto.setCreateTime(new Date());
        userDto.setPassword(encryptSHA256(userDto.getPassword()));

        int insertCount = userMapper.register(userDto);

        if (insertCount != 1) {
            log.error("User registration failed for ID: {}", userDto.getId());
            throw new RuntimeException("User registration failed");
        }

        log.info("User registered successfully with ID: {}", userDto.getId());

    }

    @Override
    public UserDTO login(String id, String password) {
        String cryptedPassword = encryptSHA256(password);
        return userMapper.findByIdAndPassword(id, cryptedPassword);
    }

    @Override
    public boolean isDuplicateId(String id) {
        return userMapper.idCheck(id) == 1;
    }

    @Override
    public UserDTO getUserInfo(String userId) {
        return userMapper.getUserProfile(userId);
    }

    @Override
    public void updatePassword(String id, String beforePassword, String afterPassword) {
        String cryptedBeforePassword = encryptSHA256(beforePassword);
        UserDTO userDto = userMapper.findByIdAndPassword(id, cryptedBeforePassword);
        if (userDto != null) {
            userDto.setPassword(encryptSHA256(afterPassword));
            int updateCount = userMapper.updatePassword(userDto);
        } else {
            log.error("Password update failed for ID: {}", id);
            throw new RuntimeException("Password update failed");
        }
    }

    @Override
    public void deleteId(String id, String password) {
        String cryptedPassword = encryptSHA256(password);
        UserDTO userDto = userMapper.findByIdAndPassword(id, cryptedPassword);
        if (userDto != null) {
            int deleteCount = userMapper.deleteUserProfile(id);
            if (deleteCount != 1) {
                log.error("User deletion failed for ID: {}", id);
                throw new RuntimeException("User deletion failed");
            }
        } else {
            log.error("User deletion failed for ID: {}", id);
            throw new RuntimeException("User deletion failed");
        }

    }
}
