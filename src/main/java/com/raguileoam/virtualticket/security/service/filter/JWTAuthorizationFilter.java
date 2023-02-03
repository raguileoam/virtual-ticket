package com.raguileoam.virtualticket.security.service.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.raguileoam.virtualticket.security.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    /**
     * It validates the JWT token.
     * Valida del token JWT.
     *
     * @param request  Servlet container.
     *                 Contenedor de servlet.
     * @param response Servlet container.
     *                 Contenedor de servlet.
     * @param chain    It gives a view into the invocation chain of a filtered
     *                 request for a resource.
     *                 Proporciona una vista de la cadena de invocación de una
     *                 solicitud filtrada de un recurso.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String header = request.getHeader(JWTService.HEADER);
        if (!this.requireAutenticacion(header)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

        if (this.jwtService.validateToken(header)) {
            usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    this.jwtService.getNombreFromToken(header),
                    null,
                    this.jwtService.getRolesFromToken(header));
        }

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(request, response);
    }

    /**
     * It deteminates if a JWT token header requires authentication.
     * Determina si una cabezera de un token JWT requiere autentificación.
     *
     * @param header Token header.
     *               Cabezera del token.
     * @return Boolean result. Resultado booleano.
     */
    protected boolean requireAutenticacion(String header) {
        return header != null && header.startsWith(JWTService.TOKEN_PREFIX);
    }
}
