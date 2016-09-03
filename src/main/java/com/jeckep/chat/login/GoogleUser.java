package com.jeckep.chat.login;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeckep.chat.user.IUser;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUser implements IUser {
    @JsonProperty("family_name") String surname;
    @JsonProperty("given_name") String name;
    String picture;
    String email;
}
