package org.sample.main.Model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Department {
    String depName;
    Long depId;
}
