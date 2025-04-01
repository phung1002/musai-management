package musai.app.services;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import musai.app.DTO.response.DocumentResponseDTO;
import musai.app.models.Document;
import musai.app.security.services.UserDetailsImpl;

public interface DocumentService {

	List<DocumentResponseDTO> listAllFiles() throws IOException;
	
	List<DocumentResponseDTO> listFilesForMember(UserDetailsImpl principal) throws IOException;
	
	Document uploadFile(MultipartFile file, UserDetailsImpl principal) throws IOException;

	void deleteDocument(Long id, Long deletedBy) throws IOException;

	Resource previewDocument(Long documentId) throws IOException;

}
