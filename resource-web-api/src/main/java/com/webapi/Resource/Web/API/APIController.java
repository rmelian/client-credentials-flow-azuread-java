package com.webapi.Resource.Web.API;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Raisel Melian raisel.melian@gmail.com
 */
@RestController("/api")
public class APIController {

    @GetMapping
    public String getName() {
        return "Hello World";
    }

}
