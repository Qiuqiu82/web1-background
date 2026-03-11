package com.controller;

import com.annotation.IgnoreAuth;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.entity.ConfigEntity;
import com.entity.EIException;
import com.service.ConfigService;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("file")
@SuppressWarnings({"unchecked", "rawtypes"})
public class FileController {

    @Autowired
    private ConfigService configService;

    @RequestMapping("/upload")
    @IgnoreAuth
    public R upload(@RequestParam("file") MultipartFile file, String type) throws Exception {
        if (file.isEmpty()) {
            throw new EIException("\u4e0a\u4f20\u6587\u4ef6\u4e0d\u80fd\u4e3a\u7a7a");
        }
        String originalName = file.getOriginalFilename();
        String fileExt = originalName.substring(originalName.lastIndexOf('.') + 1);
        String fileName = new Date().getTime() + "." + fileExt;
        File dest = new File(getUploadDir(), fileName);
        file.transferTo(dest);

        if (StringUtils.isNotBlank(type) && "1".equals(type)) {
            ConfigEntity configEntity = configService.selectOne(new EntityWrapper<ConfigEntity>().eq("name", "faceFile"));
            if (configEntity == null) {
                configEntity = new ConfigEntity();
                configEntity.setName("faceFile");
            }
            configEntity.setValue(fileName);
            configService.insertOrUpdate(configEntity);
        }
        return R.ok().put("file", fileName);
    }

    @IgnoreAuth
    @RequestMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String fileName) {
        try {
            File file = resolveUploadFile(fileName);
            if (file != null && file.exists()) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", fileName);
                return new ResponseEntity<>(org.apache.commons.io.FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private File getUploadDir() {
        File upload = new File(System.getProperty("user.dir"), "upload");
        if (!upload.exists()) {
            upload.mkdirs();
        }
        return upload;
    }

    private File resolveUploadFile(String fileName) throws IOException {
        File runtimeFile = new File(getUploadDir(), fileName);
        if (runtimeFile.exists()) {
            return runtimeFile;
        }

        File classpathStatic = new File(ResourceUtils.getURL("classpath:static").getPath());
        if (classpathStatic.exists()) {
            File legacyFile = new File(new File(classpathStatic, "upload"), fileName);
            if (legacyFile.exists()) {
                return legacyFile;
            }
        }
        return null;
    }
}