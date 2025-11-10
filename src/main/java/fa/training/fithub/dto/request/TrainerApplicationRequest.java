package fa.training.fithub.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerApplicationRequest {

    @NotBlank(message = "Qualifications không được để trống")
    @Size(min = 10, max = 5000, message = "Qualifications phải từ 10 đến 5000 ký tự")
    private String qualifications;

    @Size(max = 5000, message = "Experience details không được vượt quá 5000 ký tự")
    private String experienceDetails;

    // document_urls: { "certificates": "url1,url2,url3" }
    private Map<String, Object> documentUrls;

}
