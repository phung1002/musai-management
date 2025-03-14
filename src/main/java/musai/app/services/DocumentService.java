package musai.app.services;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import musai.app.DTO.response.DocumentResponseDTO;
import musai.app.models.Document;
import musai.app.security.services.UserDetailsImpl;

public interface DocumentService {

	List<DocumentResponseDTO> listAllFiles() throws IOException;
	
	List<DocumentResponseDTO> listFilesForMember(UserDetailsImpl principal);
	
	Document uploadFile(MultipartFile file, UserDetailsImpl principal) throws IOException;

}
