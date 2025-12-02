package com.foodya.foodya_backend.auth.dto;

import com.foodya.foodya_backend.user.model.User.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
  @NotBlank (message = "Username is required")
  private String username;
  @NotBlank (message = "Email is required")
  private String email;
  @NotBlank (message = "Password is required")
  private String password;
  @NotBlank (message = "Fullname is required")
  private String fullname;
  @NotBlank (message = "Phone number is required")
  private String phone;
  private Role role;

  // setter/getter
  public void setPhoneNumber(String phone) {
    this.phone = phone;
  }
  public String getPhoneNumber() {
    return this.phone;
  }

  public void setFullName(String fullname) {
    this.fullname = fullname;
  }
  public String getFullName() {
    return this.fullname;
  }

  public void setIsActive(Boolean isActive) {
    // do nothing, placeholder for compatibility
  }


}
