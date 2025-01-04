package com.example.zero2dev.services;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.models.Certificate;
import com.example.zero2dev.responses.CertificateResponse;
import com.example.zero2dev.storage.CongratulationsGenerator;
import com.example.zero2dev.storage.MESSAGE;
import com.lowagie.text.FontFactory;
import jakarta.annotation.PostConstruct;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class CertificateService {

    @Value("classpath:templates/certificate.jrxml")
    private Resource reportTemplate;

    @Value("classpath:banners/")
    private Resource bannerTemplate;
    @Value("${zero2dev.font_path}")
    private String fontPath;
    @Value("${zero2dev.banner_path}")
    private String bannerPath;

    @Value("${certificate.keystore.path}")
    private String keystorePath;

    @Value("${certificate.keystore.password}")
    private String keystorePassword;

    @Value("${certificate.key.alias}")
    private String keyAlias;

    @Value("${certificate.key.password}")
    private String keyPassword;
    @Autowired
    private SubmissionService submissionService;


    @PostConstruct
    public void init() {
        try {
            // Đăng ký Monotype Corsiva
            String corsivafontPath = "fonts/MTCORSVA.TTF";
            InputStream corsivais = getClass().getClassLoader().getResourceAsStream(corsivafontPath);
            if (corsivais != null) {
                FontFactory.register(corsivafontPath, "Monotype Corsiva");
                FontFactory.register("fonts/Monotype-Corsiva-Bold.ttf", "Monotype Corsiva Bold");
                FontFactory.register("fonts/Monotype-Corsiva-Regular-Italic.ttf", "Monotype Corsiva Italic");
                FontFactory.register("fonts/Monotype-Corsiva-Bold-Italic.ttf", "Monotype Corsiva Italic Bold");
            } else {
                throw new RuntimeException("Font file not found: " + corsivafontPath);
            }

            FontFactory.register("fonts/Arial.ttf", "Arial");
            FontFactory.register("fonts/ArialBD.ttf", "Arial Bold");
            FontFactory.register("fonts/ArialCEItalic.ttf", "Arial Italic");
            FontFactory.register("fonts/ArialCEBoldItalic.ttf", "Arial Bold Italic");
        } catch (Exception e) {
            throw new RuntimeException("Failed to register fonts", e);
        }
    }


    public byte[] generateCertificate(Long contestId) {
        CertificateResponse response = submissionService.getCertificateResponse(contestId);
        if (response==null){
            throw new ResourceNotFoundException(MESSAGE.FORBIDDEN_REQUEST);
        }
        try {
            JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).setProperty(
                    "net.sf.jasperreports.default.pdf.font.name", "Arial");
            JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).setProperty(
                    "net.sf.jasperreports.default.pdf.encoding", "Identity-H");
            JRPropertiesUtil.getInstance(DefaultJasperReportsContext.getInstance()).setProperty(
                    "net.sf.jasperreports.default.pdf.embedded", "true");

            InputStream templateInputStream = reportTemplate.getInputStream();
            JasperReport jasperReport = JasperCompileManager.compileReport(templateInputStream);
            System.out.println("OK QUA RESPONSE 1");
            String language = response.getLanguageName();
            System.out.println("OK QUA RESPONSE 2");
            String fullName = response.getFullName();
            System.out.println("OK QUA RESPONSE 3");
            String contestName = response.getContestTitle();
            System.out.println("OK QUA RESPONSE 4");
            Long ranking = response.getRankInContest();
            System.out.println("OK QUA RESPONSE 5");
            Long completedTasks = response.getTotalSolved();
            System.out.println("OK QUA RESPONSE 6");
            Long total = response.getTotal();
            if (!response.isCompletedAllProblems() || total==0 || completedTasks > total){
                System.out.println("Ném ra lỗi");
                throw new ResourceNotFoundException(MESSAGE.FORBIDDEN_REQUEST);
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("backgroundImage", getBanner(language));
            parameters.put("studentName", (normalizeVietnameseName(fullName)));
            parameters.put("contestName", contestName);
            parameters.put("completedTasks", completedTasks);
            parameters.put("ranking", ranking);
            System.out.println("FILL DATA");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = dateFormat.format(new Date());

            parameters.put("signatureName1", "Administrator");
            parameters.put("signatureName2", "Contest Manager");
            parameters.put("signatureDate1", currentDate);
            parameters.put("signatureDate2", currentDate);
            parameters.put("signer1Reason", "Certificate Issuance");
            parameters.put("signer2Reason", "Contest Verification");
            String congratulationsMessage = CongratulationsGenerator.generateCongratulations(
                    ranking,
                    contestName,
                    completedTasks
            );
            parameters.put("congratulationsMessage", congratulationsMessage);

            JRDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(new Object()));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            if (keystorePath != null && !keystorePath.isEmpty()) {
                return signPdf(pdfBytes, "Official Certificate");
            }

            return pdfBytes;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate", e);
        }
    }
    public static String normalizeVietnameseName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }

        fullName = fullName.replaceAll("[^\\p{L}\\s]", "").replaceAll("\\s+", " ").trim();

        fullName = Normalizer.normalize(fullName.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        fullName = pattern.matcher(fullName).replaceAll("");

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : fullName.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                c = Character.toUpperCase(c);
                capitalizeNext = false;
            }
            result.append(c);
        }

        return result.toString();
    }
    public String getBanner(String language) throws IOException {
        String bannerFileName = switch (language.toLowerCase()) {
            case "c" -> "c.png";
            case "java" -> "java.png";
            case "python" -> "python.png";
            default -> "cpp.png";
        };
        return bannerPath + File.separator + bannerFileName;
    }

    private byte[] signPdf(byte[] pdfBytes, String reason) throws Exception {
        try {
            PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));

            PDSignature signature = new PDSignature();
            signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
            signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
            signature.setName("Administrator");
            signature.setReason(reason);
            signature.setSignDate(Calendar.getInstance());

            X509Certificate certificate = loadCertificate();
            PrivateKey privateKey = loadPrivateKey();

            if (certificate == null || privateKey == null) {
                throw new RuntimeException("Certificate or private key not found");
            }

            SignatureInterface signatureInterface = content -> {
                try {
                    CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
                    generator.addSignerInfoGenerator(
                            new JcaSignerInfoGeneratorBuilder(
                                    new JcaDigestCalculatorProviderBuilder().build()
                            ).build(new JcaContentSignerBuilder("SHA256withRSA")
                                    .build(privateKey), certificate));

                    CMSTypedData msg = new CMSProcessableByteArray(content.readAllBytes());
                    CMSSignedData signedData = generator.generate(msg, true);

                    return signedData.getEncoded();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };

            document.addSignature(signature, signatureInterface);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign PDF", e);
        }
    }

    private X509Certificate loadCertificate() throws Exception {
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            Resource keystoreResource = new ClassPathResource(keystorePath.replace("classpath:", ""));
            try (InputStream is = keystoreResource.getInputStream()) {
                keystore.load(is, keystorePassword.toCharArray());
                return (X509Certificate) keystore.getCertificate(keyAlias);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {
        try {
            KeyStore keystore = KeyStore.getInstance("JKS");
            Resource keystoreResource = new ClassPathResource(keystorePath.replace("classpath:", ""));
            try (InputStream is = keystoreResource.getInputStream()) {
                keystore.load(is, keystorePassword.toCharArray());
                return (PrivateKey) keystore.getKey(keyAlias, keyPassword.toCharArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}