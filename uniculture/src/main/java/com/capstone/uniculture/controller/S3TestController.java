package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.S3UploadUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name="(미완-사용X)이미지", description = "이미지 서버 관련 API 입니다")
@RequiredArgsConstructor
@RequestMapping(value = "aws-s3")
@RestController
public class S3TestController {

    private final S3UploadUtil s3UploadUtil;

    @PostMapping(name = "S3 파일 업로드", value = "/file")
    public String fileUpload(@RequestParam("files") MultipartFile multipartFile) throws IOException {
        s3UploadUtil.upload(multipartFile, "test"); // test 폴더에 파일 생성
        return "success";
    }

    @DeleteMapping(name = "S3 파일 삭제", value = "/file")
    public String fileDelete(@RequestParam("path") String path) {
        s3UploadUtil.delete(path);
        return "success";
    }

}