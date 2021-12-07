package Hospital.doctors.domain.dto;

import Hospital.doctors.domain.entity.Specialist;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    String education;
    Integer room;
    List<Specialist> specialist;
}
