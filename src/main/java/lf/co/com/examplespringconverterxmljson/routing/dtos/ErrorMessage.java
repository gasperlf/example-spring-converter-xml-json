package lf.co.com.examplespringconverterxmljson.routing.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {

    private String code;
    private String message;
}
