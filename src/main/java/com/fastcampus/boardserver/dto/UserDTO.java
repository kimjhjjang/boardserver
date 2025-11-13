package com.fastcampus.boardserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class UserDTO {
    public enum Status {
        DEFAULT, ADMIN, DELETED
    }

    @JsonIgnore
    @Schema(description = "고유 ID (PK)", example = "1", hidden = true)
    private Integer id;

    @Schema(description = "로그인에 사용하는 사용자 아이디", example = "kimjhjjang")
    private String userId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "비밀번호 (암호화 전 예시)", example = "password123")
    private String password;

    @Schema(description = "닉네임", example = "김철수")
    private String nickName;

    @Schema(description = "관리자 여부", example = "false", hidden = true)
    private boolean isAdmin = false;

    @Schema(description = "생성 시각 (ISO 8601)", example = "2025-01-01T12:00:00Z", hidden = true)
    private Date createTime;

    @Schema(description = "탈퇴 여부", example = "false")
    private boolean isWithDraw = false;

    @Schema(description = "회원 상태", example = "DEFAULT")
    private Status status = Status.DEFAULT;

    @Schema(description = "수정 시각 (ISO 8601)", example = "2025-01-02T12:00:00Z", hidden = true)
    private Date updateTime;

    public UserDTO(){
    }

    public UserDTO(String id, String password, String name, String phone, String address, Status status, Date createTime, Date updateTime, boolean isAdmin) {
        this.userId = id;
        this.password = password;
        this.nickName = name;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isAdmin = isAdmin;
    }

    public static boolean hasNullDataBeforeSignup(UserDTO userDTO) {
        return userDTO.getUserId() == null || userDTO.getPassword() == null
                || userDTO.getNickName() == null;
    }
}
