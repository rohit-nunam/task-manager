package com.rohit.task_manager.dto.output;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private UUID id;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String timeZone;

}
