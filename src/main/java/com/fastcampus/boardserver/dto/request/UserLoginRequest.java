package com.fastcampus.boardserver.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Schema(description = "사용자 로그인 요청")
public class UserLoginRequest {

    @NonNull
    @Schema(description = "사용자 ID", example = "kimjhjjang")
    private String id;
    
    @NonNull
    @Schema(description = "비밀번호", example = "password123")
    private String password;
}
