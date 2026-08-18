package com.rev.g3.i2.ers2.controller;

import io.javalin.http.Context;

public interface UserHandler {
    public void login(Context ctx);
    public void register(Context ctx);
    public void logout(Context ctx);
}
