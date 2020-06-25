package org.graal.failure.models;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Person extends BaseModel {

    private String name;
    private String lastname;

}
