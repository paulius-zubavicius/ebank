
-- DROP TABLE public.client;
CREATE TABLE public.client
(
    id bigserial NOT NULL,
    version integer NOT NULL,
    email character varying COLLATE pg_catalog."default" NOT NULL,
    password_hash character varying(100) COLLATE pg_catalog."default" NOT NULL,
    balance numeric(10,2) NOT NULL,
    CONSTRAINT client_pkey PRIMARY KEY (id),
    CONSTRAINT client_email_key UNIQUE (email)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.client
    OWNER to postgres;
    
-- DROP TABLE public.operation;
CREATE TABLE public.operation
(
    id bigserial NOT NULL,
    client_id bigint NOT NULL,
    type character varying(20) COLLATE pg_catalog."default" NOT NULL,
    amount numeric(10,2) NOT NULL,
    "timestamp" timestamp with time zone NOT NULL,
    balance_before numeric(10,2) NOT NULL,
    CONSTRAINT operation_pkey PRIMARY KEY (id),
    CONSTRAINT operation_client_id_fkey FOREIGN KEY (client_id)
        REFERENCES public.client (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.operation
    OWNER to postgres;
    
-- DROP TABLE public.session;
CREATE TABLE public.session
(
    id bigserial NOT NULL,
    client_id bigint NOT NULL,
    last_touch timestamp without time zone NOT NULL,
    token character varying(100) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT session_pkey PRIMARY KEY (id),
    CONSTRAINT session_client_id_fkey FOREIGN KEY (client_id)
        REFERENCES public.client (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.session
    OWNER to postgres;
