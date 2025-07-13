package br.edu.ifrs.tads.ppa.event;

import java.util.List;

import br.edu.ifrs.tads.ppa.model.Profile;

public record NewUserEvent(
        String name,
        String handle,
        String email,
        String password,
        String company,
        Profile.AccountType type,
        List<String> roles
)  {

}
