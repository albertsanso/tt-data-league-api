package org.cttelsamicsterrassa.data.api.runtime.config.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@JsonIgnore
    @JsonProperty("id")
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 255)
    @JsonProperty("username")
    private String username;
    
    @Column(name = "password", nullable = false, length = 255)
    @JsonProperty("password")
    private String password;
}
