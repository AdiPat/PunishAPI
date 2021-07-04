package me.superbiebel.punishapi.data.services;

import me.superbiebel.punishapi.exceptions.FailedServiceOperationException;
import me.superbiebel.punishapi.services.Service;

public interface TestService extends Service<String> {
    
    String exampleOperation(String returnString) throws FailedServiceOperationException;
}
