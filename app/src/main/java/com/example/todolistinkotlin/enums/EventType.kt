package com.example.todolistinkotlin.enums

enum class EventType(val value: String) {
    ADD_TODO("add_todo"),
    DELETE_TODO("delete_todo"),
    EDIT_TODO("edit_todo"),
    ADD_TODO_FIELD_ERROR("add_todo_field_error"),
    SESSION_TIMESTAMP("session_timestamp")
}