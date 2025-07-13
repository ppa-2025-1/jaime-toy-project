package br.edu.ifrs.tads.ppa.service;

import br.edu.ifrs.tads.ppa.dto.NewUserDTO;
import br.edu.ifrs.tads.ppa.model.Profile;
import br.edu.ifrs.tads.ppa.model.Role;
import br.edu.ifrs.tads.ppa.model.User;
import br.edu.ifrs.tads.ppa.repository.RoleRepository;
import br.edu.ifrs.tads.ppa.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final Set<String> defaultRoles = Set.of("ROLE_USER", "ROLE_GUEST", "ROLE_VIEWER");

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void createUser(NewUserDTO newUserDTO) {
        if (userRepository.findByEmail(newUserDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Usuário com o email " + newUserDTO.email() + " já existe");
        }
        // Se o handle for nulo, será gerado um automaticamente
        if (newUserDTO.handle() != null && userRepository.findByHandle(newUserDTO.handle()).isPresent()) {
            throw new IllegalArgumentException("Usuário com o nome " + newUserDTO.handle() + " já existe");
        }

        User user = new User();
        user.setEmail(newUserDTO.email());
        user.setHandle(newUserDTO.handle() != null ? newUserDTO.handle() : generateHandle(newUserDTO.email()));
        user.setPassword(passwordEncoder.encode(newUserDTO.password()));

        // Atribuir papéis padrão e adicionais
        Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(defaultRoles));
        Set<Role> additionalRoles = roleRepository.findByNameIn(newUserDTO.roles());
        if (additionalRoles.size() != newUserDTO.roles().size()) {
            throw new IllegalArgumentException("Alguns papéis não existem");
        }
        roles.addAll(additionalRoles);

        if (roles.isEmpty()) {
            throw new IllegalArgumentException("O usuário deve ter pelo menos um papel");
        }
        user.setRoles(roles);

        // Criar e associar o perfil
        Profile profile = new Profile();
        profile.setName(newUserDTO.name());
        profile.setCompany(newUserDTO.company());
        profile.setType(newUserDTO.type() != null ? newUserDTO.type() : Profile.AccountType.FREE);
        profile.setUser(user);
        user.setProfile(profile);

        userRepository.save(user);
    }

    private String generateHandle(String email) {
        String baseHandle = email.split("@")[0];
        String handle = baseHandle;
        int i = 1;
        while (userRepository.existsByHandle(handle)) {
            handle = baseHandle + i++;
        }
        return handle;
    }

    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }
}
