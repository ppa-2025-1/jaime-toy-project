package br.edu.ifrs.tads.ppa.repository;

import br.edu.ifrs.tads.ppa.model.Role;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Collection;
import java.util.Set;

public interface RoleRepository extends ListCrudRepository<Role, Integer> {

    Role findByName(String name);

    Set<Role> findByNameIn(Collection<String> names);

    boolean existsByName(String name);
}
