package com.stefano.pedidos.util;

public class PedidoContantes {

    private PedidoContantes() {
        // Private constructor to prevent instantiation
    }

    public static final String PERSON_ID_TOKEN = "personId";
    public static final String ROLES_TOKEN = "roles";

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String SESSION_ID_HEADER = "X-Session-ID";

    public static final String REQUEST_ID_MDC = "requestId";
    public static final String PERSON_ID_MDC = "personId";
    public static final String CORRELATION_ID_MDC = "correlationId";
    public static final String SESSION_ID_MDC = "sessionId";
}
