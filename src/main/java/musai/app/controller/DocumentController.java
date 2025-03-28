package musai.app.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import musai.app.DTO.response.DocumentResponseDTO;
import musai.app.DTO.response.MessageResponse;
import musai.app.exception.NotFoundException;
import musai.app.models.Document;
import musai.app.security.services.UserDetailsImpl;
import musai.app.services.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

	private final DocumentService documentService;

	// Constructor for dependency injection
	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	// API list all document
	@GetMapping("/all")
	public ResponseEntity<Object> listFiles() {
		try {
			List<DocumentResponseDTO> files = documentService.listAllFiles();
			return ResponseEntity.ok(files);
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("file_upload_failed");
		}
	}

	// API Get document of member
	@GetMapping
	public ResponseEntity<Object> listFiles(@AuthenticationPrincipal UserDetailsImpl principal) {
		try {
			List<DocumentResponseDTO> files;
			files = documentService.listFilesForMember(principal);
			return ResponseEntity.ok(files);
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body("file_upload_failed");
		}
	}

	// API endpoint to upload a document
	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
			@AuthenticationPrincipal UserDetailsImpl principal) {
		try {
			Document document = documentService.uploadFile(file, principal);
			return ResponseEntity.ok(document);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body(new MessageResponse("file_upload_failed"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new MessageResponse(e.getMessage()));
		}
	}

	// API delete
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDocument(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl principal) {
		try {
			documentService.deleteDocument(id, principal.getId());
			return ResponseEntity.ok(new MessageResponse("file_deleted_successfully"));
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
		} catch (AccessDeniedException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(e.getMessage()));
		} catch (IOException e) {
			return ResponseEntity.internalServerError().body(new MessageResponse("error_deleting_file"));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new MessageResponse("unexpected_error"));
		}
	}

	// API preview
	@GetMapping("/preview/{documentId}")
	public ResponseEntity<?> previewDocument(@PathVariable Long documentId) {
		try {
			Resource file = documentService.previewDocument(documentId);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(file);
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new MessageResponse("error_previewing_file"));
		}
	}

}
