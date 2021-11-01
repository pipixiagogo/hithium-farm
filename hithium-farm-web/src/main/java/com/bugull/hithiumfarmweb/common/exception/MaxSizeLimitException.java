package com.bugull.hithiumfarmweb.common.exception;

import com.bugull.hithiumfarmweb.utils.ResHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class MaxSizeLimitException {
    private static  final Logger logger = LoggerFactory.getLogger(MaxSizeLimitException.class);
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResHelper<Void> handleSizeLimitException(MaxUploadSizeExceededException e){
        logger.error(e.getMessage(),e);
        return ResHelper.error("上传单张图片超过5MB限制/上传总图片超过25MB限制");
    }
}
