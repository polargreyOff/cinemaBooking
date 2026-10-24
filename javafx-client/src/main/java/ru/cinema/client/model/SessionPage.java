package ru.cinema.client.model;

import java.util.List;
public record SessionPage(List<Session> content, int page, int size, long totalElements, int totalPages) {}
