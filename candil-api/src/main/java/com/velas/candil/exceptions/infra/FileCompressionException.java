package com.velas.candil.exceptions.infra;

import com.velas.candil.exceptions.BusinessErrorCode;
import com.velas.candil.exceptions.BusinessException;

public class FileCompressionException extends BusinessException {
    public FileCompressionException(String message) {
        super(BusinessErrorCode.FILE_COMPRESSION_ERROR,message);
    }
}


