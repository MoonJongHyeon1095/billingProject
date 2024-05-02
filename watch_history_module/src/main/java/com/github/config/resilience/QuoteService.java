package com.github.config.resilience;

public interface QuoteService {
    int getQuote();

    int getQuoteFallback(Throwable t);
}
