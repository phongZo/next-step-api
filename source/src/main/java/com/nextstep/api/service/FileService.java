package com.nextstep.api.service;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.UploadFileDto;
import com.nextstep.api.form.file.UploadFileForm;
import com.nextstep.api.model.Permission;
import com.nextstep.api.repository.CompanyRepository;
import com.nextstep.api.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FileService {

    static final String[] UPLOAD_TYPES = new String[]{"LOGO", "AVATAR", "IMAGE", "DOCUMENT", "CV"};
    static final String[] ALLOWED_EXTENSIONS = new String[]{"pdf", "doc", "docx"};

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserBaseOTPService OTPService;

    @Autowired
    CommonAsyncService commonAsyncService;
    
    @Autowired
    UserServiceImpl userService;

    @Autowired
    CompanyRepository companyRepository;

    /**
     * return file path
     *
     * @param uploadFileForm
     * @return
     */
    public ApiMessageDto<UploadFileDto> storeFile(UploadFileForm uploadFileForm) {
        // Normalize file name
        ApiMessageDto<UploadFileDto> apiMessageDto = new ApiMessageDto<>();
        try {
            boolean contains = Arrays.stream(UPLOAD_TYPES).anyMatch(uploadFileForm.getType()::equalsIgnoreCase);
            if (!contains) {
                apiMessageDto.setResult(false);
                apiMessageDto.setMessage("Type is required in AVATAR, LOGO, IMAGE, DOCUMENT or CV");
                return apiMessageDto;
            }

            String fileName = StringUtils.cleanPath(uploadFileForm.getFile().getOriginalFilename());
            String ext = FilenameUtils.getExtension(fileName).toLowerCase();

            if (uploadFileForm.getType().equals("CV")) {
                boolean isValidExt = Arrays.stream(ALLOWED_EXTENSIONS).anyMatch(ext::equals);
                if (!isValidExt) {
                    apiMessageDto.setResult(false);
                    apiMessageDto.setMessage("CV file must be PDF, DOC or DOCX");
                    return apiMessageDto;
                }
                
                if (!Objects.equals(userService.getAddInfoFromToken().getUserKind(), NextStepConstant.USER_KIND_CANDIDATE)) {
                    apiMessageDto.setResult(false);
                    apiMessageDto.setMessage("Only candidates can upload CV");
                    return apiMessageDto;
                }
            }

            String finalFile = uploadFileForm.getType() + "_" + RandomStringUtils.randomAlphanumeric(10) + "." + ext;
            String typeFolder = File.separator + uploadFileForm.getType();

            // Add company ID to path for LOGO type
            if (uploadFileForm.getType().equals("LOGO")) {
                Long companyId = userService.getAddInfoFromToken().getStoreId();
                if (companyId != null) {
                    typeFolder = "/" + companyId + typeFolder;
                }
            }

            if (uploadFileForm.getType().equals("CV")) {
                Long candidateId = userService.getAddInfoFromToken().getAccountId();
                typeFolder = typeFolder + File.separator + candidateId;
            }

            Path fileStorageLocation = Paths.get(uploadDir + typeFolder).toAbsolutePath().normalize();
            Files.createDirectories(fileStorageLocation);
            Path targetLocation = fileStorageLocation.resolve(finalFile);
            Files.copy(uploadFileForm.getFile().getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            UploadFileDto uploadFileDto = new UploadFileDto();
            uploadFileDto.setFilePath(typeFolder + File.separator + finalFile);
            apiMessageDto.setData(uploadFileDto);
            apiMessageDto.setMessage("Upload file success");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("" + e.getMessage());
        }

        return apiMessageDto;
    }

    public ApiMessageDto<UploadFileDto> storeCompanyFile(Long companyId, UploadFileForm uploadFileForm) {
        ApiMessageDto<UploadFileDto> apiMessageDto = new ApiMessageDto<>();

        try {
            if (!companyRepository.existsById(companyId)) {
                apiMessageDto.setResult(false);
                apiMessageDto.setMessage("Company ID does not exist");
                return apiMessageDto;
            }

            if (!Arrays.asList("LOGO", "IMAGE","AVATAR").contains(uploadFileForm.getType().toUpperCase())) {
                apiMessageDto.setResult(false);
                apiMessageDto.setMessage("Only LOGO or IMAGE or AVATAR can be uploaded for a company");
                return apiMessageDto;
            }

            String fileName = StringUtils.cleanPath(uploadFileForm.getFile().getOriginalFilename());
            String ext = FilenameUtils.getExtension(fileName).toLowerCase();

            String typeFolder = File.separator + uploadFileForm.getType() + File.separator + companyId;
            String finalFile = uploadFileForm.getType() + "_" + RandomStringUtils.randomAlphanumeric(10) + "." + ext;

            Path fileStorageLocation = Paths.get(uploadDir + typeFolder).toAbsolutePath().normalize();
            Files.createDirectories(fileStorageLocation);
            Path targetLocation = fileStorageLocation.resolve(finalFile);
            Files.copy(uploadFileForm.getFile().getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadFileDto uploadFileDto = new UploadFileDto();
            uploadFileDto.setFilePath(typeFolder + File.separator + finalFile);
            apiMessageDto.setData(uploadFileDto);
            apiMessageDto.setMessage("Upload file success");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("File upload failed: " + e.getMessage());
        }

        return apiMessageDto;
    }


    public void deleteFile(String filePath) {
        File file = new File(uploadDir + filePath);
//        file.deleteOnExit();
        if(file.exists()) file.delete();
    }

    public Resource loadFileAsResource(String folder, String fileName) {
        try {
            // Handle nested paths by combining folder and fileName
            String fullPath = folder;
            if (fileName != null && !fileName.isEmpty()) {
                fullPath = folder + File.separator + fileName;
            }
            
            Path fileStorageLocation = Paths.get(uploadDir + File.separator + fullPath).toAbsolutePath().normalize();
            Resource resource = new UrlResource(fileStorageLocation.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    public InputStreamResource loadFileAsResourceExt(String folder, String fileName) {

        try {
            File file = new File(uploadDir + File.separator + folder + File.separator + fileName);
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(file));
            if (inputStreamResource.exists()) {
                return inputStreamResource;
            }
        } catch (FileNotFoundException ex) {
            log.error(ex.getMessage(), ex);

        }
        return null;
    }

    public String getOTPForgetPassword(){
        return OTPService.generate(4);
    }

    public synchronized Long getOrderHash(){
        return Long.parseLong(OTPService.generate(9));
    }

    // create generated referral code
    public synchronized String getReferralCode(){
        return OTPService.generate(6);
    }

//    public void pushToFirebase(String url, String data){
//        commonAsyncService.pushToFirebase(url,data);
//    }

    public void sendEmail(String email, String msg, String subject, boolean html){
        commonAsyncService.sendEmail(email,msg,subject,html);
    }

    public String convertGroupToUri(List<Permission> permissions){
        if(permissions!=null){
            StringBuilder builderPermission = new StringBuilder();
            for(Permission p : permissions){
                builderPermission.append(p.getAction().trim().replace("/v1","")+",");
            }
            return  builderPermission.toString();
        }
        return null;
    }
}
