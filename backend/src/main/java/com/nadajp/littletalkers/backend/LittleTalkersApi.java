package com.nadajp.littletalkers.backend;

/**
 * Created by nadajp on 5/19/16.
 */

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;

import java.util.List;

import javax.inject.Named;

import static com.nadajp.littletalkers.backend.service.OfyService.ofy;

@Api(name = "littleTalkersApi",
        version = "v1",
        scopes = {Constants.EMAIL_SCOPE}, clientIds = {
        Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID,
        Constants.ANDROID_CLIENT_ID}, audiences = {Constants.ANDROID_AUDIENCE}, description = "LittleTalkers API",
        namespace = @ApiNamespace(ownerDomain = "backend.littlealkers.nadajp.com",
                ownerName = "backend.littletalkers.nadajp.com",
                packagePath = ""))

public class LittleTalkersApi {
    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     *
     * @param id the primary key of the entity
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "getProfileById")
    public UserProfile getProfileById(final User user, @Named("id") Long id)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        if (id == null) {
            return insertProfile(user);
        }
        UserProfile profile = ofy().load().key(Key.create(UserProfile.class, id)).now();
        if (profile == null) {
            return insertProfile(user);
        }
        return profile;
    }

    /**
     * This method gets the entity from email address in the user parameter.
     * It uses HTTP GET method.
     *
     * @return The entity with email address user.getEmail()
     */
    @ApiMethod(name = "getProfile")
    public UserProfile getProfile(final User user)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        String email = user.getEmail();

        UserProfile profile = ofy().load().type(UserProfile.class).filter("email", email).first().now();
        if (profile == null) {
            profile = new UserProfile(email);
        }
        return profile;
    }

    private Long getIdFromEmail(String email) {
        UserProfile user = ofy().load().type(UserProfile.class).filter("email", email).first().now();
        return user.getId();
    }

    /**
     * This inserts a new UserProfile into App Engine datastore. If the user already
     * exists in the datastore, an exception is thrown. It uses HTTP POST method.
     *
     * @param profile the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertProfile")
    public UserProfile insertProfile(final User user)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        // try to load a user profile with this email address and if it exists, return it;
        UserProfile profile = ofy().load().type(UserProfile.class).filter("email", user.getEmail()).first().now();
        if (profile != null) {
            return profile;
        }
        // if profile does not exist for this email address, create it and save it
        profile = new UserProfile(user.getEmail());
        ofy().save().entity(profile).now();
        return profile;
    }

    /**
     * This method is used for updating an existing UserProfile. If the entity does
     * not exist in the datastore, an exception is thrown. It uses HTTP PUT
     * method.
     *
     * @param profile - the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateProfile")
    public UserProfile updateProfile(UserProfile profile) throws EntityNotFoundException {
        ofy().save().entity(profile).now();
        return profile;
    }

    /**
     * This method removes the entity with primary key id. It uses HTTP DELETE
     * method.
     *
     * @param id - the primary key of the entity to be deleted.
     */
    @ApiMethod(name = "removeProfile")
    public void removeProfile(@Named("id") String id) {
        // delete all data related to this user, then delete user
    }

    private boolean containsProfile(Long id) {
        if (id == null) {
            return false;
        }
        UserProfile profile = ofy().load().type(UserProfile.class).id(id).now();
        if (profile == null) {
            return false;
        }
        return true;
    }

    /**
     * This inserts a new Kid into App Engine datastore. If the entity already
     * exists in the datastore, an exception is thrown. It uses HTTP POST method.
     *
     * @param kid the entity to be inserted.
     * @return The inserted entity.
     */
    @ApiMethod(name = "insertKid")
    public com.nadajp.littletalkers.backend.Kid insertKid(User user, Kid kid) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        ofy().save().entity(kid).now();
        return kid;
    }

    /**
     * This method gets the entity having primary key id. It uses HTTP GET
     * method.
     *
     * @param userId: id of the user (parent)
     * @param id:     id of the child
     * @return The entity with primary key id.
     */
    @ApiMethod(name = "getKid")
    public com.nadajp.littletalkers.backend.Kid getKid(User user, @Named("userId") Long userId, @Named("id") Long id) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        Key<UserProfile> parentKey = Key.create(UserProfile.class, userId);
        com.nadajp.littletalkers.backend.Kid kid = ofy().load().type(Kid.class).parent(parentKey).id(id).now();
        return kid;
    }

    /**
     * This inserts a new entity into App Engine datastore. If the entity already
     * exists in the datastore, an exception is thrown. It uses HTTP POST method.
     *
     * @param data   - all of user's data entities to be inserted
     * @param userId
     * @return The inserted entities wrapped in UserDataWrapper class.
     */
    @ApiMethod(name = "insertUserData")
    public UserDataWrapper insertUserData(final User user, @Named("userId") Long userId, final UserDataWrapper data)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        Key<UserProfile> parentKey = Key.create(UserProfile.class, userId);

        List<com.nadajp.littletalkers.backend.Kid> kids = data.getKids();
        if (kids != null) {
            for (com.nadajp.littletalkers.backend.Kid kid : kids) {
                kid.setParentId(userId);
                //kid.setParentKey(parentKey);
            }
            ofy().save().entities(kids).now();
            kids = ofy().load().type(com.nadajp.littletalkers.backend.Kid.class).ancestor(parentKey).list();
        }
        List<com.nadajp.littletalkers.backend.Word> words = data.getWords();
        if (words != null) {
            for (com.nadajp.littletalkers.backend.Word word : words) {
                word.setParentId(userId);
            }
            ofy().save().entities(words).now();
            words = ofy().load().type(com.nadajp.littletalkers.backend.Word.class).ancestor(parentKey).list();
        }

        UserDataWrapper result = new UserDataWrapper();
        result.setWords(words);
        result.setKids(kids);

        return result;
    }

    /**
     * @param list of kid entities to be inserted.
     * @return The inserted entities.
     */
    @ApiMethod(name = "getWordsForKid")
    public List<com.nadajp.littletalkers.backend.Word> getWordsForKid(User user, @Named("userId") Long userId,
                                                                      @Named("kidId") long kidId)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        Key<UserProfile> parent = Key.create(UserProfile.class, userId);
        List<com.nadajp.littletalkers.backend.Word> words = ofy().load().type(Word.class).ancestor(parent).filter("kidId", kidId).list();
        return words;
    }

    /**
     * This inserts a word into App Engine datastore. If the entity already
     * exists in the datastore, an exception is thrown. It uses HTTP POST method.
     *
     * @param user id
     * @param word to be inserted
     * @return The inserted entity
     */
    @ApiMethod(name = "insertWord")
    public com.nadajp.littletalkers.backend.Word insertWord(User user, @Named("userId") Long userId,
                                                            final Word word)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        word.setParentId(userId);
        ofy().save().entity(word).now();
        return word;
    }

    /**
     * This retrieves a word from datastore by userId, kidId and word.
     *
     * @param userId
     * @param kidId
     * @param word
     * @return The word entity meeting the criteria
     */
    @ApiMethod(name = "getWordDetails")
    public com.nadajp.littletalkers.backend.Word getWordDetails(User user, @Named("userId") Long userId,
                                                                @Named("kidId") Long kidId,
                                                                @Named("word") String word)
            throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }
        Key<UserProfile> parent = Key.create(UserProfile.class, userId);
        com.nadajp.littletalkers.backend.Word result = ofy().load().type(Word.class).ancestor(parent).filter("kidId", kidId).filter("word", word).first().now();
        return result;
    }

    /**
     * This method is used for updating an existing entity. If the entity does
     * not exist in the datastore, an exception is thrown. It uses HTTP PUT
     * method.
     *
     * @param kid - the entity to be updated.
     * @return The updated entity.
     */
    @ApiMethod(name = "updateKid", httpMethod = ApiMethod.HttpMethod.PUT)
    public com.nadajp.littletalkers.backend.Kid updateKid(Kid kid) {

        return kid;
    }

    /**
     * This method removes the entity with primary key id. It uses HTTP DELETE
     * method.
     *
     * @param id the primary key of the entity to be deleted.
     */
    @ApiMethod(name = "removeKid")
    public void removeKid(@Named("id") Long id) {

    }

    private boolean containsKid(com.nadajp.littletalkers.backend.Kid kid) {

        return false;
    }

    public static class UserDataWrapper {
        private List<com.nadajp.littletalkers.backend.Kid> mKids;
        private List<Word> mWords;

        // Empty default constructor
        public UserDataWrapper() {
        }

        public List<com.nadajp.littletalkers.backend.Kid> getKids() {
            return mKids;
        }

        public void setKids(List<com.nadajp.littletalkers.backend.Kid> kids) {
            this.mKids = kids;
        }

        public List<com.nadajp.littletalkers.backend.Word> getWords() {
            return mWords;
        }

        public void setWords(List<com.nadajp.littletalkers.backend.Word> words) {
            this.mWords = words;
        }
    }
}
