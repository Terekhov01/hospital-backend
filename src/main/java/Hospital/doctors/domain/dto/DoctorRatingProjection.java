package Hospital.doctors.domain.dto;

import lombok.Data;

public interface DoctorRatingProjection {

    Integer getId();
    String getFirstName();
    String getLastName();
    Double getRating();
}
