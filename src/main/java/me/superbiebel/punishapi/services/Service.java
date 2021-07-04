package me.superbiebel.punishapi.services;

import me.superbiebel.punishapi.SystemStatus;
import me.superbiebel.punishapi.exceptions.ShutDownException;
import me.superbiebel.punishapi.exceptions.StartupException;
import org.jetbrains.annotations.NotNull;

public interface Service<T> {
    void startup(boolean force) throws StartupException;
    void shutdown() throws ShutDownException;
    //Will shut down without throwing any exception, force closing and killing anything that is in its way.
    void kill();
    @NotNull
    SystemStatus status();
    T type();
    
}
