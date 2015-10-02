package com.nadajp.littletalkers.model;

public class Word 
{
   //private variables
   int id;
   String word;
   int kidId;
   String language;
   String date;
   String location;
   String translation;
   String towhom;
   String notes;
   String audioFileUri;
     
   // Empty constructor
   public Word()
   {}
    
   // constructor
   public Word(int id, String word, int kidId, String language, String date, String location, 
    		    String translation, String towhom, String notes)
   {
      this.id = id;
      this.word = word;
      this.kidId = kidId;
      this.language = language;
      this.date = date;
      this.location = location;
      this.translation = translation;
      this.towhom = towhom;
      this.notes = notes;
   }
     
   // getting ID
   public int getID()
   {
      return this.id;
   }
     
   // setting id
   public void setID(int id)
   {
      this.id = id;
   }
     
   // getting word
   public String getWord()
   {
      return this.word;
   }
     
   // setting word
   public void setWord(String word)
   {
      this.word = word;
   }
     
   // getting kid id
   public int getKidId()
   {
      return this.kidId;
   }
     
   // setting kid id
   public void setKidId(int kidId)
   {
      this.kidId = kidId;
   }
}
