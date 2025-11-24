package com.coursehub.course_service.dto.response;


import java.io.Serializable;

public record UserResponse(
        String username,
        String email
)implements Serializable {
}
