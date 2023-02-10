package com.raguileoam.virtualticket.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.raguileoam.virtualticket.security.model.Account;
import com.raguileoam.virtualticket.security.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * It finds a user by username.
     * Localiza al usuario en función del nombre de usuario.
     *
     * @param s User's e-mail address. La direccion de e-mail del usuario.
     * @return A fully populated user record. Un registro de usuario completamente
     *         poblado.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Account user = accountRepository.findByEmail(s);

        if (user == null)
            throw new UsernameNotFoundException("Usuario no encontrado");

        List<GrantedAuthority> authorities = getAuthorities(user);

        if (authorities.isEmpty())
            throw new UsernameNotFoundException("El usuario no tiene roles asignados");

        return new User(user.getEmail(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }

    /**
     * It gets authorities of a specific user.
     * Obtiene los roles de un usuario especifico.
     *
     * @param account account.
     *                Usuario.
     * @return Authorities of this user.
     */
    private List<GrantedAuthority> getAuthorities(Account account) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(account.getRol().name()));
        return authorities;
    }
}
