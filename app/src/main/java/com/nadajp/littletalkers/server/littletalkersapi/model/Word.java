/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-03-26 20:30:19 UTC)
 * on 2015-05-27 at 16:25:17 UTC 
 * Modify at your own risk.
 */

package com.nadajp.littletalkers.server.littletalkersapi.model;

/**
 * Model definition for Word.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the littletalkersapi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class Word extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String audioFileUri;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long date;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long kidId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String language;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String location;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String notes;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long parentId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String toWhom;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String translation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String word;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getAudioFileUri() {
    return audioFileUri;
  }

  /**
   * @param audioFileUri audioFileUri or {@code null} for none
   */
  public Word setAudioFileUri(java.lang.String audioFileUri) {
    this.audioFileUri = audioFileUri;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getDate() {
    return date;
  }

  /**
   * @param date date or {@code null} for none
   */
  public Word setDate(java.lang.Long date) {
    this.date = date;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public Word setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getKidId() {
    return kidId;
  }

  /**
   * @param kidId kidId or {@code null} for none
   */
  public Word setKidId(java.lang.Long kidId) {
    this.kidId = kidId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLanguage() {
    return language;
  }

  /**
   * @param language language or {@code null} for none
   */
  public Word setLanguage(java.lang.String language) {
    this.language = language;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLocation() {
    return location;
  }

  /**
   * @param location location or {@code null} for none
   */
  public Word setLocation(java.lang.String location) {
    this.location = location;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getNotes() {
    return notes;
  }

  /**
   * @param notes notes or {@code null} for none
   */
  public Word setNotes(java.lang.String notes) {
    this.notes = notes;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getParentId() {
    return parentId;
  }

  /**
   * @param parentId parentId or {@code null} for none
   */
  public Word setParentId(java.lang.Long parentId) {
    this.parentId = parentId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getToWhom() {
    return toWhom;
  }

  /**
   * @param toWhom toWhom or {@code null} for none
   */
  public Word setToWhom(java.lang.String toWhom) {
    this.toWhom = toWhom;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getTranslation() {
    return translation;
  }

  /**
   * @param translation translation or {@code null} for none
   */
  public Word setTranslation(java.lang.String translation) {
    this.translation = translation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getWord() {
    return word;
  }

  /**
   * @param word word or {@code null} for none
   */
  public Word setWord(java.lang.String word) {
    this.word = word;
    return this;
  }

  @Override
  public Word set(String fieldName, Object value) {
    return (Word) super.set(fieldName, value);
  }

  @Override
  public Word clone() {
    return (Word) super.clone();
  }

}
