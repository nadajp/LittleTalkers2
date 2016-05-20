package com.nadajp.littletalkers.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class UserProfile
{
  /**
   * The id for the datastore key.
   *
   * Use automatic id assignment for entities of Profile class.
   */
  @Id
  private Long id;

  @Index
  private String email;

  /**
   * Empty public constructor for Profile.
   */
  public UserProfile()
  {
  }

  /**
   * Public constructor for Profile.
   * @param email User's main e-mail address.
   */
  public UserProfile(String email)
  {
    this.email = email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getEmail()
  {
    return this.email;
  }

  public Long getId()
  {
    return this.id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }
}
