package org.cttelsamicsterrassa.data.api.rest.practicioner;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.cttelsamicsterrassa.data.api.rest.ControllerConfig.API_BASE_PATH_V1;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.PARAMETER})
@RestController
@RequestMapping(API_BASE_PATH_V1 + "/practicioner")
@Tag(name = "Practicioner API", description = "Endpoints for searching practicioners")
public @interface PracticionerOpenAPIv1Controller {
}

