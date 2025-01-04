package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.ContestIdDTO;
import com.example.zero2dev.models.Certificate;
import com.example.zero2dev.responses.ErrorResponse;
import com.example.zero2dev.services.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping(value = "/generate",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateCertificate(@RequestBody ContestIdDTO certificate) {
        try {
            byte[] pdfBytes = certificateService.generateCertificate(certificate.getContestId());

//            Path path = Paths.get("C:\\Users\\ADMIN\\Zero2Dev\\zero2dev\\src\\main\\resources\\storage\\haidz.pdf");
//            Files.write(path, pdfBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "certificate.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse(500,e.getMessage(), new ArrayList<>());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

//    @PostMapping(value = "/generate-batch")
//    public ResponseEntity<List<byte[]>> generateBatchCertificates(@RequestBody List<Certificate> certificates) {
//        try {
//            List<byte[]> pdfList = new ArrayList<>();
//            for (Certificate cert : certificates) {
//                pdfList.add(certificateService.generateCertificate(cert));
//            }
//            return ResponseEntity.ok(pdfList);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}
