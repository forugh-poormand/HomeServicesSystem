package ir.maktab127.homeservicessystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
public class ImageLengthOutOfBoundException extends RuntimeException {
    public ImageLengthOutOfBoundException(String message) {
        super(message);
    }
}