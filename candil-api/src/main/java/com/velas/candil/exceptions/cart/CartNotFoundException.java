package com.velas.candil.exceptions.cart;

import com.velas.candil.exceptions.BusinessErrorCode;
import com.velas.candil.exceptions.BusinessException;

public class CartNotFoundException extends BusinessException {

    public CartNotFoundException(String message) {
        super(BusinessErrorCode.CART_NOT_FOUND, message);
    }
}
