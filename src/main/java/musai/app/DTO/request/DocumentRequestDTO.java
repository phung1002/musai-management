package musai.app.DTO.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class DocumentRequestDTO {
	
	private String title;
	
	private MultipartFile file;
}
