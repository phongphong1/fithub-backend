package fa.training.fithub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponseDTO {
    private String access_token;
    private String refresh_token;
    private long expires_in; // giây cho access token
}


