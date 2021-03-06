package org.openkilda.wfm.topology.stats;

import org.neo4j.driver.v1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The type Cypher executor.
 */
public class CypherExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CypherExecutor.class);
    private final Driver driver;

    /**
     * Instantiates a new Cypher executor.
     *
     * @param url the url
     */
    public CypherExecutor(String url) {
        this(url, null, null);
    }

    /**
     * Instantiates a new Cypher executor.
     *
     * @param url      the url
     * @param username the username
     * @param password the password
     */
    public CypherExecutor(String url, String username, String password) {
        boolean hasPassword = password != null && !password.isEmpty();
        AuthToken token = hasPassword ? AuthTokens.basic(username, password) : AuthTokens.none();
        logger.debug("connecting to neo4j at " + url);
        driver = GraphDatabase.driver(url, token,
                Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig());
    }

    /**
     * Returns an iterator of the cypher query dependent upon the type of result.
     *
     * @param query  the query
     * @param params the params
     * @return the iterator
     */
    public Iterator<Map<String, Object>> query(String query, Map<String, Object> params) {
        try (Session session = driver.session()) {
            List<Map<String, Object>> list = session.run(query, params)
                    .list(r -> r.asMap(CypherExecutor::convert));
            return list.iterator();
        }
    }

    /**
     * Converts the result of a cypher query for well know result types.
     *
     * @param value
     * @return the object of the query results
     */
    private static Object convert(Value value) {
        switch (value.type().name()) {
            case "PATH":
                return value.asList(CypherExecutor::convert);
            case "NODE":
            case "RELATIONSHIP":
                return value.asMap();
        }
        return value.asObject();
    }
}
