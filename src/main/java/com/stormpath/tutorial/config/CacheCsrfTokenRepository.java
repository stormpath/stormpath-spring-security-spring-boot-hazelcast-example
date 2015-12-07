package com.stormpath.tutorial.config;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.config.CookieConfig;
import com.stormpath.sdk.servlet.http.CookieSaver;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

/**
 * Spring Cache-based implementation of the {@link CsrfTokenRepository} interface.  To ensure correct
 * behavior, the specified TTL <em>MUST</em> be the equal to or greater than the specified cache's TTL.
 */
public class CacheCsrfTokenRepository implements CsrfTokenRepository {

    private static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";

    private static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";

    private static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";

    private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;

    private String headerName = DEFAULT_CSRF_HEADER_NAME;

    private static final Logger log = LoggerFactory.getLogger(CacheCsrfTokenRepository.class);

    private final Cache nonceCache;
    private final String signingKey;
    private final long ttlMillis;

    /**
     * Creates a new instance.
     *
     * @param nonceCache a cache to place used CSRF tokens.  This is required to ensure the consumed token is never used
     *                   again, which is mandatory for CSRF protection.  This cache <em>MUST</em> have a TTL value equal
     *                   to or greater than {@code ttlMillis}. Cache key: a unique token ID, Cache value: the used
     *                   token
     * @param signingKey a base64-encoded (and hopefully secure-random) cryptographic signing key used to digitally sign the CSRF token to
     *                   ensure it cannot be tampered with by HTTP clients.
     * @param ttlMillis  the length of time in milliseconds for which a generated CSRF token is valid.  When a token is
     *                   created, it cannot be used after this duration, even if it has not been consumed yet.
     */
    public CacheCsrfTokenRepository(Cache nonceCache, String signingKey, long ttlMillis) {
        Assert.notNull(nonceCache, "nonce cache cannot be null.");
        this.nonceCache = nonceCache;
        Assert.hasText(signingKey, "signingKey cannot be null or empty.");
        this.signingKey = signingKey;
        Assert.isTrue(ttlMillis > 0, "ttlMillis must be greater than zero.");
        this.ttlMillis = ttlMillis;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {

        String id = UUID.randomUUID().toString().replace("-", "");

        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + ttlMillis);

        String token = Jwts.builder().setId(id).setIssuedAt(now).setNotBefore(now).setExpiration(exp)
            .signWith(SignatureAlgorithm.HS256, signingKey).compact();

        return new DefaultCsrfToken(headerName, parameterName, token);
    }

    protected CookieConfig getCookieConfig() {
        return new DefaultCookieConfig(DEFAULT_CSRF_COOKIE_NAME);
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        CookieSaver saver = new CookieSaver(getCookieConfig());
        if (token == null) {
            saver.set(request, response, null);
            return;
        }

        saver.set(request, response, token.getToken());
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {

        String token = Strings.clean(request.getParameter(parameterName));

        if (token == null) {
            token = Strings.clean(request.getHeader(headerName));
        }

        if (token == null) {
            return null;
        }

        try {
            Jws<Claims> jws = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);

            //signature is valid, now let's ensure it hasn't been submitted before:

            String id = jws.getBody().getId();

            String usedNonce = null;

            Cache.ValueWrapper wrapper = nonceCache.get(id);
            if (wrapper != null) {
                Object val = wrapper.get();
                if (val != null) {
                    usedNonce = val.toString();
                }
            }

            if (usedNonce == null) {
                //CSRF token hasn't been used yet, mark it as used:
                nonceCache.put(id, token);

                return new DefaultCsrfToken(headerName, parameterName, token);
            }
        } catch (Exception e) {
            log.debug("CSRF token is invalid (this is likely to happen and not necessarily an error condition).", e);
        }

        return null;
    }
}