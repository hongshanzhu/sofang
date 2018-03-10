package com.sofang.controller.admin;

import com.sofang.base.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

/**
 * 后台管理
 *
 * @since 1.0
 *
 * @version 1.0
 *
 * @author gegf
 */
@Controller
public class AdminController {

    @GetMapping("/admin/center")
    public String adminCenterPage(){
        return "admin/center";
    }

    @GetMapping("/admin/welcome")
    public String welcomePage(){
        return "admin/welcome";
    }

    @GetMapping("/admin/login")
    public String login(){
        return "admin/login";
    }

    @GetMapping("/admin/add/house")
    public String addHousePage(){
        return "admin/house-add";
    }

    @ResponseBody
    @PostMapping(value="/admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadPhoto(@RequestParam("file")MultipartFile file){
        return ResponseEntity.createBySuccessMessage("");
    }
}
