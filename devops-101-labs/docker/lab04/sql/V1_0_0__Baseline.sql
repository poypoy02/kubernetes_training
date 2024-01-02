CREATE TABLE IF NOT EXISTS public."product_catalog"
(
    id character varying(128),
    name character varying(256),
    description character varying(512),
    price numeric(32, 3),
    PRIMARY KEY (id)
);

ALTER TABLE public."product_catalog"
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public."order"
(
    id character varying(128),
    order_date date,
    total numeric(32, 3),
    user_id uuid,
    PRIMARY KEY (id)
);

ALTER TABLE public."order"
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public."user"
(
    id character varying(128),
    username character varying(64) NOT NULL,
    password character varying(128) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE public."user"
    OWNER to postgres;