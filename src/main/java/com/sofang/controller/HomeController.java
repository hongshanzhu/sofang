package com.sofang.controller;

import com.sofang.base.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by gegf
 */

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("name", "SoFang");
        return "index";
    }

    @ResponseBody
    @GetMapping("/get")
    public ResponseEntity get(){
        return ResponseEntity.createBySuccessMessage("Success!");
    }
}
