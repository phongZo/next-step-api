package com.nextstep.api.service;

import com.nextstep.api.constant.NextStepConstant;
import com.nextstep.api.dto.ApiMessageDto;
import com.nextstep.api.dto.UploadFileDto;
import com.nextstep.api.form.file.UploadFileForm;
import com.nextstep.api.model.Permission;
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
            }

            //upload to uploadFolder/TYPE/id
            String finalFile = uploadFileForm.getType() + "_" + RandomStringUtils.randomAlphanumeric(10) + "." + ext;
            String typeFolder = File.separator + uploadFileForm.getType();

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

    public void deleteFile(String filePath) {
        File file = new File(uploadDir + filePath);
//        file.deleteOnExit();
        if(file.exists()) file.delete();
    }

    public Resource loadFileAsResource(String folder, String fileName) {

        try {
            Path fileStorageLocation = Paths.get(uploadDir + File.separator + folder).toAbsolutePath().normalize();
            Path fP = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(fP.toUri());
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
