CREATE TABLE IF NOT EXISTS users
(
    user_id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_name   VARCHAR(255)                            NOT NULL,
    email       VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id),
    CONSTRAINT uc_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    category_name VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_categories PRIMARY KEY (category_id),
    CONSTRAINT uc_categories_cat_name UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS events
(
    event_id           BIGINT GENERATED BY DEFAULT AS IDENTITY   NOT NULL,
    annotation         VARCHAR(2000)                             NOT NULL,
    category_id        BIGINT                                    NOT NULL,
    confirmed_requests BIGINT                      DEFAULT 0     NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    description        VARCHAR(7000)                             NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE               NOT NULL,
    initiator_id       BIGINT                                    NOT NULL,
    latitude           DOUBLE PRECISION                          NOT NULL,
    longitude          DOUBLE PRECISION                          NOT NULL,
    paid               boolean                                   NOT NULL,
    participant_limit  integer                     DEFAULT 0     NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation boolean                     DEFAULT TRUE  NOT NULL,
    state              VARCHAR(255)                              NOT NULL,
    title              VARCHAR(120)                              NOT NULL,
    CONSTRAINT pk_events PRIMARY KEY (event_id),
    CONSTRAINT fk_events_on_cat FOREIGN KEY (category_id) REFERENCES categories (category_id),
    CONSTRAINT fk_events_on_initiator FOREIGN KEY (initiator_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS requests
(
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    request_date TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    user_id      BIGINT                                  NOT NULL,
    status       VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (request_id),
    CONSTRAINT fk_requests_on_event FOREIGN KEY (event_id) REFERENCES events (event_id),
    CONSTRAINT fk_requests_on_user FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    comp_id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned  boolean                   DEFAULT FALSE NOT NULL,
    title   VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_compilations PRIMARY KEY (comp_id)
);

CREATE TABLE IF NOT EXISTS compilations_events
(
        comp_id  BIGINT NOT NULL,
        event_id BIGINT NOT NULL,
        CONSTRAINT pk_compilations_events PRIMARY KEY (comp_id, event_id),
        CONSTRAINT fk_comeve_on_compilation FOREIGN KEY (comp_id) REFERENCES compilations (comp_id),
        CONSTRAINT fk_comeve_on_event FOREIGN KEY (event_id) REFERENCES events (event_id)
);