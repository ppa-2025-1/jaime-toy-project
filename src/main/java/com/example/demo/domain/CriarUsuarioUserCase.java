package com.example.demo.domain;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

import com.example.demo.domain.dto.NewUser;
import com.example.demo.domain.out.IPasswordEncoder;
import com.example.demo.domain.out.IRoleRepository;
import com.example.demo.domain.out.IUserRepository;
import com.example.demo.repository.ChamadoRepository;
import com.example.demo.repository.entity.Chamado;
import com.example.demo.repository.entity.Profile;
import com.example.demo.repository.entity.Role;
import com.example.demo.repository.entity.User;

// HIGH-LEVEL POLICY: políticas de alto nível

// classe que representa o negócio
@UseCase // marcar como um Bean de Negócio
public class CriarUsuarioUserCase {
    
    // dependências para o lado de fora do domain
    private IUserRepository userRepository;
    private IRoleRepository roleRepository;
    private IPasswordEncoder passwordEncoder;
    private final ChamadoRepository chamadoRepository;
    // ------------------------------------------

    private Set<String> defaultRoles;

    public CriarUsuarioUserCase(
            IUserRepository userRepository, 
            IRoleRepository roleRepository,
            IPasswordEncoder passwordEncoder,
            @Value("${app.user.default.roles}") Set<String> defaultRoles,
            ChamadoRepository chamadoRepository) {
                
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;   
                this.passwordEncoder = passwordEncoder;
                this.defaultRoles = defaultRoles;
                this.chamadoRepository = chamadoRepository;
    }

    // contém toda a lógica de criação de usuário
    public void criarUsuario(NewUser newUser) {
        // BUSINESS RULES
        // DOMAIN RULES
        // if (newUser.email() == null || newUser.password() == null) {
        //     throw new IllegalArgumentException("Email e senha são obrigatórios");
        // }

        // if (newUser.email().isEmpty() || newUser.password().isEmpty()) {
        //     throw new IllegalArgumentException("Email e senha não podem estar vazios");
        // }

        if (!newUser.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email não é válido");
        }

        if (!newUser.password().matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$")) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres e conter pelo menos uma letra e um número");
        }
        
        userRepository.findByEmail(newUser.email())
            .ifPresent(user -> {
                throw new IllegalArgumentException("Usuário com o email " + newUser.email() + " já existe");
            });

        userRepository.findByHandle(newUser.handle())
            .ifPresent(user -> {
                throw new IllegalArgumentException("Usuário com o nome " + newUser.handle() + " já existe");
            });

        User user = new User();
        
        user.setEmail(newUser.email());
        user.setHandle(newUser.handle() != null ? newUser.handle() : generateHandle(newUser.email()));
        user.setPassword(passwordEncoder.encode(newUser.password()));
        
        Set<Role> roles = new HashSet<>();
        
        roles.addAll(roleRepository.findByNameIn(defaultRoles));

        Set<Role> additionalRoles = roleRepository.findByNameIn(newUser.roles());
        if (additionalRoles.size() != newUser.roles().size()) {
            throw new IllegalArgumentException("Alguns papéis não existem");
        }

        if (roles.isEmpty()) {
            throw new IllegalArgumentException("O usuário deve ter pelo menos um papel");
        }

        user.setRoles(roles);

        Profile profile = new Profile();
        
        profile.setName(newUser.name());
        profile.setCompany(newUser.company());
        profile.setType(newUser.type() != null ? newUser.type() : Profile.AccountType.FREE);

        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user); 

        User savedUser = userRepository.save(user);

        Chamado chamado = new Chamado();
        chamado.setAcao("CRIAR");
        chamado.setObjeto("E-MAIL");
        chamado.setDetalhamento(savedUser.getHandle() + "@tads.rg.ifrs.edu.br\nCriar e-mail para novo usuário.");
        chamado.setSituacao("NOVO");
        chamado.setUsuario(savedUser);
        
        chamadoRepository.save(chamado);
    }

    private String generateHandle(String email) {
        String[] parts = email.split("@");
        String handle = parts[0];
        int i = 1;
        while (userRepository.existsByHandle(handle)) {
            handle = parts[0] + i++;
        }
        return handle;
    }

}
