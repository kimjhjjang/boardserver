package com.fastcampus.boardserver.controller;

import com.fastcampus.boardserver.aop.LoginCheck;
import com.fastcampus.boardserver.dto.request.UserUpdatePasswordRequest;
import com.fastcampus.boardserver.dto.response.LoginResponse;
import com.fastcampus.boardserver.dto.UserDTO;
import com.fastcampus.boardserver.dto.request.UserLoginRequest;
import com.fastcampus.boardserver.dto.response.UserInfoResponse;
import com.fastcampus.boardserver.service.impl.UserServiceImpl;
import com.fastcampus.boardserver.utils.SessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@Log4j2
@Tag(name = "사용자 관리", description = "사용자 회원가입, 로그인, 정보 조회 API")
public class UserController {

    private final UserServiceImpl userService;
    private static final ResponseEntity<LoginResponse> FAIL_RESPONSE = new ResponseEntity<LoginResponse>(HttpStatus.BAD_REQUEST);

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public void signUp(@Parameter(description = "회원가입 정보") @RequestBody UserDTO userDto) {
        if (UserDTO.hasNullDataBeforeSignup(userDto)) {
            throw new IllegalArgumentException("Missing required user data");
        }

        userService.register(userDto);
    }

    @PostMapping("signin")
    @Operation(summary = "로그인", description = "사용자 로그인을 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "로그인 실패")
    })
    public HttpStatus login(@Parameter(description = "로그인 정보") @RequestBody UserLoginRequest userLoginRequest,
                            HttpSession session) {
        ResponseEntity<LoginResponse> responseEntity = null;
        LoginResponse loginResponse;
        UserDTO userDto = userService.login(userLoginRequest.getId(), userLoginRequest.getPassword());
        if(userDto != null) {
            session.setAttribute("user", userDto);
            loginResponse = LoginResponse.success(userDto);
            if(userDto.isAdmin()) {
                SessionUtil.setLoginAdminId(session, userDto.getUserId());
            } else {
                SessionUtil.setLoginMemberId(session, userDto.getUserId());
            }

            responseEntity = new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK);
        } else if (userDto == null) {
            return  HttpStatus.NOT_FOUND;
        } else {
            throw new RuntimeException("Login failed");
        }

        return HttpStatus.OK;
    }

    @PutMapping("logout")
    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public void logout(HttpSession session) {
        SessionUtil.clear(session);
    }

    @GetMapping("my-info")
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    public ResponseEntity<UserInfoResponse> memberInfo(HttpSession session) {
        String id = SessionUtil.getLoginMemberId(session);
        if(id == null) id = SessionUtil.getLoginAdminId(session);

        if(id == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserDTO memberInfo = userService.getUserInfo(id);
        log.info("id={}, memberInfo: {}",id, memberInfo);
        if (memberInfo == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(new UserInfoResponse(memberInfo));
    }

    @PatchMapping("password")
    @LoginCheck(type = LoginCheck.UserType.USER)
    public ResponseEntity<LoginResponse> updateUserPassword(String accountId, @RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest,
                                                            HttpSession session) {
        ResponseEntity<LoginResponse> responseEntity = null;
        String Id = accountId;
        String beforePassword = userUpdatePasswordRequest.getBeforePassword();
        String afterPassword = userUpdatePasswordRequest.getAfterPassword();

        try {
            userService.updatePassword(Id, beforePassword, afterPassword);
            UserDTO userInfo = userService.login(Id, afterPassword);
            LoginResponse loginResponse = LoginResponse.success(userInfo);
            ResponseEntity.ok(new ResponseEntity<LoginResponse>(loginResponse, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            log.error("updatePassword 실패", e);
            responseEntity = FAIL_RESPONSE;
        }
        return responseEntity;
    }
}
