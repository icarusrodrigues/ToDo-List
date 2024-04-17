package com.challenge.nuven.todolist.model;

public enum EnumMessage {
    GET_MESSAGE {
        @Override
        public String message() {
            return "Data recovered successfully!";
        }
    },
    POST_MESSAGE {
        @Override
        public String message() {
            return "Data saved successfully!";
        }
    },
    PUT_MESSAGE {
        @Override
        public String message() {
            return "Data updated successfully!";
        }
    },
    DELETE_MESSAGE {
        @Override
        public String message() {
            return "Data deleted successfully!";
        }
    },
    ENTITY_NOT_FOUND_MESSAGE {
        @Override
        public String message() {
            return "Entity not found!";
        }
    },
    CONSTRAINT_VIOLATION_MESSAGE {
        @Override
        public String message() {
            return "Constraint violated!";
        }
    },
    PROPERTY_NOT_FOUND_MESSAGE {
        @Override
        public String message() {
            return "Property not found in entity!";
        }
    };


    public abstract String message();
}
