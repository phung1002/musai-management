package musai.app.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import musai.app.DTO.response.DocumentResponseDTO;
import musai.app.exception.NotFoundException;
import musai.app.models.Document;
import musai.app.models.Employee;
import musai.app.repositories.DocumentRepository;
import musai.app.repositories.EmployeeRepository;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.DocumentService;

@Service
public class DocumentServiceImpl implements DocumentService {

	@Value("${upload.dir}")
	private String uploadDir; // Path for saving uploaded files, injected from environment variable

	private final DocumentRepository documentRepository;
	private final EmployeeRepository employeeRepository;

	public DocumentServiceImpl(DocumentRepository documentRepository, EmployeeRepository employeeRepository) {
		super();
		this.documentRepository = documentRepository;
		this.employeeRepository = employeeRepository;
	}

	@Override
	public List<DocumentResponseDTO> listAllFiles() {
		
		return processFilesAndSyncDB(null);
		
	}

	@Override
	public List<DocumentResponseDTO> listFilesForMember(UserDetailsImpl principal) {

		return processFilesAndSyncDB(principal.getId());
	}

	private List<DocumentResponseDTO> processFilesAndSyncDB(Long loginId) {
		List<DocumentResponseDTO> response = new ArrayList<>();

		List<Document> documents;

		// Get list from DB
		if (loginId != null) {
			// Get document of employee, if have employee id
			documents = documentRepository.findByUploadById(loginId);
		} else {
			// Get all, if don't have employee id
			documents = documentRepository.findAllActive();
		}

		for (Document document : documents) {
			Path filePath = Paths.get(uploadDir + document.getPath());
			if (Files.exists(filePath)) {
				// If exist, add to response
				response.add(mapToDTO(document, filePath.toFile()));
			} else {
				// If doesn't exist, update deletedAt
				if (document.getDeletedAt() == null) {
					document.setDeletedAt(LocalDateTime.now());
					documentRepository.save(document);
				}
			}
		}

		return response;
	}

	private DocumentResponseDTO mapToDTO(Document document, File file) {
		return new DocumentResponseDTO(document.getId(), cleanFileName(file.getName()), file.getPath(),
				document.getUploadBy().getId(), document.getUploadBy().getFullName(), document.getUploadAt());
	}

	private String cleanFileName(String fileName) {
		// remove timestamp
		fileName = fileName.replaceFirst("^\\d+_", "");
		// remove .pdf
		if (fileName.endsWith(".pdf")) {
			fileName = fileName.substring(0, fileName.length() - 4);
		}
		return fileName;
	}

	@Override
	public Document uploadFile(MultipartFile file, UserDetailsImpl principal) throws IOException {
		// Check the content type of the file, handle potential null values
		String contentType = file.getContentType();
		if (contentType == null || !contentType.equals("application/pdf")) {
			throw new IllegalArgumentException("only_pdf_allowed");
		}
		Employee employee = employeeRepository.findById(principal.getId())
				.orElseThrow(() -> new NotFoundException("employee_not_exist"));

		// Check if the file size exceeds the maximum limit of 5MB
		final long MAX_SIZE = 5 * 1024 * 1024; // 5MB size limit
		if (file.getSize() > MAX_SIZE) {
			throw new IllegalArgumentException("file_size_exceeds_5MB_limit");
		}

		String uploadDirMember = uploadDir + principal.getId();
		// Ensure the upload directory exists, create it if not
		Path uploadPath = Paths.get(uploadDirMember);
		Files.createDirectories(uploadPath);

		// Generate a unique filename using the current timestamp and original file name
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		Path filePath = Paths.get(uploadDirMember, fileName);

		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		// Create a new document entity to store file metadata in the database
		Document document = new Document();
		document.setTitle(fileName);
		document.setPath("/" + principal.getId() + "/" + fileName);
		document.setUploadBy(employee);
		document.setUploadAt(LocalDateTime.now());

		return documentRepository.save(document);
	}

	@Override
	public void deleteDocument(Long documentId, Long employeeId) throws Exception {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new NotFoundException("file_not_found"));

		if (!document.getUploadBy().getId().equals(employeeId)) {
			throw new AccessDeniedException("not_authorized_to_delete");
		}

		Path filePath = Paths.get(uploadDir, document.getPath());
		File file = filePath.toFile();

		if (file.exists() && file.isFile()) {
			if (!file.delete()) {
				throw new IOException("file_delete_failed");
			}
		} else {
			throw new NotFoundException("file_not_found_in_directory");
		}

		document.setDeletedAt(LocalDateTime.now());
		documentRepository.save(document);
	}

	@Override
	public Resource previewDocument(Long documentId) throws Exception {
		Document document = documentRepository.findById(documentId)
				.orElseThrow(() -> new NotFoundException("file_not_found"));

		String filePath = uploadDir + document.getPath();

		File file = new File(filePath);
		if (!file.exists()) {
			throw new NotFoundException("file_not_found_in_directory");
		}
		Path path = Paths.get(filePath);
		return new FileSystemResource(path);
	}
}
