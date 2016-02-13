package com.ignacioavila.chuckreligion;

import java.util.List;

public class JokesResponse {

    String type;
    List<Joke> value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Joke> getValue() {
        return value;
    }

    public void setValue(List<Joke> value) {
        this.value = value;
    }
}
