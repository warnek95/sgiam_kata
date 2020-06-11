package com.carbonit.sgiam.kata.exceptions;

public class ParameterNotValidException extends RuntimeException {

    public enum ErrorMessage {
        INVALID_UUID("The value %s is not a valid uuid."),
        NAME_CONTAINS_ADMIN ("The value %s is not a valid user_name, the name is expected to not contain admin."),
        UNVALID ("Please verify the data validity."),
        ;

        private final String messageTemplate;

        ErrorMessage(String messageTemplate) {
            this.messageTemplate = messageTemplate;
        }

        public String getMessageTemplate() {
            return messageTemplate;
        }
    }

    public ParameterNotValidException(final ErrorMessage errorMessage, final String value) {
        super(String.format(errorMessage.getMessageTemplate(), value));
    }

}
