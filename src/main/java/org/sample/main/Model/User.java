package org.sample.main.Model;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    String name;
    Long id;
    Department department;
}
