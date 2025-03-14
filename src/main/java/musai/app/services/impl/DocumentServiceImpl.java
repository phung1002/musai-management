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
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import musai.app.DTO.response.DocumentResponseDTO;
import musai.app.exception.NotFoundException;
import musai.app.models.Document;
import musai.app.models.User;
import musai.app.repositories.DocumentRepository;
import musai.app.repositories.UserRepository;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.DocumentService;

@Service
public class DocumentServiceImpl implements DocumentService {

	@Value("${upload.dir}")
	private String uploadDir; // Path for saving uploaded files, injected from environment variable

	private final DocumentRepository documentRepository;
	private final UserRepository userRepository;

	public DocumentServiceImpl(DocumentRepository documentRepository, UserRepository userRepository) {
		super();
		this.documentRepository = documentRepository;
		this.userRepository = userRepository;
	}

	@Override
	public List<DocumentResponseDTO> listAllFiles() throws IOException {
		List<DocumentResponseDTO> response = new ArrayList<>();

		try {
			// Step into each child folder
			Files.walk(Paths.get(uploadDir)).filter(Files::isRegularFile) // Just take file
					.forEach(filePath -> {
						String fileName = filePath.getFileName().toString();

						Optional<Document> document = documentRepository.findByTitleAndDeletedAtIsNull(fileName);
						if (document.isPresent()) {
							DocumentResponseDTO documentDTO = new DocumentResponseDTO();
							documentDTO.setId(document.get().getId());
							documentDTO.setTitle(cleanFileName(fileName));
							documentDTO.setPath(filePath.toString());
							documentDTO.setUploadBy(document.get().getUploadBy().getFullName());
							documentDTO.setUploadAt(document.get().getUploadAt());
							response.add(documentDTO);
						}
					});
		} catch (IOException e) {
			throw new IOException("error_saving_file_to_server");
		}
		return response;
	}

	@Override
	public List<DocumentResponseDTO> listFilesForMember(UserDetailsImpl principal) {
		String uploadDirMember = uploadDir + principal.getId();
		File directory = new File(uploadDirMember);
		File[] files = directory.listFiles();
		List<DocumentResponseDTO> response = new ArrayList<>();

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) { // Only list files (not directories)
					// Get file name and infor with deleteAt is null
					Optional<Document> document = documentRepository.findByTitleAndDeletedAtIsNull(file.getName());
					if (document.isPresent()) {
						DocumentResponseDTO documentDTO = new DocumentResponseDTO();
						documentDTO.setId(document.get().getId());
						documentDTO.setTitle(cleanFileName(file.getName()));
						documentDTO.setPath(file.getPath());
						documentDTO.setUploadBy(document.get().getUploadBy().getFullName());
						documentDTO.setUploadAt(document.get().getUploadAt());
						response.add(documentDTO);
					}
				}
			}
		}
		return response;
	}

	private String cleanFileName(String fileName) {
		// remove timestamp
		fileName = fileName.replaceFirst("^\\d+_", "");
	
		//remove .pdf
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
		User user = userRepository.findByIdAndDeletedAtIsNull(principal.getId())
				.orElseThrow(() -> new NotFoundException("user_not_exist"));

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
		document.setPath("/uploads/" + principal.getId() + "/" + fileName);
		document.setUploadBy(user);
		document.setUploadAt(LocalDateTime.now());

		// Save the document metadata to the database
		return documentRepository.save(document);
	}

}
