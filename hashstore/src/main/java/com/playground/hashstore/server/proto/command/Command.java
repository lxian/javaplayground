package com.playground.hashstore.server.proto.command;

public interface Command {
    byte getOp();

    short id();
}
