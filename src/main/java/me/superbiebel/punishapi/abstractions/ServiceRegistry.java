package me.superbiebel.punishapi.abstractions;

import java.util.concurrent.ConcurrentMap;
import me.superbiebel.punishapi.exceptions.ServiceAlreadyRegisteredException;
import me.superbiebel.punishapi.exceptions.ServiceNotFoundException;
import me.superbiebel.punishapi.exceptions.ShutDownException;
import me.superbiebel.punishapi.exceptions.StartupException;

public abstract class ServiceRegistry<T> extends System {

    protected final ConcurrentMap<T, Service<T>> serviceRegistryMap;

    protected ServiceRegistry(ConcurrentMap<T, Service<T>> serviceRegistryMap) {
        this.serviceRegistryMap = serviceRegistryMap;
    }

    @Override
    protected void onStartup(boolean force) throws StartupException {
        onServiceRegistryStartup(force);
    }

    @Override
    protected void onShutdown() throws ShutDownException {
        onServiceRegistryShutdown();
    }

    @Override
    protected void onKill() throws ShutDownException {
        onServiceRegistryKill();
    }

    protected abstract void onServiceRegistryStartup(boolean force) throws StartupException;

    protected abstract void onServiceRegistryShutdown() throws ShutDownException;

    protected abstract void onServiceRegistryKill() throws ShutDownException;

    public void addService(T serviceType, Service<T> service) throws StartupException, ServiceAlreadyRegisteredException {
        if (serviceRegistryMap.containsKey(serviceType)) {
            throw new ServiceAlreadyRegisteredException("Service was already registered");
        }
        serviceRegistryMap.put(serviceType, service);
        service.serviceStartup(false);
    }

    public Service<T> getService(T serviceType) throws ServiceNotFoundException {
        Service<T> returnedService = serviceRegistryMap.get(serviceType);
        if (returnedService == null) {
            throw new ServiceNotFoundException();
        }
        return returnedService;
    }

    public Service<T> removeService(T serviceType, boolean kill) throws ShutDownException, ServiceNotFoundException {
        if (!serviceRegistryMap.containsKey(serviceType)) {
            throw new ServiceNotFoundException("Servicetype not found");
        }
        Service<T> service = serviceRegistryMap.remove(serviceType);
        if (kill) {
            service.serviceKill();
        } else {
            service.serviceShutdown();
        }
        return service;
    }

    public void emptyServiceRegistry(boolean kill) throws ShutDownException, ServiceNotFoundException {
        for (T serviceType : serviceRegistryMap.keySet()) {
            this.removeService(serviceType, kill);
        }
    }
}
