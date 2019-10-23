package com.sofang.base.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 错误异常拦截器
 *
 * @since 1.0
 *
 * @version 1.0
 *
 * @author gegf
 */
@Controller
public class AppErrorController implements ErrorController{

    private static final String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    private ErrorAttributes errorAttributes;

    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes){
        this.errorAttributes = errorAttributes;
    }


    /**
     * 错误返回json
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = ERROR_PATH)
    public ResponseEntity errorResponseHandler(HttpServletRequest request){
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);

        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(requestAttributes, false);
        Integer status = getStatus(request);
        return ResponseEntity.createByErrorCodeMessage(status,
            String.valueOf(attr.getOrDefault("message", "error")));
    }

    private int getStatus(HttpServletRequest request){
        Integer status = (Integer)request.getAttribute("javax.servlet.error.status_code");
        return status != null ? status : 500;
    }

}
