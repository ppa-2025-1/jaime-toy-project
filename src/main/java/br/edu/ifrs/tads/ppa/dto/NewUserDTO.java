package br.edu.ifrs.tads.ppa.dto;

import br.edu.ifrs.tads.ppa.model.Profile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewUserDTO(
    @NotBlank(message = "O nome é obrigatório")
    String name,
    
    String handle,
    
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email não é válido")
    String email,
    
    @NotBlank(message = "A senha é obrigatória")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$",
             message = "A senha deve ter pelo menos 8 caracteres e conter uma letra e um número")
    String password,
    
    String company,
    
    Profile.AccountType type,
    
    @Size(min = 1, message = "O usuário deve ter pelo menos um papel")
    List<String> roles
) {}
