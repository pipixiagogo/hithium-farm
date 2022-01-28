package com.bugull.hithiumfarmweb.common;

import org.springframework.data.annotation.Id;

public  abstract class IncIdEntity<T> {
    @Id
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
