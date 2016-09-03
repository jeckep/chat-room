package com.jeckep.chat.env;

public class Envs {
    public static final String DB_NAME = System.getenv("DB_NAME");
    public static final String DB_URL = System.getenv("DB_URL");
    public static final String DB_USER = System.getenv("DB_USER");
    public static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    public static final String LOG_LEVEL = System.getenv("LOG_LEVEL");

    public static final String GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY");
    public static final String GOOGLE_API_SECRET = System.getenv("GOOGLE_API_SECRET");
}
