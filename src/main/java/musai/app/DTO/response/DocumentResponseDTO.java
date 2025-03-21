package musai.app.DTO.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentResponseDTO {
	private Long id;
	private String title;
	private String path;
	private String uploadBy;
	private LocalDateTime uploadAt;
}
