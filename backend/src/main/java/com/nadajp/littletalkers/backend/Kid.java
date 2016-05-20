package com.nadajp.littletalkers.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;


@Entity
public class Kid
{
    /**
     * Use automatic id assignment for entities of Kid class
     */
    //@Id
    //private long id;

    /**
     * Holds Profile key as the parent.
     */
    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<UserProfile> parentKey;

    /*
    @Index
    private long userId;

    /**
     * Id corresponding to the local sql database
     */
    @Id
    private long id;

    // kid's name
    @Index
    private String name;
    // kid's birthdate
    private long birthdate;
    // default location
    private String location;
    // default language
    private String language;
    // local uri of the profile picture
    private String pictureUri;

    public Kid() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(long birthdate) {
        this.birthdate = birthdate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public void setParentId(long userId)
    {
        this.parentKey = Key.create(UserProfile.class, userId);
    }

    public void setParentKey(Key<UserProfile> parent)
    {
        this.parentKey = parent;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<UserProfile> getParentKey()
    {
        return parentKey;
    }
}
