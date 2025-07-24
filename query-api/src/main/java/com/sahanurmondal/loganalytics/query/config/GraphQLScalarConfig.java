package com.sahanurmondal.loganalytics.query.config;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.util.Map;

/**
 * GraphQL configuration for custom scalar types.
 */
@Configuration
public class GraphQLScalarConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(GraphQLScalarType.newScalar()
                        .name("JSON")
                        .description("A custom scalar that handles JSON objects")
                        .coercing(new Coercing<Object, Object>() {
                            @Override
                            public Object serialize(Object dataFetcherResult) throws CoercingSerializeException {
                                return dataFetcherResult;
                            }

                            @Override
                            public Object parseValue(Object input) throws CoercingParseValueException {
                                return input;
                            }

                            @Override
                            public Object parseLiteral(Object input) throws CoercingParseLiteralException {
                                return input;
                            }
                        })
                        .build())
                .scalar(GraphQLScalarType.newScalar()
                        .name("Long")
                        .description("A custom scalar that handles Long values")
                        .coercing(new Coercing<Long, Long>() {
                            @Override
                            public Long serialize(Object dataFetcherResult) throws CoercingSerializeException {
                                if (dataFetcherResult instanceof Number) {
                                    return ((Number) dataFetcherResult).longValue();
                                }
                                if (dataFetcherResult instanceof String) {
                                    return Long.parseLong((String) dataFetcherResult);
                                }
                                throw new CoercingSerializeException("Expected a Long value");
                            }

                            @Override
                            public Long parseValue(Object input) throws CoercingParseValueException {
                                if (input instanceof Number) {
                                    return ((Number) input).longValue();
                                }
                                if (input instanceof String) {
                                    return Long.parseLong((String) input);
                                }
                                throw new CoercingParseValueException("Expected a Long value");
                            }

                            @Override
                            public Long parseLiteral(Object input) throws CoercingParseLiteralException {
                                return parseValue(input);
                            }
                        })
                        .build());
    }
}
