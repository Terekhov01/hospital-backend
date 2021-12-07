package Hospital.doctors.domain.dto;

import Hospital.doctors.domain.entity.Doctor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRatingDto {

    private String feedback;
    private Integer rating;
    private Integer doctorId;
    private Integer userId;
}
