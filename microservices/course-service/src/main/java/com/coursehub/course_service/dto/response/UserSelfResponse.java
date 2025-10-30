package com.coursehub.course_service.dto.response;


import java.io.Serializable;

public record UserSelfResponse(
        String username,
        String email
)implements Serializable {
}
