package estagio.opus.beanbalance.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super("%s not found with identifier: %s".formatted(resource, identifier));
    }
}
