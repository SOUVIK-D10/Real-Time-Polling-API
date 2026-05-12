# Real-Time-Polling-API

A REST API for polls with real-time vote updates using Server-Sent Events (SSE).

## Features

- Real-time poll vote updates via SSE
- JWT authentication with refresh tokens
- Poll creation, voting, and result retrieval
- Poll visibility policies: public, post-expiry, owner-only
- Docker-ready deployment

## Technology Stack

- Java 21
- Spring Boot 4.0.6
- Spring Security + JWT
- Spring Data JPA / Hibernate
- PostgreSQL
- Server-Sent Events (SSE) through Spring
- Docker / Docker Compose

## Endpoint Overview

### Authentication
- `POST /api/users/register` — create a new user
- `POST /api/users/login` — authenticate and get access/refresh tokens
- `POST /api/users/refresh` — refresh an expired access token

### Polls
- `POST /api/polls/create` — create a poll with options
- `GET /api/polls/{pollId}` — fetch poll details by ID
- `GET /api/polls` — list all polls
- `PUT /api/polls/{pollId}` — update an existing poll
- `DELETE /api/polls/{pollId}` — delete a poll

### Voting
- `POST /api/polls/{pollId}/vote` — cast a vote
- `GET /api/polls/{pollId}/results` — view poll results
- `GET /api/polls/{pollId}/stream` — subscribe to live vote events

## Docker Usage

Run the service with Docker Compose. Provide the required values in a local `.env` file so Docker can start the app and database together.

## Summary

This project lets users register, authenticate, create polls, vote, and receive live updates. The backend is secured with JWT and stores poll data in PostgreSQL. Docker makes it easy to run on any machine with Docker installed.
