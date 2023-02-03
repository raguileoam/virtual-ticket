package com.raguileoam.virtualticket.security.service.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raguileoam.virtualticket.security.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JWTService jwtService;

    public JWTAuthenticationFilter() {
    }

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
    }

    /**
     * It attempts authenticate an user.
     * Intenta autenticar a un usuario.
     *
     * @param request  It contains the credentials. Contiene las credenciales.
     * @param response Response, it needed in layering authentication cases.
     *                 Respuesta, necesaria en casos de autentificación por capas.
     * @return Authenticated user's token. Token del usuario autentificado.
     *         <p>
     *         Para enviar los datos por form-data usar username y password.
     *         Para enviar los datos por Raw usar campos nombre y clave.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = this.obtainUsername(request);
        String password = this.obtainPassword(request);

        if (username != null && password != null) {
            username = username.trim();
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
            return this.authenticationManager.authenticate(token);

        } else {
            return null;
        }
    }

    /**
     * It builds the response JSON, after a successful request.
     * Creación del JSON respuesta a la petición de autentificación exitosa.
     *
     * @param request    Servlet container.
     *                   Contenedor de servlet.
     * @param response   Servlet container.
     *                   Contenedor de servlet.
     * @param chain      It gives a view into the invocation chain of a filtered
     *                   request for a resource.
     *                   Proporciona una vista de la cadena de invocación de una
     *                   solicitud filtrada de un recurso.
     * @param authResult It represents the token for an authentication request.
     *                   Representa el token para una solicitud de autenticacion.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
            throws IOException, ServletException {
        String token = this.jwtService.createToken(authResult);
        response.addHeader(JWTService.HEADER, JWTService.TOKEN_PREFIX + token);

        Map<String, Object> body = addValuesToSuccessfulBody(token, (User) authResult.getPrincipal());

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    /**
     * It adds values to body of successfulAuthentication.
     * Agrega valores al body de successfulAuthentication.
     *
     * @param token Restoration token value.
     *              Valor del token de restauracion.
     * @param user  Actual user.
     *              Usuario actual.
     * @return Map that represents the body of successfulAuthentification. Map que
     *         representa el body de successfulAuthentification.
     */
    private Map<String, Object> addValuesToSuccessfulBody(String token, User user) {
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("user", user);
        body.put("mensaje", "Has iniciado sesión con éxito");
        return body;
    }

    /**
     * It builds the response JSON, after a unsuccessful request.
     * Creación del JSON respuesta a la petición de autentificación fallida.
     *
     * @param request  Servlet container.
     *                 Contenedor de servlet.
     * @param response Servlet container.
     *                 Contenedor de servlet.
     * @param failed   It represents an authentication exception.
     *                 Representa una excepcion en la autentificacion.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed)
            throws IOException, ServletException {
        Map<String, Object> body = addValuesToFailureBody(failed);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
    }

    /**
     * It adds values to body of unsuccessfulAuthentication.
     * Agrega valores al body de unsuccessfulAuthentication.
     *
     * @param failed It represents the authentication error.
     *               Representa el error de autentificacion.
     * @return Map that represents the body of unsuccessfulAuthentification. Map que
     *         representa el body de unsuccessfulAuthentification.
     */
    private Map<String, Object> addValuesToFailureBody(AuthenticationException failed) {
        Map<String, Object> body = new HashMap<>();
        body.put("mensaje", "Credenciales inválidas");
        body.put("error", failed.getMessage());
        return body;
    }

}
